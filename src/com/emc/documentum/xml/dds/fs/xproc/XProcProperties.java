/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.fs.xproc;

import com.emc.documentum.xml.dds.fs.Property;

public interface XProcProperties
extends Iterable<Property> {
    public boolean isReadOnly();

    public Property get(String var1);
}

