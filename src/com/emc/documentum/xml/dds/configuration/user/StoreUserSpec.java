/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.configuration.user;

import com.emc.documentum.xml.dds.configuration.persistence.StoreUserConfiguration;

public class StoreUserSpec {
    private String storeAlias;
    private StoreUserConfiguration storeUser;

    public StoreUserSpec(String storeAlias, StoreUserConfiguration storeUser) {
        this.storeAlias = storeAlias;
        this.storeUser = storeUser;
    }

    public String getStoreAlias() {
        return this.storeAlias;
    }

    public void setStoreAlias(String storeAlias) {
        this.storeAlias = storeAlias;
    }

    public StoreUserConfiguration getStoreUser() {
        return this.storeUser;
    }

    public void setStoreUser(StoreUserConfiguration storeUser) {
        this.storeUser = storeUser;
    }
}

