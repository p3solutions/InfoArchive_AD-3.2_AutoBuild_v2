/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.persistence.xam.internal;

import com.emc.documentum.xml.dds.DDS;
import com.emc.documentum.xml.dds.application.Application;
import com.emc.documentum.xml.dds.configuration.persistence.StoreConfiguration;
import com.emc.documentum.xml.dds.configuration.persistence.xam.XAMStoreConfiguration;
import com.emc.documentum.xml.dds.logging.LogCenter;
import com.emc.documentum.xml.dds.persistence.Container;
import com.emc.documentum.xml.dds.persistence.Location;
import com.emc.documentum.xml.dds.persistence.Session;
import com.emc.documentum.xml.dds.persistence.Store;
import com.emc.documentum.xml.dds.persistence.StoreType;
import com.emc.documentum.xml.dds.persistence.StoreUser;
import com.emc.documentum.xml.dds.persistence.XMLNode;
import com.emc.documentum.xml.dds.persistence.exception.StoreSpecificException;
import com.emc.documentum.xml.dds.persistence.internal.AbstractStore;
import com.emc.documentum.xml.dds.persistence.registry.vfs.xam.XAMContentRegistry;
import com.emc.documentum.xml.dds.persistence.xam.internal.CenteraStoreUser;
import com.emc.documentum.xml.dds.persistence.xam.internal.XAMContainer;
import com.emc.documentum.xml.dds.persistence.xam.internal.XAMLocation;
import com.emc.documentum.xml.dds.persistence.xam.internal.XAMSession;
import com.emc.documentum.xml.dds.persistence.xam.internal.XAMXMLNode;
import com.emc.documentum.xml.dds.xquery.XQueryExecutor;
import java.util.List;
import org.w3c.dom.Node;

public class XAMStore
extends AbstractStore {
    private final String connectionString;
    private final XAMContentRegistry contentRegistry;

    public XAMStore(XAMStoreConfiguration configuration) {
        super(configuration);
        this.connectionString = configuration.getConnectionString();
        Application app = DDS.getApplication();
        if (app != null) {
            Store crStore = app.getStore(configuration.getContentRegistryStoreAlias());
            this.contentRegistry = new XAMContentRegistry(app, crStore.getLocation("/SYSTEM/CR/" + configuration.getContentRegistryName()));
            try {
                this.contentRegistry.initialize();
            }
            catch (Exception de) {
                LogCenter.error("Could not initialize Content Registry.");
            }
        } else {
            this.contentRegistry = null;
        }
    }

    public String getConnectionString() {
        return this.connectionString;
    }

    public Location getCRLocation() {
        return this.contentRegistry.getContentRegistryLocation();
    }

    public XAMContentRegistry getContentRegistry() {
        return this.contentRegistry;
    }

    @Override
    public Container getContainer(Location location, String name) {
        return new XAMContainer(location, name);
    }

    @Override
    public Container getContainer(String path, String name) {
        return new XAMContainer(new XAMLocation((Store)this, path), name);
    }

    @Override
    public Location getLocation(List<String> pathComponents) {
        return new XAMLocation((Store)this, pathComponents);
    }

    @Override
    public Location getLocation(String path) {
        return new XAMLocation((Store)this, path);
    }

    @Override
    public XMLNode getXMLNode(Location location) {
        return new XAMXMLNode(location);
    }

    @Override
    public XMLNode getXMLNode(Container container, String xpointer) {
        return new XAMXMLNode(null, container, xpointer, null);
    }

    @Override
    public XMLNode getXMLNode(Location location, Container container, String xpointer, Node node) {
        return new XAMXMLNode(location, container, xpointer, node);
    }

    @Override
    public Session getSession(StoreUser storeUser, boolean readOnly) throws StoreSpecificException {
        return new XAMSession((CenteraStoreUser)storeUser, this);
    }

    @Override
    public XQueryExecutor getXQueryExecutor() {
        return null;
    }

    @Override
    public StoreType getType() {
        return StoreType.XAM;
    }
}

