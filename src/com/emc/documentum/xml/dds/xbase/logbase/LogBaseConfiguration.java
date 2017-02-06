/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.xbase.logbase;

import com.emc.documentum.xml.dds.configuration.baseline.XBaseConfiguration;
import com.emc.documentum.xml.dds.xbase.StorageStrategy;

public class LogBaseConfiguration
implements XBaseConfiguration {
    private String xBaseId;
    private String storeAlias;
    private String storeUserId;
    private String baseName;
    private String location;
    private StorageStrategy strategy;

    public LogBaseConfiguration(String xBaseId, String storeAlias, String storeUserId, String baseName, String location, StorageStrategy strategy) {
        this.xBaseId = xBaseId;
        this.storeAlias = storeAlias;
        this.storeUserId = storeUserId;
        this.baseName = baseName;
        this.location = location;
        this.strategy = strategy;
    }

    public String getXBaseId() {
        return this.xBaseId == null ? null : this.xBaseId.trim();
    }

    public void setXBaseId(String xBaseIdParam) {
        this.xBaseId = xBaseIdParam;
    }

    public String getStoreAlias() {
        return this.storeAlias == null ? null : this.storeAlias.trim();
    }

    public void setStoreAlias(String alias) {
        this.storeAlias = alias;
    }

    public String getStoreUserId() {
        return this.storeUserId == null ? null : this.storeUserId.trim();
    }

    public void setStoreUserId(String storeUserId) {
        this.storeUserId = storeUserId;
    }

    public String getBaseName() {
        return this.baseName == null ? null : this.baseName.trim();
    }

    public void setBaseName(String baseName) {
        this.baseName = baseName;
    }

    public String getLocation() {
        return this.location == null ? null : this.location.trim();
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public StorageStrategy getStrategy() {
        return this.strategy;
    }

    public void setStrategy(StorageStrategy strategy) {
        this.strategy = strategy;
    }
}

