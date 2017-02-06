/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.fs.xquery;

import com.emc.documentum.xml.dds.exception.DDSException;
import com.emc.documentum.xml.dds.fs.xquery.XQuery;
import com.emc.documentum.xml.dds.fs.xquery.XQueryContext;
import com.emc.documentum.xml.dds.fs.xquery.XQueryProperties;
import com.emc.documentum.xml.dds.fs.xquery.XQueryResultHandler;

public interface XQueryService {
    public <H, R> R runXQuery(XQueryContext var1, XQuery var2, XQueryProperties var3, XQueryResultHandler<H, R> var4) throws DDSException;
}

