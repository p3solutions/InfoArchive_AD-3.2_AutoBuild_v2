/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.configuration.persistence.esu;

import com.emc.documentum.xml.dds.configuration.persistence.StoreUserConfiguration;
import com.emc.documentum.xml.dds.persistence.StoreType;

public final class ESUStoreUserConfiguration
implements StoreUserConfiguration {
    private String uid;
    private String sharedSecret;

    public ESUStoreUserConfiguration(String uid, String sharedSecret) {
        this.uid = uid;
        this.sharedSecret = sharedSecret;
    }

    public String getUid() {
        return this.uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getSharedSecret() {
        return this.sharedSecret;
    }

    public void setSharedSecret(String sharedSecret) {
        this.sharedSecret = sharedSecret;
    }

    @Override
    public StoreType getType() {
        return StoreType.ESU;
    }
}

