/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.configuration.baseline;

import com.emc.documentum.xml.dds.configuration.baseline.StructureConfiguration;

public class DataSetConfiguration
implements StructureConfiguration {
    private String alias;
    private String storeAlias;
    private String id;
    private String defaultLocale;

    public DataSetConfiguration(String alias, String storeAlias, String id) {
        this.alias = alias;
        this.storeAlias = storeAlias;
        this.id = id;
    }

    public String getAlias() {
        return this.alias == null ? null : this.alias.trim();
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getStoreAlias() {
        return this.storeAlias == null ? null : this.storeAlias.trim();
    }

    public void setStoreAlias(String storeAlias) {
        this.storeAlias = storeAlias;
    }

    public String getId() {
        return this.id == null ? null : this.id.trim();
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDefaultLocale() {
        return this.defaultLocale == null ? null : this.defaultLocale.trim();
    }

    public void setDefaultLocale(String defaultLocale) {
        this.defaultLocale = defaultLocale;
    }
}

