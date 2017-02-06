/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  com.emc.documentum.xml.xproc.XProc
 *  com.emc.documentum.xml.xproc.XProcConfiguration
 *  com.emc.documentum.xml.xproc.io.Resolver
 *  com.emc.documentum.xml.xproc.io.Source
 *  com.emc.documentum.xml.xproc.pipeline.model.PipelineInput
 *  com.emc.documentum.xml.xproc.pipeline.model.PipelineOutput
 *  com.emc.documentum.xml.xproc.pipeline.model.Serialization
 *  com.emc.documentum.xml.xproc.pipeline.model.step.Pipeline
 *  com.emc.documentum.xml.xproc.serialization.SerializationOptions
 *  com.emc.documentum.xml.xproc.serialization.Serializer
 *  com.emc.documentum.xml.xproc.serialization.SerializerFactory
 *  com.emc.documentum.xml.xproc.util.XProcUtil
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package com.emc.documentum.xml.dds.servlet.xproc;

import com.emc.documentum.xml.dds.application.Application;
import com.emc.documentum.xml.dds.application.ServiceManager;
import com.emc.documentum.xml.dds.logging.LogCenter;
import com.emc.documentum.xml.dds.operation.exception.CommitFailedException;
import com.emc.documentum.xml.dds.persistence.Session;
import com.emc.documentum.xml.dds.service.DDSServiceType;
import com.emc.documentum.xml.dds.service.Service;
import com.emc.documentum.xml.dds.service.ServiceType;
import com.emc.documentum.xml.dds.servlet.AbstractDDSHttpServlet;
import com.emc.documentum.xml.dds.servlet.xproc.HttpRequestBasedXProcParameterProvider;
import com.emc.documentum.xml.dds.servlet.xproc.XProcParameterProvider;
import com.emc.documentum.xml.dds.user.User;
import com.emc.documentum.xml.dds.util.SessionPool;
import com.emc.documentum.xml.dds.util.internal.DistributedByteArray;
import com.emc.documentum.xml.dds.util.internal.StringUtils;
import com.emc.documentum.xml.dds.xproc.DDSXProc;
import com.emc.documentum.xml.dds.xproc.XProcService;
import com.emc.documentum.xml.dds.xproc.XProcUtils;
import com.emc.documentum.xml.dds.xproc.internal.TransientWriterResolverHandler;
import com.emc.documentum.xml.xproc.XProc;
import com.emc.documentum.xml.xproc.XProcConfiguration;
import com.emc.documentum.xml.xproc.io.Resolver;
import com.emc.documentum.xml.xproc.io.Source;
import com.emc.documentum.xml.xproc.pipeline.model.PipelineInput;
import com.emc.documentum.xml.xproc.pipeline.model.PipelineOutput;
import com.emc.documentum.xml.xproc.pipeline.model.Serialization;
import com.emc.documentum.xml.xproc.pipeline.model.step.Pipeline;
import com.emc.documentum.xml.xproc.serialization.SerializationOptions;
import com.emc.documentum.xml.xproc.serialization.Serializer;
import com.emc.documentum.xml.xproc.serialization.SerializerFactory;
import com.emc.documentum.xml.xproc.util.XProcUtil;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.namespace.QName;

public class XProcServlet
extends AbstractDDSHttpServlet {
    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        this.doXProc(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) {
        this.doXProc(request, response);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void doXProc(HttpServletRequest request, HttpServletResponse response) {
        DDSXProc xprocIS = null;
        boolean ok = true;
        try {
            XProcParameterProvider parameterProvider = this.getXProcParameterProvider(request);
            Application application = this.getApplication();
            User user = this.getUserFromRequest(request);
            XProcService xprocService = (XProcService)application.getServiceManager().getService(DDSServiceType.XPROC);
            xprocIS = xprocService.newXProc(user, parameterProvider.isReadOnly());
            XProc xproc = xprocIS.getXProc();
            Pipeline pipeline = XProcUtils.newPipeline(xproc, parameterProvider.getPipelineURI());
            PipelineInput pipelineInput = parameterProvider.getPipelineInput();
            PipelineOutput output = XProcUtils.runPipeline(xproc, pipeline, pipelineInput);
            this.processPipelineOutput(xproc, pipeline, output, parameterProvider, request, response);
            this.releaseTransientData(xproc);
        }
        catch (Exception e) {
            ok = false;
            LogCenter.exception("Error while running an XProc pipeline", (Throwable)e);
            this.respondWithFailure(response, 500, e.getMessage());
        }
        finally {
            block12 : {
                try {
                    if (xprocIS != null) {
                        this.releaseISXProc(xprocIS);
                    }
                }
                catch (Exception e) {
                    if (!ok) break block12;
                    LogCenter.exception("Error while running an XProc pipeline", (Throwable)e);
                    this.respondWithFailure(response, 500, e.getMessage());
                }
            }
        }
    }

    private void releaseISXProc(DDSXProc xproc) throws CommitFailedException {
        Collection<Session> openSessions = xproc.getOpenSessions();
        if (openSessions != null) {
            for (Session session : openSessions) {
                session.commit();
            }
        }
        xproc.getSessionPool().clear();
    }

    protected void releaseTransientData(XProc xproc) {
        Object handler = xproc.getXProcConfiguration().getAttribute(TransientWriterResolverHandler.TRANSIENT_HANDLER_INTERNAL_ATTR);
        if (handler instanceof TransientWriterResolverHandler) {
            ((TransientWriterResolverHandler)handler).release();
        }
    }

    protected void processPipelineOutput(XProc xproc, Pipeline pipeline, PipelineOutput output, XProcParameterProvider parameterProvider, HttpServletRequest request, HttpServletResponse response) throws Exception {
        String contentType;
        InputStream is;
        if (parameterProvider.useExternalOutput()) {
            String uri = XProcUtils.getExternalOutputURI(output);
            XProcConfiguration xprocConfig = xproc.getXProcConfiguration();
            Resolver resolver = xprocConfig.getResolver();
            Source source = resolver.getSource(null, uri);
            contentType = parameterProvider.getExternalContentType(uri);
            is = source.getInputStream();
        } else {
            Serialization ser;
            String primaryOutputPort = XProcUtils.getPrimaryOutputPort(output);
            List<Source> resultSources = XProcUtils.getOutputSources(output, primaryOutputPort);
            SerializationOptions serializationOptions = null;
            Map serializations = pipeline.getSerializations();
            if (serializations != null && (ser = (Serialization)serializations.get(primaryOutputPort)) != null) {
                serializationOptions = ser.getSerializationOptions();
            }
            SerializerFactory serializerFactory = SerializerFactory.newInstance();
            Serializer serializer = serializerFactory.newSerializer();
            String encoding = this.getOutputXMLEncoding(primaryOutputPort, serializationOptions);
            contentType = parameterProvider.getContentType(primaryOutputPort);
            if (!StringUtils.isEmpty(encoding)) {
                contentType = StringUtils.isEmpty(contentType) ? "charset=" + encoding : contentType + ";charset=" + encoding;
            }
            is = XProcServlet.serialize(xproc, serializer, serializationOptions, resultSources);
        }
        this.writeHTTPResponse(request, response, is, contentType, parameterProvider.getHeaders());
    }

    protected XProcParameterProvider getXProcParameterProvider(HttpServletRequest request) throws Exception {
        return new HttpRequestBasedXProcParameterProvider(request);
    }

    private void writeHTTPResponse(HttpServletRequest request, HttpServletResponse response, InputStream is, String contentType, Map<String, String> headers) throws IOException {
        response.setContentType(contentType);
        response.setHeader("Expires", "0");
        response.setHeader("Cache-Control", "must-revalidate");
        if (headers != null) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                response.setHeader(entry.getKey(), entry.getValue());
            }
        }
        this.writeResponse(request, response, is);
    }

    private static InputStream serialize(XProc xproc, Serializer serializer, SerializationOptions serializationOptions, List<Source> sources) throws IOException {
        DistributedByteArray dba = new DistributedByteArray();
        OutputStream os = dba.getOutputStream();
        if (sources != null) {
            for (Source source : sources) {
                xproc.getXProcUtil().serialize(source, serializer, serializationOptions, os);
            }
        }
        return dba.getInputStream();
    }

    @Deprecated
    protected String getOutputXMLEncoding(Source result) {
        return "UTF-8";
    }

    protected String getOutputXMLEncoding(String port, SerializationOptions serializationOptions) {
        String encoding = serializationOptions == null ? null : (String)serializationOptions.getOption(SerializationOptions.ENCODING);
        return encoding == null ? "UTF-8" : encoding;
    }
}

