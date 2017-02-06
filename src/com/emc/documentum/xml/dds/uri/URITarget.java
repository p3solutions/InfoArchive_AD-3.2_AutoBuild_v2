/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.uri;

import com.emc.documentum.xml.dds.persistence.Store;
import com.emc.documentum.xml.dds.persistence.StoreChild;
import com.emc.documentum.xml.dds.structure.DDSDataSet;
import com.emc.documentum.xml.dds.structure.DDSLocale;

public interface URITarget {
    public Store getStore();

    public DDSDataSet getDataSet();

    public DDSLocale getLocale();

    public StoreChild getStoreChild();
}

