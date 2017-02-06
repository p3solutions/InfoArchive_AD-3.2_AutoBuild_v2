/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.fs.search.metadata;

import com.emc.documentum.xml.dds.exception.DDSException;
import com.emc.documentum.xml.dds.fs.search.metadata.MetadataSearchContext;
import com.emc.documentum.xml.dds.fs.search.metadata.MetadataSearchProperties;
import com.emc.documentum.xml.dds.fs.search.metadata.MetadataSearchQuery;
import com.emc.documentum.xml.dds.fs.search.metadata.MetadataSearchResultHandler;
import com.emc.documentum.xml.dds.fs.search.metadata.QueryStringBuilder;

public interface MetadataSearchService {
    public <H, R> R search(MetadataSearchContext var1, MetadataSearchQuery var2, QueryStringBuilder var3, MetadataSearchProperties var4, MetadataSearchResultHandler<H, R> var5) throws DDSException;
}

