/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpSession
 */
package com.emc.documentum.xml.dds.internal.webservice;

import com.emc.documentum.xml.dds.DDS;
import com.emc.documentum.xml.dds.application.Application;
import com.emc.documentum.xml.dds.application.ServiceManager;
import com.emc.documentum.xml.dds.fs.ServiceContext;
import com.emc.documentum.xml.dds.service.DDSServiceType;
import com.emc.documentum.xml.dds.service.Service;
import com.emc.documentum.xml.dds.service.ServiceType;
import com.emc.documentum.xml.dds.user.TokenService;
import com.emc.documentum.xml.dds.user.TokenToTokenMapping;
import com.emc.documentum.xml.dds.user.User;
import com.emc.documentum.xml.dds.user.UserService;
import com.emc.documentum.xml.dds.user.UserToken;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.ws.handler.MessageContext;

public class WebServiceContext
implements ServiceContext {
    @Resource
    private final javax.xml.ws.WebServiceContext webServiceContext;

    public WebServiceContext(javax.xml.ws.WebServiceContext webServiceContext) {
        this.webServiceContext = webServiceContext;
    }

    @Override
    public User getUser() {
        return this.getUserFromRequest();
    }

    public User getUserFromRequest() {
        Application application = this.getApplication();
        if (application != null) {
            UserService userManager = (UserService)application.getServiceManager().getService(DDSServiceType.USER);
            if (userManager == null) {
                return application.getApplicationUser();
            }
            MessageContext msgContext = this.webServiceContext.getMessageContext();
            if (msgContext == null) {
                return application.getApplicationUser();
            }
            HttpServletRequest request = (HttpServletRequest)msgContext.get("javax.xml.ws.servlet.request");
            if (request == null) {
                return application.getApplicationUser();
            }
            UserToken userToken = this.getUserTokenFromRequest(request);
            if (userToken != null) {
                return userManager.getUser(userToken.getUserId());
            }
        }
        return null;
    }

    public UserToken getUserTokenFromRequest(HttpServletRequest request) {
        TokenService tokenManager;
        Application application = this.getApplication();
        if (application != null && (tokenManager = (TokenService)application.getServiceManager().getService(DDSServiceType.TOKEN)) != null) {
            TokenToTokenMapping tokenToTokenMapping = tokenManager.getTokenToTokenMapping();
            return tokenToTokenMapping.getUserToken(request.getSession().getId());
        }
        return null;
    }

    public Application getApplication() {
        return DDS.getApplication();
    }
}

