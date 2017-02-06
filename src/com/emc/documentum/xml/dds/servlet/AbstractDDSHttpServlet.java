/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServlet
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  javax.servlet.http.HttpSession
 */
package com.emc.documentum.xml.dds.servlet;

import com.emc.documentum.xml.dds.DDS;
import com.emc.documentum.xml.dds.application.Application;
import com.emc.documentum.xml.dds.application.ServiceManager;
import com.emc.documentum.xml.dds.operation.exception.OperationFailedException;
import com.emc.documentum.xml.dds.operation.framework.Operation;
import com.emc.documentum.xml.dds.service.DDSServiceType;
import com.emc.documentum.xml.dds.service.Service;
import com.emc.documentum.xml.dds.service.ServiceType;
import com.emc.documentum.xml.dds.servlet.DDSServletCore;
import com.emc.documentum.xml.dds.uri.DDSURI;
import com.emc.documentum.xml.dds.uri.URIResolver;
import com.emc.documentum.xml.dds.uri.URITarget;
import com.emc.documentum.xml.dds.uri.exception.DDSURIException;
import com.emc.documentum.xml.dds.user.TokenService;
import com.emc.documentum.xml.dds.user.TokenToTokenMapping;
import com.emc.documentum.xml.dds.user.User;
import com.emc.documentum.xml.dds.user.UserService;
import com.emc.documentum.xml.dds.user.UserToken;
import java.io.IOException;
import java.io.InputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public abstract class AbstractDDSHttpServlet
extends HttpServlet {
    private final DDSServletCore core;

    public AbstractDDSHttpServlet() {
        this.core = new DDSServletCore(this);
    }

    protected Application getApplication() {
        return DDS.getApplication();
    }

    protected UserToken getUserTokenFromRequest(HttpServletRequest request) {
        TokenService tokenManager;
        Application application = this.getApplication();
        if (application != null && (tokenManager = (TokenService)application.getServiceManager().getService(DDSServiceType.TOKEN)) != null) {
            TokenToTokenMapping tokenToTokenMapping = tokenManager.getTokenToTokenMapping();
            return tokenToTokenMapping.getUserToken(request.getSession().getId());
        }
        return null;
    }

    protected User getUserFromRequest(HttpServletRequest request) {
        Application application = this.getApplication();
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

    protected void respondWithFailure(HttpServletResponse response, int status, String message) {
        this.core.respondWithFailure(response, status, message);
    }

    protected void writeResponse(HttpServletRequest request, HttpServletResponse response, InputStream inputStream) throws IOException {
        this.core.writeResponse(request, response, inputStream);
    }

    protected <T> T execute(HttpServletRequest request, Operation<T> operation) throws OperationFailedException {
        return this.getApplication().execute(this.getUserFromRequest(request), operation);
    }

    protected URIResolver getURIResolver(Application application) {
        return application.getDefaultURIResolver();
    }

    protected URITarget resolveURI(HttpServletRequest request, String uri) throws DDSURIException {
        return this.getURIResolver(this.getApplication()).resolveURI(DDSURI.parseURI(uri), this.getUserFromRequest(request));
    }
}

