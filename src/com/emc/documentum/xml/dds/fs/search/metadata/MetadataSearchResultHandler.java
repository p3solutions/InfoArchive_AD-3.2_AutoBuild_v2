/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.fs.search.metadata;

import com.emc.documentum.xml.dds.exception.DDSException;

public interface MetadataSearchResultHandler<T, R> {
    public R transformSearchResult(T var1) throws DDSException;
}

