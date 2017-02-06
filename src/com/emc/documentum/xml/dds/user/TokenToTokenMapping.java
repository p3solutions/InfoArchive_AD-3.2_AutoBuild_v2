/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.user;

import com.emc.documentum.xml.dds.user.UserToken;

public interface TokenToTokenMapping {
    public Object getExternalToken(UserToken var1);

    public UserToken getUserToken(Object var1);

    public UserToken getUserTokenFromUserId(String var1);

    public Object getExternalTokenFromUserId(String var1);

    public void storeTokenMapping(Object var1, UserToken var2);

    public void removeTokenMapping(Object var1);

    public void removeTokenMapping(UserToken var1);

    public void removeTokenMappingFromUserId(String var1);
}

