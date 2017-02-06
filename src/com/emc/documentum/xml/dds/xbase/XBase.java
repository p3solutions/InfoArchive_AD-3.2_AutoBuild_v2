/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.xbase;

import com.emc.documentum.xml.dds.exception.DDSException;
import com.emc.documentum.xml.dds.persistence.Session;
import com.emc.documentum.xml.dds.persistence.Store;
import com.emc.documentum.xml.dds.persistence.xdb.internal.XDBStore;
import com.emc.documentum.xml.dds.persistence.xdb.internal.XDBStoreUser;
import com.emc.documentum.xml.dds.xbase.XBaseEntry;
import com.emc.documentum.xml.dds.xbase.XBaseFile;
import org.w3c.dom.Node;

public interface XBase {
    public String getId();

    public Store getStore();

    public void store(Session var1, XBaseEntry var2) throws DDSException;

    public void clear();

    public XBaseEntry newEntry(String var1);

    public XBaseEntry newEntry(Node var1);

    public XBaseFile newXBaseFile(XDBStore var1, XDBStoreUser var2, String var3, String var4);
}

