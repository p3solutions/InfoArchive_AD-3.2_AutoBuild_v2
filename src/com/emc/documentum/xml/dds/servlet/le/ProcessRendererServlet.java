/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  com.emc.documentum.xml.le.engine.resolver.DataModuleRef
 *  com.emc.documentum.xml.le.engine.resolver.DataModuleRefFactory
 *  com.emc.documentum.xml.le.error.ProcessException
 *  javax.servlet.ServletOutputStream
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package com.emc.documentum.xml.dds.servlet.le;

import com.emc.documentum.xml.dds.application.Application;
import com.emc.documentum.xml.dds.application.ServiceManager;
import com.emc.documentum.xml.dds.le.DDSProcessDataModuleRenderer;
import com.emc.documentum.xml.dds.le.LogicEngineService;
import com.emc.documentum.xml.dds.le.exception.DDSProcessDataModuleRendererConfigurationException;
import com.emc.documentum.xml.dds.logging.LogCenter;
import com.emc.documentum.xml.dds.operation.exception.CommitFailedException;
import com.emc.documentum.xml.dds.persistence.Session;
import com.emc.documentum.xml.dds.service.DDSServiceType;
import com.emc.documentum.xml.dds.service.Service;
import com.emc.documentum.xml.dds.service.ServiceType;
import com.emc.documentum.xml.dds.service.exception.ServiceNotAvailableException;
import com.emc.documentum.xml.dds.servlet.AbstractDDSHttpServlet;
import com.emc.documentum.xml.dds.user.User;
import com.emc.documentum.xml.dds.util.SessionPool;
import com.emc.documentum.xml.dds.util.internal.StringUtils;
import com.emc.documentum.xml.le.engine.resolver.DataModuleRef;
import com.emc.documentum.xml.le.engine.resolver.DataModuleRefFactory;
import com.emc.documentum.xml.le.error.ProcessException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

public class ProcessRendererServlet
extends AbstractDDSHttpServlet {
    public static final String PARAM_REFDM = "refdm";
    public static final String PARAM_CONTENT_TYPE = "contenttype";
    public static final String PARAM_XLINK = "xlink";
    private static final String CONTENT_TYPE_APPLICATION_PDF = "application/pdf";

    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        this.doRender(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) {
        this.doRender(request, response);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void doRender(HttpServletRequest request, HttpServletResponse response) {
        String contentType = this.getContentType(request);
        String refdm = this.getRefdm(request);
        boolean isXLink = this.isXLinkReference(request);
        if (!"application/pdf".equals(contentType)) {
            this.respondWithFailure(response, 501, "Content type '" + contentType + "' not supported.");
            return;
        }
        DDSProcessDataModuleRenderer processDataModuleRendererIS = null;
        boolean ok = true;
        try {
            DataModuleRef dmRef = this.getDataModuleRef(refdm, isXLink);
            processDataModuleRendererIS = this.getDDSProcessDataModuleRenderer(request);
            processDataModuleRendererIS.render(dmRef, (OutputStream)response.getOutputStream(), DDSProcessDataModuleRenderer.OutputType.PDF);
            response.setContentType(contentType);
            response.setHeader("Expires", "0");
            response.setHeader("Cache-Control", "must-revalidate");
            this.releaseDDSProcessDataModuleRenderer(processDataModuleRendererIS);
        }
        catch (Exception e) {
            ok = false;
            LogCenter.exception("Error while rendering a process data module", (Throwable)e);
            this.respondWithFailure(response, 500, e.getMessage());
        }
        finally {
            block13 : {
                try {
                    if (processDataModuleRendererIS != null) {
                        this.releaseDDSProcessDataModuleRenderer(processDataModuleRendererIS);
                    }
                }
                catch (Exception e) {
                    if (!ok) break block13;
                    LogCenter.exception("Error while rendering a process data module", (Throwable)e);
                    this.respondWithFailure(response, 500, e.getMessage());
                }
            }
        }
    }

    private DDSProcessDataModuleRenderer getDDSProcessDataModuleRenderer(HttpServletRequest request) throws ServiceNotAvailableException, DDSProcessDataModuleRendererConfigurationException {
        Application application = this.getApplication();
        User user = this.getUserFromRequest(request);
        LogicEngineService logicEngineService = (LogicEngineService)application.getServiceManager().getService(DDSServiceType.LOGICENGINE);
        return logicEngineService.newProcessDataModuleRenderer(user);
    }

    private void releaseDDSProcessDataModuleRenderer(DDSProcessDataModuleRenderer processDataModuleRenderer) throws CommitFailedException {
        Collection<Session> openSessions = processDataModuleRenderer.getOpenSessions();
        if (openSessions != null) {
            for (Session session : openSessions) {
                session.commit();
            }
        }
        processDataModuleRenderer.getSessionPool().clear();
    }

    private String getContentType(HttpServletRequest request) {
        String contentType = request.getParameter("contenttype");
        return StringUtils.isEmpty(contentType) ? "application/pdf" : contentType;
    }

    private String getRefdm(HttpServletRequest request) {
        return request.getParameter("refdm");
    }

    private boolean isXLinkReference(HttpServletRequest request) {
        String xlink = request.getParameter("xlink");
        return StringUtils.isEmpty(xlink) || "true".equals(xlink);
    }

    private DataModuleRef getDataModuleRef(String refdm, boolean isXLink) throws ProcessException, ParserConfigurationException, SAXException, IOException {
        if (isXLink) {
            return DataModuleRefFactory.newInstance().newDataModuleRef(refdm);
        }
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = builderFactory.newDocumentBuilder();
        Document doc = builder.parse(new ByteArrayInputStream(refdm.getBytes("UTF-8")));
        return DataModuleRefFactory.newInstance().newDataModuleRef(doc.getDocumentElement());
    }
}

