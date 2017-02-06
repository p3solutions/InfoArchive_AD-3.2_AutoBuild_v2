/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.persistence;

import com.emc.documentum.xml.dds.configuration.Configuration;
import com.emc.documentum.xml.dds.persistence.Container;
import com.emc.documentum.xml.dds.persistence.Location;
import com.emc.documentum.xml.dds.persistence.Session;
import com.emc.documentum.xml.dds.persistence.StoreType;
import com.emc.documentum.xml.dds.persistence.StoreUser;
import com.emc.documentum.xml.dds.persistence.XMLNode;
import com.emc.documentum.xml.dds.persistence.exception.StoreSpecificException;
import com.emc.documentum.xml.dds.structure.internal.RootStructure;
import com.emc.documentum.xml.dds.xquery.XQueryExecutor;
import java.util.List;
import org.w3c.dom.Node;

public interface Store
extends Configuration {
    public String getAlias();

    public StoreType getType();

    public String getSeparator();

    public StoreUser getDefaultStoreUser();

    public void setDefaultStoreUser(StoreUser var1);

    public Session getSession(StoreUser var1, boolean var2) throws StoreSpecificException;

    public XQueryExecutor getXQueryExecutor();

    public Location getLocation(List<String> var1);

    public Location getLocation(String var1);

    public Container getContainer(Location var1, String var2);

    public Container getContainer(String var1, String var2);

    public XMLNode getXMLNode(Location var1);

    public XMLNode getXMLNode(Container var1, String var2);

    public XMLNode getXMLNode(Location var1, Container var2, String var3, Node var4);

    public RootStructure getRootStructure();
}

