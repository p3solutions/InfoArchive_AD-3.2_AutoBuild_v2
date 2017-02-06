/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.xbase.logbase;

import com.emc.documentum.xml.dds.persistence.Session;
import com.emc.documentum.xml.dds.xbase.XBaseFile;
import org.w3c.dom.Node;

public interface LogBaseFile
extends XBaseFile {
    public void addAsString(Session var1, String var2);

    public void addAsNode(Session var1, Node var2);

    public void createEntry(Session var1);

    public void closeEntry(Session var1);

    public void addPair(Session var1, String var2, String var3);

    public void addAttribute(Session var1, String var2, String var3);

    public void addElement(Session var1, String var2);

    public void closeElement(Session var1);

    public void addContent(Session var1, String var2);
}

