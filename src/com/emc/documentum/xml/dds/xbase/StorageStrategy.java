/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.xbase;

import com.emc.documentum.xml.dds.persistence.Location;
import com.emc.documentum.xml.dds.persistence.xdb.internal.XDBStore;
import com.emc.documentum.xml.dds.persistence.xdb.internal.XDBStoreUser;
import com.emc.documentum.xml.dds.xbase.XBase;
import com.emc.documentum.xml.dds.xbase.XBaseEntry;
import com.emc.documentum.xml.dds.xbase.XBaseFile;

public interface StorageStrategy {
    public void prepare(XBase var1, XDBStore var2, XDBStoreUser var3, Location var4, String var5);

    public XBaseFile fileFor(XBaseEntry var1);
}

