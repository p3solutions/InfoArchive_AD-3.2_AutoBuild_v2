/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.persistence;

import com.emc.documentum.xml.dds.configuration.persistence.StoreConfiguration;
import com.emc.documentum.xml.dds.configuration.persistence.esu.ESUStoreConfiguration;
import com.emc.documentum.xml.dds.configuration.persistence.filesystem.FileSystemStoreConfiguration;
import com.emc.documentum.xml.dds.configuration.persistence.filesystem.UnixStoreConfiguration;
import com.emc.documentum.xml.dds.configuration.persistence.filesystem.WindowsStoreConfiguration;
import com.emc.documentum.xml.dds.configuration.persistence.filesystem.WindowsUNCStoreConfiguration;
import com.emc.documentum.xml.dds.configuration.persistence.xam.XAMStoreConfiguration;
import com.emc.documentum.xml.dds.configuration.persistence.xdb.ReplicatedXDBStoreConfiguration;
import com.emc.documentum.xml.dds.configuration.persistence.xdb.XDBStoreConfiguration;
import com.emc.documentum.xml.dds.persistence.Store;
import com.emc.documentum.xml.dds.persistence.StoreType;
import com.emc.documentum.xml.dds.persistence.esu.internal.ESUStore;
import com.emc.documentum.xml.dds.persistence.filesystem.internal.FileSystemType;
import com.emc.documentum.xml.dds.persistence.filesystem.internal.UnixStore;
import com.emc.documentum.xml.dds.persistence.filesystem.internal.WindowsStore;
import com.emc.documentum.xml.dds.persistence.filesystem.internal.WindowsUNCStore;
import com.emc.documentum.xml.dds.persistence.xam.internal.XAMStore;
import com.emc.documentum.xml.dds.persistence.xdb.internal.ReplicatedXDBStore;
import com.emc.documentum.xml.dds.persistence.xdb.internal.XDBStore;

public final class StoreFactory {
    private StoreFactory() {
    }

    public static Store newStore(StoreConfiguration configuration) {
        if (configuration == null) {
            return null;
        }
        switch (configuration.getType()) {
            case XDB: {
                return new XDBStore((XDBStoreConfiguration)configuration);
            }
            case REPLICATEDXDB: {
                return new ReplicatedXDBStore((ReplicatedXDBStoreConfiguration)configuration);
            }
            case XAM: {
                return new XAMStore((XAMStoreConfiguration)configuration);
            }
            case FILESYSTEM: {
                switch (((FileSystemStoreConfiguration)configuration).getFileSystemType()) {
                    case UNIX: {
                        return new UnixStore((UnixStoreConfiguration)configuration);
                    }
                    case WINDOWS: {
                        return new WindowsStore((WindowsStoreConfiguration)configuration);
                    }
                    case WINDOWS_UNC: {
                        return new WindowsUNCStore((WindowsUNCStoreConfiguration)configuration);
                    }
                }
                return null;
            }
            case ESU: {
                return new ESUStore((ESUStoreConfiguration)configuration);
            }
        }
        return null;
    }

}

