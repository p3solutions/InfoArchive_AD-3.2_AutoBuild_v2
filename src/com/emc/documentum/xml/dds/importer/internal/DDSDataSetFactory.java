/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.importer.internal;

import com.emc.documentum.xml.dds.importer.internal.DDSDataImporter;
import java.util.List;

public interface DDSDataSetFactory {
    public DDSDataImporter getDataImporter(String var1);

    public void createDataSet(String var1, boolean var2);

    public void createDataSet(String var1, int var2, boolean var3);

    public void deleteDataSet(String var1);

    public void deleteDataSet(String var1, boolean var2);

    public List<String> getDataSetNames();
}

