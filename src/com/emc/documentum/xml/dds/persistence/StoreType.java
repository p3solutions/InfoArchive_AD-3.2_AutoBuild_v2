/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.persistence;

import java.io.File;

public enum StoreType {
    XDB("/"),
    REPLICATEDXDB("/"),
    FILESYSTEM(File.separator),
    XAM("/"),
    ESU("/");
    
    private String separator;

    private StoreType(String separator) {
        this.separator = separator;
    }

    public String getSeparator() {
        return this.separator;
    }
}

