/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.structure;

import com.emc.documentum.xml.dds.structure.DDSDataSet;
import com.emc.documentum.xml.dds.structure.Structure;
import java.util.Locale;

public interface DDSLocale
extends Structure {
    public DDSDataSet getDataSet();

    public Locale getJavaLocale();
}

