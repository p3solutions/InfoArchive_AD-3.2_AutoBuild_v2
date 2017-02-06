/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.fs.search.metadata;

import com.emc.documentum.xml.dds.fs.search.metadata.LogicalOperator;
import com.emc.documentum.xml.dds.fs.search.metadata.SearchExpression;

public interface LogicalExpression
extends SearchExpression {
    public Iterable<SearchExpression> getSearchExpressions();

    public LogicalOperator getOperator();
}

