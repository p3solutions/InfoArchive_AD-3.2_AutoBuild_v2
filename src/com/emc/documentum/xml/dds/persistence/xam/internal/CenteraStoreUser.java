/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.persistence.xam.internal;

import com.emc.documentum.xml.dds.configuration.persistence.xam.CenteraStoreUserConfiguration;
import com.emc.documentum.xml.dds.persistence.StoreType;
import com.emc.documentum.xml.dds.persistence.StoreUser;
import com.emc.documentum.xml.dds.util.internal.StringUtils;

public class CenteraStoreUser
implements StoreUser {
    private final String peaFileName;
    private final String id;
    private final String password;

    public CenteraStoreUser(CenteraStoreUserConfiguration configuration) {
        this.peaFileName = configuration.getPeaFileName();
        if (this.peaFileName == null) {
            this.id = configuration.getId();
            this.password = configuration.getEncryptedPassword() != null ? StringUtils.decrypt(configuration.getEncryptedPassword()) : null;
        } else {
            this.id = null;
            this.password = null;
        }
    }

    public String getPEAFileName() {
        return this.peaFileName;
    }

    public String getId() {
        return this.id;
    }

    public String getPassword() {
        return this.password;
    }

    @Override
    public StoreType getStoreType() {
        return StoreType.XAM;
    }
}

