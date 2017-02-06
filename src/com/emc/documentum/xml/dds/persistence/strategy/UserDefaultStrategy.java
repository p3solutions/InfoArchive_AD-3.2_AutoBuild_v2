/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.persistence.strategy;

import com.emc.documentum.xml.dds.application.Application;
import com.emc.documentum.xml.dds.persistence.StoreUser;
import com.emc.documentum.xml.dds.persistence.strategy.AbstractSessionStoreUserStrategy;
import com.emc.documentum.xml.dds.user.User;

public class UserDefaultStrategy
extends AbstractSessionStoreUserStrategy {
    @Override
    public StoreUser getStoreUser(Application application, User user, String storeAlias) {
        StoreUser result = user.getStoreUser(storeAlias);
        return result == null ? this.fallback(application, user, storeAlias) : result;
    }
}

