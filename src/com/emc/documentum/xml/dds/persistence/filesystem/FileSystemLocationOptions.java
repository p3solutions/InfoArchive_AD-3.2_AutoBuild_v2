/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.persistence.filesystem;

import com.emc.documentum.xml.dds.persistence.LocationOptions;
import com.emc.documentum.xml.dds.persistence.StoreType;

public class FileSystemLocationOptions
implements LocationOptions {
    @Override
    public StoreType getStoreType() {
        return StoreType.FILESYSTEM;
    }
}

