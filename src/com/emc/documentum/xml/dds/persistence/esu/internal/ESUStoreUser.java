/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.persistence.esu.internal;

import com.emc.documentum.xml.dds.configuration.persistence.esu.ESUStoreUserConfiguration;
import com.emc.documentum.xml.dds.persistence.StoreType;
import com.emc.documentum.xml.dds.persistence.StoreUser;

public class ESUStoreUser
implements StoreUser {
    private final String uid;
    private final String sharedSecret;

    public ESUStoreUser(ESUStoreUserConfiguration configuration) {
        this.uid = configuration.getUid();
        this.sharedSecret = configuration.getSharedSecret();
    }

    public String getUid() {
        return this.uid;
    }

    public String getSharedSecret() {
        return this.sharedSecret;
    }

    @Override
    public StoreType getStoreType() {
        return StoreType.ESU;
    }
}

