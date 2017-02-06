/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.persistence;

import com.emc.documentum.xml.dds.application.Application;
import com.emc.documentum.xml.dds.persistence.StoreUser;
import com.emc.documentum.xml.dds.user.User;

public interface SessionStoreUserStrategy {
    public StoreUser getStoreUser(Application var1, User var2, String var3);

    public void setFallBackStrategy(SessionStoreUserStrategy var1);
}

