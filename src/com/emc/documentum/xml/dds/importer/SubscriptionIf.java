/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.importer;

import java.io.IOException;
import java.util.Map;

public interface SubscriptionIf {
    public static final short DOCUMENT_NODE = 9;
    public static final short BLOB_NODE = 203;
    public static final short LIBRARY_NODE = 201;

    public void addSpecialItems(String var1, String var2, boolean var3);

    public void removeNamedItem(String var1);

    public void removeCollectionItem(String var1);

    public void removeMetadataItem(String var1);

    public void truncateCollection();

    public void truncateCollectionMetadata();

    public void addNamedItem(String var1, String var2, String var3, String var4, short var5, boolean var6) throws IOException;

    public long addItemXMLMetadata(String var1, String var2, String var3, boolean var4, String var5, boolean var6) throws IOException;

    public boolean checkContentExists(String var1);

    public boolean checkMetadataExists(String var1);

    public void copyFallbackContent(String var1, String var2);

    public long copyFallbackMetadata(String var1, String var2);

    public void commit();

    public void checkpoint();

    public void rollback();

    public void terminate();

    public Map getCollectionItemXhiveMetadata(String var1);

    public Map getMetadataItemXhiveMetadata(String var1);

    public String getCollectionItemXhiveMetadata(String var1, String var2);

    public void setCollectionItemXhiveMetadata(String var1, String var2, String var3);
}

