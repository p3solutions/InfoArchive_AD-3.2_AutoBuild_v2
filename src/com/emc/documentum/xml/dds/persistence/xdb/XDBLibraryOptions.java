/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.persistence.xdb;

import com.emc.documentum.xml.dds.persistence.LocationOptions;
import com.emc.documentum.xml.dds.persistence.StoreType;

public class XDBLibraryOptions
implements LocationOptions {
    private boolean concurrentLibrary;
    private boolean lockWithParent;

    public XDBLibraryOptions(int libraryOptions) {
        this.setLibraryOptions(libraryOptions);
    }

    public boolean getConcurrentLibraryValue() {
        return this.concurrentLibrary;
    }

    public void setConcurrentLibraryValue(boolean option) {
        this.concurrentLibrary = option;
    }

    @Deprecated
    public boolean getConcurrentNameBaseValue() {
        return true;
    }

    @Deprecated
    public void setConcurrentNameBaseValue(boolean option) {
    }

    @Deprecated
    public boolean getDocumentsDoNotLockWithParentValue() {
        return true;
    }

    @Deprecated
    public void setDocumentsDoNotLockWithParentValue(boolean option) {
    }

    public boolean getLockWithParentValue() {
        return this.lockWithParent;
    }

    public void setLockWithParentValue(boolean option) {
        this.lockWithParent = option;
    }

    public int getLibraryOptions() {
        int result = 0;
        if (this.concurrentLibrary) {
            result |= 128;
        }
        if (this.lockWithParent) {
            result |= 1;
        }
        result |= 4;
        return result |= 64;
    }

    public void setLibraryOptions(int libraryOptions) {
        this.concurrentLibrary = (libraryOptions & 128) != 0;
        this.lockWithParent = (libraryOptions & 1) != 0;
    }

    @Override
    public StoreType getStoreType() {
        return StoreType.XDB;
    }
}

