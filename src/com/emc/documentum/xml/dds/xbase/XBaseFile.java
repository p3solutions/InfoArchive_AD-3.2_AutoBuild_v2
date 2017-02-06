/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.xbase;

import com.emc.documentum.xml.dds.exception.DDSException;
import com.emc.documentum.xml.dds.persistence.Session;
import com.emc.documentum.xml.dds.xbase.XBaseEntry;

public interface XBaseFile {
    public void create(boolean var1);

    public void store(Session var1, XBaseEntry var2) throws DDSException;

    public void clear();

    public void close();
}

