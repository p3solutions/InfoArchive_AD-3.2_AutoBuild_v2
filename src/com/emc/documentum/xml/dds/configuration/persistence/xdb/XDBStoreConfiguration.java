/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.configuration.persistence.xdb;

import com.emc.documentum.xml.dds.configuration.persistence.StoreConfiguration;
import com.emc.documentum.xml.dds.configuration.persistence.StoreUserConfiguration;
import com.emc.documentum.xml.dds.persistence.StoreType;

public final class XDBStoreConfiguration
extends StoreConfiguration {
    private String bootstrap;
    private String databaseName;
    private int cachePages = 10000;

    public XDBStoreConfiguration(String alias, StoreUserConfiguration defaultStoreUser, String bootstrap, String databaseName, int cachePages) {
        super(alias, defaultStoreUser);
        this.bootstrap = bootstrap;
        this.databaseName = databaseName;
        this.cachePages = cachePages;
    }

    public String getBootstrap() {
        return this.bootstrap == null ? null : this.bootstrap.trim();
    }

    public void setBootstrap(String bootstrap) {
        this.bootstrap = bootstrap;
    }

    public String getDatabaseName() {
        return this.databaseName == null ? null : this.databaseName.trim();
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public int getCachePages() {
        return this.cachePages;
    }

    public void setCachePages(int cachePages) {
        this.cachePages = cachePages;
    }

    @Override
    public StoreType getType() {
        return StoreType.XDB;
    }
}

