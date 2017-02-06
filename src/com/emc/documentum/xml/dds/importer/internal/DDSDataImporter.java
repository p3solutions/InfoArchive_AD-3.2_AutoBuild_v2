/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.importer.internal;

import com.emc.documentum.xml.dds.util.internal.ImportSettings;
import java.io.IOException;
import java.io.InputStream;

public interface DDSDataImporter {
    public boolean itemExists(String var1);

    public void removeCompleteItem(String var1);

    public void removeItem(String var1);

    public boolean metadataItemExists(String var1);

    public void removeMetadataItem(String var1);

    public void addItem(InputStream var1, String var2, String var3, String var4, short var5, ImportSettings var6) throws IOException;

    public long addMetadataItem(InputStream var1, String var2, String var3, boolean var4, ImportSettings var5);
}

