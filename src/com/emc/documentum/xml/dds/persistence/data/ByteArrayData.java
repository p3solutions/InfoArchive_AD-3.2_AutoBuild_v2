/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.persistence.data;

import com.emc.documentum.xml.dds.persistence.data.AbstractData;

public class ByteArrayData
extends AbstractData<byte[]> {
    private final byte[] bytes;

    public ByteArrayData(byte[] bytes) {
        this.bytes = bytes;
    }

    @Override
    public byte[] content() {
        return this.bytes;
    }
}

