/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.xbase.logbase;

import com.emc.documentum.xml.dds.xbase.XBase;
import com.emc.documentum.xml.dds.xbase.XBaseEntry;
import java.util.Map;

public interface LogBase
extends XBase {
    public XBaseEntry newEntry(Map<String, String> var1);
}

