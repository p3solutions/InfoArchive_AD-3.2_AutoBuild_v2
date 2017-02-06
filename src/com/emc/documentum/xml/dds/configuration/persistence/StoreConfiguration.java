/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.configuration.persistence;

import com.emc.documentum.xml.dds.configuration.Configuration;
import com.emc.documentum.xml.dds.configuration.persistence.StoreUserConfiguration;
import com.emc.documentum.xml.dds.persistence.StoreType;

public abstract class StoreConfiguration
implements Configuration {
    private String alias;
    private StoreUserConfiguration defaultStoreUser;

    public StoreConfiguration(String alias) {
        this.alias = alias;
    }

    public StoreConfiguration(String alias, StoreUserConfiguration defaultStoreUser) {
        this.alias = alias;
        this.defaultStoreUser = defaultStoreUser;
    }

    public final String getAlias() {
        return this.alias == null ? null : this.alias.trim();
    }

    public final void setAlias(String alias) {
        this.alias = alias;
    }

    public final StoreUserConfiguration getDefaultStoreUser() {
        return this.defaultStoreUser;
    }

    public final void setDefaultStoreUser(StoreUserConfiguration defaultStoreUser) {
        this.defaultStoreUser = defaultStoreUser;
    }

    public abstract StoreType getType();
}

