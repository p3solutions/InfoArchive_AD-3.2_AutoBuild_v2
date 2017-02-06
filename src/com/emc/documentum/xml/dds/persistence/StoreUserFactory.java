/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.persistence;

import com.emc.documentum.xml.dds.configuration.persistence.StoreUserConfiguration;
import com.emc.documentum.xml.dds.configuration.persistence.esu.ESUStoreUserConfiguration;
import com.emc.documentum.xml.dds.configuration.persistence.filesystem.FileSystemStoreUserConfiguration;
import com.emc.documentum.xml.dds.configuration.persistence.xam.CenteraStoreUserConfiguration;
import com.emc.documentum.xml.dds.configuration.persistence.xdb.XDBStoreUserConfiguration;
import com.emc.documentum.xml.dds.persistence.StoreType;
import com.emc.documentum.xml.dds.persistence.StoreUser;
import com.emc.documentum.xml.dds.persistence.esu.internal.ESUStoreUser;
import com.emc.documentum.xml.dds.persistence.filesystem.internal.FileSystemStoreUser;
import com.emc.documentum.xml.dds.persistence.xam.internal.CenteraStoreUser;
import com.emc.documentum.xml.dds.persistence.xdb.internal.XDBStoreUser;

public final class StoreUserFactory {
    private StoreUserFactory() {
    }

    public static StoreUser newStoreUser(StoreUserConfiguration configuration) {
        if (configuration == null) {
            return null;
        }
        switch (configuration.getType()) {
            case XDB: {
                return new XDBStoreUser((XDBStoreUserConfiguration)configuration);
            }
            case XAM: {
                return new CenteraStoreUser((CenteraStoreUserConfiguration)configuration);
            }
            case FILESYSTEM: {
                return new FileSystemStoreUser((FileSystemStoreUserConfiguration)configuration);
            }
            case ESU: {
                return new ESUStoreUser((ESUStoreUserConfiguration)configuration);
            }
        }
        return null;
    }

}

