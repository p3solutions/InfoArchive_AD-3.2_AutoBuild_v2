/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.fs.search.metadata;

import com.emc.documentum.xml.dds.fs.search.metadata.MetadataField;
import com.emc.documentum.xml.dds.fs.search.metadata.Range;
import com.emc.documentum.xml.dds.fs.search.metadata.SearchExpression;
import com.emc.documentum.xml.dds.fs.search.metadata.SortExpression;

public interface MetadataSearchQuery {
    public SearchExpression getSearchExpression();

    public Iterable<SortExpression> getSortExpressions();

    public Range getRange();

    public Iterable<MetadataField> getResultFields();
}

