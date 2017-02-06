/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.persistence.data;

import com.emc.documentum.xml.dds.persistence.data.AbstractData;

public class ObjectData
extends AbstractData<Object> {
    private final Object object;

    public ObjectData(Object object) {
        this.object = object;
    }

    @Override
    public Object content() {
        return this.object;
    }
}

