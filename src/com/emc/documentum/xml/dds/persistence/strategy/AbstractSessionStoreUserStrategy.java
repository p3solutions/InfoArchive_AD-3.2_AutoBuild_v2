/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.persistence.strategy;

import com.emc.documentum.xml.dds.application.Application;
import com.emc.documentum.xml.dds.persistence.SessionStoreUserStrategy;
import com.emc.documentum.xml.dds.persistence.StoreUser;
import com.emc.documentum.xml.dds.user.User;

public abstract class AbstractSessionStoreUserStrategy
implements SessionStoreUserStrategy {
    private SessionStoreUserStrategy fallbackStrategy;

    @Override
    public void setFallBackStrategy(SessionStoreUserStrategy strategy) {
        this.fallbackStrategy = strategy;
    }

    protected StoreUser fallback(Application application, User user, String storeAlias) {
        if (this.fallbackStrategy == null) {
            return null;
        }
        return this.fallbackStrategy.getStoreUser(application, user, storeAlias);
    }
}

