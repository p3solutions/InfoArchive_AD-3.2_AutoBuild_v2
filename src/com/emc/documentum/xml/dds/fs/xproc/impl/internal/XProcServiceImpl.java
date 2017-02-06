/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  com.emc.documentum.xml.xproc.XProc
 *  com.emc.documentum.xml.xproc.XProcConfiguration
 *  com.emc.documentum.xml.xproc.XProcException
 *  com.emc.documentum.xml.xproc.io.Source
 *  com.emc.documentum.xml.xproc.pipeline.model.PipelineInput
 *  com.emc.documentum.xml.xproc.pipeline.model.PipelineOutput
 *  com.emc.documentum.xml.xproc.pipeline.model.step.Pipeline
 */
package com.emc.documentum.xml.dds.fs.xproc.impl.internal;

import com.emc.documentum.xml.dds.application.ServiceManager;
import com.emc.documentum.xml.dds.exception.DDSException;
import com.emc.documentum.xml.dds.fs.ServiceContext;
import com.emc.documentum.xml.dds.fs.impl.internal.ServiceImpl;
import com.emc.documentum.xml.dds.fs.xproc.XProcProperties;
import com.emc.documentum.xml.dds.fs.xproc.XProcResultHandler;
import com.emc.documentum.xml.dds.fs.xproc.XProcService;
import com.emc.documentum.xml.dds.fs.xproc.XProcServiceException;
import com.emc.documentum.xml.dds.operation.exception.CommitFailedException;
import com.emc.documentum.xml.dds.persistence.Session;
import com.emc.documentum.xml.dds.service.DDSServiceType;
import com.emc.documentum.xml.dds.service.Service;
import com.emc.documentum.xml.dds.service.ServiceType;
import com.emc.documentum.xml.dds.user.User;
import com.emc.documentum.xml.dds.util.SessionPool;
import com.emc.documentum.xml.dds.xproc.DDSXProc;
import com.emc.documentum.xml.dds.xproc.XProcUtils;
import com.emc.documentum.xml.dds.xproc.internal.TransientWriterResolverHandler;
import com.emc.documentum.xml.xproc.XProc;
import com.emc.documentum.xml.xproc.XProcConfiguration;
import com.emc.documentum.xml.xproc.XProcException;
import com.emc.documentum.xml.xproc.io.Source;
import com.emc.documentum.xml.xproc.pipeline.model.PipelineInput;
import com.emc.documentum.xml.xproc.pipeline.model.PipelineOutput;
import com.emc.documentum.xml.xproc.pipeline.model.step.Pipeline;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.xml.namespace.QName;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;

public class XProcServiceImpl
extends ServiceImpl
implements XProcService {
    public XProcServiceImpl(ServiceContext serviceContext) {
        super(serviceContext);
    }

    @Override
    public PipelineOutput runPipeline(Source pipeline, PipelineInput pipelineInput, XProcProperties properties) throws DDSException {
        return (PipelineOutput)this.runPipeline(pipeline, pipelineInput, properties, new DefaultXProcResultHandler());
    }

    @Override
    public <H> H runPipeline(Source pipeline, PipelineInput pipelineInput, XProcProperties properties, XProcResultHandler<H> resultHandler) throws DDSException {
        DDSXProc xprocIS = null;
        if (pipeline == null) {
            throw new XProcServiceException("runPipeline invoked with null Source pipeline");
        }
        if (resultHandler == null) {
            throw new XProcServiceException("runPipeline invoked with null XProcResultHandler");
        }
        try {
            User user = this.getServiceContext().getUser();
            com.emc.documentum.xml.dds.xproc.XProcService xprocService = (com.emc.documentum.xml.dds.xproc.XProcService)XProcServiceImpl.getApplication().getServiceManager().getService(DDSServiceType.XPROC);
            boolean readOnly = true;
            if (properties != null) {
                readOnly = properties.isReadOnly();
            }
            xprocIS = xprocService.newXProc(user, readOnly);
            XProc xproc = xprocIS.getXProc();
            Pipeline newPipeline = XProcUtils.newPipeline(xproc, pipeline);
            PipelineOutput output = XProcUtils.runPipeline(xproc, newPipeline, pipelineInput);
            this.releaseTransientData(xproc);
            H h = resultHandler.transformXProcResult(output);
            return h;
        }
        catch (Exception e) {
            if (e instanceof DDSException) {
                throw (DDSException)e;
            }
            if (e instanceof XProcException) {
                throw (XProcException)e;
            }
            throw new XProcServiceException(e);
        }
        finally {
            if (xprocIS != null) {
                this.releaseISXProc(xprocIS);
            }
        }
    }

    private void releaseISXProc(DDSXProc xprocIS) throws CommitFailedException {
        Collection<Session> openSessions = xprocIS.getOpenSessions();
        if (openSessions != null) {
            for (Session session : openSessions) {
                session.commit();
            }
        }
        xprocIS.getSessionPool().clear();
    }

    protected void releaseTransientData(XProc xproc) {
        Object handler = xproc.getXProcConfiguration().getAttribute(TransientWriterResolverHandler.TRANSIENT_HANDLER_INTERNAL_ATTR);
        if (handler instanceof TransientWriterResolverHandler) {
            ((TransientWriterResolverHandler)handler).release();
        }
    }

    public static class ExceptionWrapper {
        private Exception exception;

        public void setException(Exception e) {
            this.exception = e;
        }

        public Exception getException() {
            return this.exception;
        }
    }

    public static class DefaultXProcResultHandler
    implements XProcResultHandler<PipelineOutput> {
        @Override
        public PipelineOutput transformXProcResult(PipelineOutput input) throws DDSException {
            return this.serialize(input);
        }

        public PipelineOutput serialize(final PipelineOutput output) throws DDSException {
            ExceptionWrapper exceptionWrapper = new ExceptionWrapper();
            final HashMap portMap = new HashMap();
            for (String port : output.getOutputPorts()) {
                ArrayList<Source> resultSources = new ArrayList<Source>();
                List sources = output.getSources(port);
                if (sources != null) {
                    Iterator i$ = sources.iterator();
                    while (i$.hasNext()) {
                        Source source;
                        Source newSource = source = (Source)i$.next();
                        if (source.getNode() != null) {
                            Node node = source.getNode();
                            DOMImplementationLS domImplementationLS = null;
                            domImplementationLS = node.getNodeType() != 9 ? (DOMImplementationLS)((Object)node.getOwnerDocument().getImplementation()) : (DOMImplementationLS)((Object)((Document)node).getImplementation());
                            LSSerializer ls = domImplementationLS.createLSSerializer();
                            String data = ls.writeToString(node);
                            try {
                                ByteArrayInputStream is = new ByteArrayInputStream(data.getBytes("UTF-8"));
                                newSource = new Source((InputStream)is, source.getPublicID(), source.getSystemID());
                            }
                            catch (IOException e) {
                                exceptionWrapper.setException(e);
                            }
                        }
                        resultSources.add(newSource);
                    }
                }
                portMap.put(port, resultSources);
            }
            PipelineOutput resultOutput = new PipelineOutput(){

                public Set<String> getExternalOutputs() {
                    return output.getExternalOutputs();
                }

                public Set<String> getOutputPorts() {
                    return output.getOutputPorts();
                }

                public String getPrimaryOutputPort() {
                    return output.getPrimaryOutputPort();
                }

                public List<Source> getSources(String port) {
                    return (List)portMap.get(port);
                }
            };
            Exception e = exceptionWrapper.getException();
            if (e != null) {
                throw new DDSException(e);
            }
            return resultOutput;
        }

    }

}

