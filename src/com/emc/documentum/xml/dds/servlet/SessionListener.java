/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpSession
 *  javax.servlet.http.HttpSessionEvent
 *  javax.servlet.http.HttpSessionListener
 */
package com.emc.documentum.xml.dds.servlet;

import com.emc.documentum.xml.dds.DDS;
import com.emc.documentum.xml.dds.application.Application;
import com.emc.documentum.xml.dds.application.ServiceManager;
import com.emc.documentum.xml.dds.logging.LogCenter;
import com.emc.documentum.xml.dds.persistence.exception.UserNotFoundException;
import com.emc.documentum.xml.dds.service.DDSServiceType;
import com.emc.documentum.xml.dds.service.Service;
import com.emc.documentum.xml.dds.service.ServiceType;
import com.emc.documentum.xml.dds.service.exception.ServiceNotAvailableException;
import com.emc.documentum.xml.dds.user.TokenService;
import com.emc.documentum.xml.dds.user.TokenToTokenMapping;
import com.emc.documentum.xml.dds.user.UserService;
import com.emc.documentum.xml.dds.user.UserToken;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

public class SessionListener
implements HttpSessionListener {
    public void sessionCreated(HttpSessionEvent event) {
    }

    public void sessionDestroyed(HttpSessionEvent event) {
        HttpSession session = event.getSession();
        if (session != null) {
            Application application = DDS.getApplication();
            UserService userManager = (UserService)application.getServiceManager().getService(DDSServiceType.USER);
            TokenService tokenManager = (TokenService)application.getServiceManager().getService(DDSServiceType.TOKEN);
            if (tokenManager != null) {
                TokenToTokenMapping tokenToTokenMapping = tokenManager.getTokenToTokenMapping();
                UserToken userToken = tokenToTokenMapping.getUserToken(session.getId());
                tokenToTokenMapping.removeTokenMapping(session.getId());
                if (userManager != null && userToken != null) {
                    try {
                        userManager.logoutUser(userToken.getUserId());
                    }
                    catch (ServiceNotAvailableException snae) {
                        LogCenter.exception(this, "UserService not available while logging out: " + userToken.getUserId(), snae);
                    }
                    catch (UserNotFoundException unfe) {
                        LogCenter.exception(this, "User not found: " + userToken.getUserId(), unfe);
                    }
                }
            }
        }
    }
}

