/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.fs.search.metadata;

import com.emc.documentum.xml.dds.fs.search.metadata.MetadataField;
import com.emc.documentum.xml.dds.fs.search.metadata.MetadataFieldType;
import com.emc.documentum.xml.dds.fs.search.metadata.SortOrder;

public interface SortExpression {
    public MetadataField getMetadataField();

    public MetadataFieldType getMetadataFieldType();

    public SortOrder getSortOrder();
}

