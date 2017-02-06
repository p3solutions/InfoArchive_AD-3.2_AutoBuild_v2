/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.fs.xquery;

import com.emc.documentum.xml.dds.exception.DDSException;

public interface XQueryResultHandler<T, R> {
    public R transformXQueryResult(T var1) throws DDSException;
}

