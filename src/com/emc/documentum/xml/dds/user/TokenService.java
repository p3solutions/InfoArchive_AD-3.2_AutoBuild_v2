/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.user;

import com.emc.documentum.xml.dds.application.Application;
import com.emc.documentum.xml.dds.service.Service;
import com.emc.documentum.xml.dds.service.exception.ServiceNotAvailableException;
import com.emc.documentum.xml.dds.user.TokenToTokenMapping;
import com.emc.documentum.xml.dds.user.User;
import com.emc.documentum.xml.dds.user.UserToken;

public interface TokenService
extends Service {
    public UserToken createToken(Application var1, User var2) throws ServiceNotAvailableException;

    public Application getApplication(UserToken var1);

    public User getUser(UserToken var1);

    public void setTokenToTokenMapping(TokenToTokenMapping var1);

    public TokenToTokenMapping getTokenToTokenMapping();
}

