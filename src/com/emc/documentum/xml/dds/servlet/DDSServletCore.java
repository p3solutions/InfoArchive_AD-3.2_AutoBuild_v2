/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  com.google.gwt.user.server.rpc.RPCServletUtils
 *  javax.servlet.ServletContext
 *  javax.servlet.ServletOutputStream
 *  javax.servlet.http.HttpServlet
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  javax.servlet.http.HttpSession
 */
package com.emc.documentum.xml.dds.servlet;

import com.emc.documentum.xml.dds.DDS;
import com.emc.documentum.xml.dds.application.Application;
import com.emc.documentum.xml.dds.application.ServiceManager;
import com.emc.documentum.xml.dds.service.DDSServiceType;
import com.emc.documentum.xml.dds.service.Service;
import com.emc.documentum.xml.dds.service.ServiceType;
import com.emc.documentum.xml.dds.user.TokenService;
import com.emc.documentum.xml.dds.user.TokenToTokenMapping;
import com.emc.documentum.xml.dds.user.User;
import com.emc.documentum.xml.dds.user.UserService;
import com.emc.documentum.xml.dds.user.UserToken;
import com.google.gwt.user.server.rpc.RPCServletUtils;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.zip.GZIPOutputStream;
import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class DDSServletCore {
    private static final String CONTENT_ENCODING = "Content-Encoding";
    private static final String CONTENT_ENCODING_GZIP = "gzip";
    private final HttpServlet ownerServlet;

    public DDSServletCore(HttpServlet ownerServlet) {
        this.ownerServlet = ownerServlet;
    }

    public Application getApplicationFromRequest() {
        return DDS.getApplication();
    }

    public UserToken getUserTokenFromRequest(HttpServletRequest request) {
        TokenService tokenManager;
        Application application = this.getApplicationFromRequest();
        if (application != null && (tokenManager = (TokenService)application.getServiceManager().getService(DDSServiceType.TOKEN)) != null) {
            TokenToTokenMapping tokenToTokenMapping = tokenManager.getTokenToTokenMapping();
            HttpSession session = request.getSession();
            if (session != null) {
                return tokenToTokenMapping.getUserToken(session.getId());
            }
        }
        return null;
    }

    public User getUserFromRequest(HttpServletRequest request) {
        Application application = this.getApplicationFromRequest();
        if (application != null) {
            UserService userManager = (UserService)application.getServiceManager().getService(DDSServiceType.USER);
            if (userManager == null) {
                return application.getApplicationUser();
            }
            UserToken userToken = this.getUserTokenFromRequest(request);
            if (userToken != null) {
                return userManager.getUser(userToken.getUserId());
            }
        }
        return null;
    }

    public void respondWithFailure(HttpServletResponse response, int status, String message) {
        block2 : {
            try {
                response.setContentType("text/plain");
                response.setStatus(status);
                response.getWriter().write(message);
            }
            catch (IOException e) {
                ServletContext servletContext = this.ownerServlet.getServletContext();
                if (servletContext == null) break block2;
                servletContext.log("respondWithFailure failed while sending the previous failure to the client", (Throwable)e);
            }
        }
    }

    public void writeResponse(HttpServletRequest request, HttpServletResponse response, InputStream inputStream) throws IOException {
        if (RPCServletUtils.acceptsGzipEncoding((HttpServletRequest)request)) {
            Throwable caught = null;
            try {
                response.setHeader("Content-Encoding", "gzip");
                response.setStatus(200);
                GZIPOutputStream gzipOutputStream = new GZIPOutputStream((OutputStream)response.getOutputStream());
                this.writeTo(gzipOutputStream, inputStream);
                gzipOutputStream.finish();
                gzipOutputStream.flush();
            }
            catch (IOException e) {
                ServletContext servletContext = this.ownerServlet.getServletContext();
                if (servletContext != null) {
                    servletContext.log("Unable to compress response", caught);
                }
                response.sendError(500);
                return;
            }
        } else {
            this.writeTo((OutputStream)response.getOutputStream(), inputStream);
        }
    }

    private void writeTo(OutputStream outputStream, InputStream inputStream) throws IOException {
        byte[] bytes = new byte[16384];
        int len = inputStream.read(bytes);
        while (len > 0) {
            outputStream.write(bytes, 0, len);
            len = inputStream.read(bytes);
        }
    }
}

