/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.application;

import com.emc.documentum.xml.dds.configuration.Configurable;
import com.emc.documentum.xml.dds.configuration.baseline.XBaseConfiguration;
import com.emc.documentum.xml.dds.xbase.XBase;
import java.util.List;

public interface XBaseManager
extends Configurable {
    public XBase getXBase(String var1);

    public List<XBase> getXBases();

    public XBase addXBase(XBaseConfiguration var1);
}

