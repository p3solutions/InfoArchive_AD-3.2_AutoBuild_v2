/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.fs.search.metadata;

import com.emc.documentum.xml.dds.fs.search.metadata.FieldOperator;
import com.emc.documentum.xml.dds.fs.search.metadata.MetadataField;
import com.emc.documentum.xml.dds.fs.search.metadata.MetadataFieldValue;
import com.emc.documentum.xml.dds.fs.search.metadata.SearchExpression;

public interface FieldExpression
extends SearchExpression {
    public FieldOperator getOperator();

    public MetadataField getMetadataField();

    public MetadataFieldValue getMetadataFieldValue();
}

