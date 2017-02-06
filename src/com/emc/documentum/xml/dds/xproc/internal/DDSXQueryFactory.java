/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  com.emc.documentum.xml.xproc.XProcConfiguration
 *  com.emc.documentum.xml.xproc.impl.api.xquery.XQuery
 *  com.emc.documentum.xml.xproc.impl.api.xquery.XQueryFactory
 *  com.emc.documentum.xml.xproc.util.ExtendedNamespaceContext
 */
package com.emc.documentum.xml.dds.xproc.internal;

import com.emc.documentum.xml.dds.xproc.internal.DDSXQuery;
import com.emc.documentum.xml.xproc.XProcConfiguration;
import com.emc.documentum.xml.xproc.impl.api.xquery.XQuery;
import com.emc.documentum.xml.xproc.impl.api.xquery.XQueryFactory;
import com.emc.documentum.xml.xproc.util.ExtendedNamespaceContext;

public class DDSXQueryFactory
extends XQueryFactory {
    private final XProcConfiguration config;

    @Deprecated
    public DDSXQueryFactory() {
        this(null);
    }

    public DDSXQueryFactory(XProcConfiguration config) {
        this.config = config;
    }

    public XQuery newXQuery(String expression, ExtendedNamespaceContext namespaceContext) {
        return new DDSXQuery(this.config, expression, namespaceContext);
    }
}

