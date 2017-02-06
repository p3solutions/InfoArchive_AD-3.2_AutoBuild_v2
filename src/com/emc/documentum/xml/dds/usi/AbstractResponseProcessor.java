/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.usi;

import com.emc.documentum.xml.dds.usi.ResponseProcessor;

public abstract class AbstractResponseProcessor
implements ResponseProcessor {
    private String id;

    @Override
    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }
}

