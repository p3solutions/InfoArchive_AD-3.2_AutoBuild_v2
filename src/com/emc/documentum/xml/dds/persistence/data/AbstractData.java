/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.persistence.data;

import com.emc.documentum.xml.dds.persistence.Data;

public abstract class AbstractData<T>
implements Data<T> {
    private String mimeType;

    @Override
    public String getMimeType() {
        return this.mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }
}

