/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.persistence.content;

import com.emc.documentum.xml.dds.persistence.ContentDescriptor;
import com.emc.documentum.xml.dds.serialization.DefaultDDSSerializer;
import com.emc.documentum.xml.dds.serialization.Serializer;

public class ObjectContentDescriptor
implements ContentDescriptor {
    private final Serializer serializer;

    public ObjectContentDescriptor() {
        this.serializer = new DefaultDDSSerializer();
    }

    public ObjectContentDescriptor(Serializer serializer) {
        this.serializer = serializer == null ? new DefaultDDSSerializer() : serializer;
    }

    public Serializer getSerializer() {
        return this.serializer;
    }

    @Override
    public boolean isXML() {
        return true;
    }
}

