/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.user.impl;

import com.emc.documentum.xml.dds.DDS;
import com.emc.documentum.xml.dds.application.Application;
import com.emc.documentum.xml.dds.application.ServiceManager;
import com.emc.documentum.xml.dds.service.DDSServiceType;
import com.emc.documentum.xml.dds.service.Service;
import com.emc.documentum.xml.dds.service.ServiceType;
import com.emc.documentum.xml.dds.service.exception.ServiceNotAvailableException;
import com.emc.documentum.xml.dds.service.impl.ServiceImpl;
import com.emc.documentum.xml.dds.user.TokenService;
import com.emc.documentum.xml.dds.user.TokenToTokenMapping;
import com.emc.documentum.xml.dds.user.User;
import com.emc.documentum.xml.dds.user.UserService;
import com.emc.documentum.xml.dds.user.UserToken;
import com.emc.documentum.xml.dds.user.internal.TokenToTokenMappingImpl;
import com.emc.documentum.xml.dds.user.internal.UserTokenImpl;

public class TokenServiceImpl
extends ServiceImpl
implements TokenService {
    private long currentId;
    private final Object tokenMutex = new Object();
    private TokenToTokenMapping tokenToTokenMapping;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public UserToken createToken(Application application, User user) throws ServiceNotAvailableException {
        this.checkRunning();
        Object object = this.tokenMutex;
        synchronized (object) {
            return new UserTokenImpl(application.getName(), user.getId(), this.currentId++);
        }
    }

    @Override
    public Application getApplication(UserToken token) {
        return DDS.getApplication();
    }

    @Override
    public User getUser(UserToken token) {
        UserService userManager = (UserService)DDS.getApplication().getServiceManager().getService(DDSServiceType.USER);
        return userManager.getUser(token.getUserId());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public TokenToTokenMapping getTokenToTokenMapping() {
        Object object = this.tokenMutex;
        synchronized (object) {
            if (this.tokenToTokenMapping == null) {
                this.tokenToTokenMapping = new TokenToTokenMappingImpl();
            }
        }
        return this.tokenToTokenMapping;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setTokenToTokenMapping(TokenToTokenMapping tokenToTokenMapping) {
        Object object = this.tokenMutex;
        synchronized (object) {
            this.tokenToTokenMapping = tokenToTokenMapping;
        }
    }

    @Override
    public ServiceType getType() {
        return DDSServiceType.TOKEN;
    }

    @Override
    public boolean activateConfiguration() {
        return true;
    }

    @Override
    protected boolean checkDependencies() {
        return true;
    }

    @Override
    protected boolean executeInitialization() {
        return true;
    }

    @Override
    protected boolean executeStartup() {
        return true;
    }

    @Override
    protected boolean executePause() {
        return true;
    }

    @Override
    protected boolean executeResume() {
        return true;
    }

    @Override
    protected boolean executeShutdown() {
        return true;
    }
}

