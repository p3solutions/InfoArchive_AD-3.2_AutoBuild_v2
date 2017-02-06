/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.user.internal;

import com.emc.documentum.xml.dds.user.TokenToTokenMapping;
import com.emc.documentum.xml.dds.user.UserToken;
import java.util.HashMap;
import java.util.Map;

public class TokenToTokenMappingImpl
implements TokenToTokenMapping {
    private final Map<Object, UserToken> external2IS = new HashMap<Object, UserToken>();
    private final Map<UserToken, Object> is2External = new HashMap<UserToken, Object>();
    private final Map<String, UserToken> id2IS = new HashMap<String, UserToken>();

    @Override
    public synchronized Object getExternalToken(UserToken userToken) {
        return this.is2External.get(userToken);
    }

    @Override
    public synchronized UserToken getUserToken(Object externalToken) {
        return this.external2IS.get(externalToken);
    }

    @Override
    public synchronized UserToken getUserTokenFromUserId(String userId) {
        return this.id2IS.get(userId);
    }

    @Override
    public synchronized Object getExternalTokenFromUserId(String userId) {
        UserToken userToken = this.id2IS.get(userId);
        if (userToken != null) {
            return this.is2External.get(userToken);
        }
        return null;
    }

    @Override
    public synchronized void storeTokenMapping(Object externalToken, UserToken isToken) {
        this.external2IS.put(externalToken, isToken);
        this.is2External.put(isToken, externalToken);
        this.id2IS.put(isToken.getUserId(), isToken);
    }

    @Override
    public synchronized void removeTokenMapping(Object externalToken) {
        UserToken isToken = this.external2IS.remove(externalToken);
        if (isToken != null) {
            this.is2External.remove(isToken);
            this.id2IS.remove(isToken.getUserId());
        }
    }

    @Override
    public synchronized void removeTokenMapping(UserToken isToken) {
        Object externalToken = this.is2External.remove(isToken);
        if (externalToken != null) {
            this.external2IS.remove(externalToken);
        }
        this.id2IS.remove(isToken.getUserId());
    }

    @Override
    public synchronized void removeTokenMappingFromUserId(String userId) {
        Object externalToken;
        UserToken isToken = this.id2IS.remove(userId);
        if (isToken != null && (externalToken = this.is2External.remove(isToken)) != null) {
            this.external2IS.remove(externalToken);
        }
    }
}

