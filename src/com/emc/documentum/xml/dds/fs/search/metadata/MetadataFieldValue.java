/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.fs.search.metadata;

import com.emc.documentum.xml.dds.fs.search.metadata.MetadataFieldType;

public interface MetadataFieldValue {
    public String getStringValue();

    public MetadataFieldType getMetadataFieldType();
}

