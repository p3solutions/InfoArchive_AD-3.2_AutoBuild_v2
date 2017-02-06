/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.persistence.esu.internal;

import com.emc.documentum.xml.dds.DDS;
import com.emc.documentum.xml.dds.application.Application;
import com.emc.documentum.xml.dds.configuration.persistence.StoreConfiguration;
import com.emc.documentum.xml.dds.configuration.persistence.esu.ESUStoreConfiguration;
import com.emc.documentum.xml.dds.logging.LogCenter;
import com.emc.documentum.xml.dds.persistence.Container;
import com.emc.documentum.xml.dds.persistence.Location;
import com.emc.documentum.xml.dds.persistence.Session;
import com.emc.documentum.xml.dds.persistence.Store;
import com.emc.documentum.xml.dds.persistence.StoreType;
import com.emc.documentum.xml.dds.persistence.StoreUser;
import com.emc.documentum.xml.dds.persistence.XMLNode;
import com.emc.documentum.xml.dds.persistence.esu.internal.ESUContainer;
import com.emc.documentum.xml.dds.persistence.esu.internal.ESULocation;
import com.emc.documentum.xml.dds.persistence.esu.internal.ESUSession;
import com.emc.documentum.xml.dds.persistence.esu.internal.ESUStoreUser;
import com.emc.documentum.xml.dds.persistence.esu.internal.ESUXMLNode;
import com.emc.documentum.xml.dds.persistence.exception.StoreSpecificException;
import com.emc.documentum.xml.dds.persistence.internal.AbstractStore;
import com.emc.documentum.xml.dds.persistence.registry.vfs.esu.ESUContentRegistry;
import com.emc.documentum.xml.dds.xquery.XQueryExecutor;
import java.util.List;
import org.w3c.dom.Node;

public class ESUStore
extends AbstractStore {
    private final String host;
    private final Integer port;
    private final ESUContentRegistry contentRegistry;

    public ESUStore(ESUStoreConfiguration configuration) {
        super(configuration);
        this.host = configuration.getHost();
        this.port = configuration.getPort();
        Application app = DDS.getApplication();
        if (app != null) {
            Store crStore = app.getStore(configuration.getContentRegistryStoreAlias());
            this.contentRegistry = new ESUContentRegistry(app, crStore.getLocation("/SYSTEM/CR/ESU" + configuration.getContentRegistryName()));
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

    public String getHost() {
        return this.host;
    }

    public Integer getPort() {
        return this.port;
    }

    public Location getCRLocation() {
        return this.contentRegistry.getContentRegistryLocation();
    }

    public ESUContentRegistry getContentRegistry() {
        return this.contentRegistry;
    }

    @Override
    public Container getContainer(Location location, String name) {
        return new ESUContainer(location, name);
    }

    @Override
    public Container getContainer(String path, String name) {
        return new ESUContainer(new ESULocation((Store)this, path), name);
    }

    @Override
    public Location getLocation(List<String> pathComponents) {
        return new ESULocation((Store)this, pathComponents);
    }

    @Override
    public Location getLocation(String path) {
        return new ESULocation((Store)this, path);
    }

    @Override
    public Session getSession(StoreUser storeUser, boolean readOnly) throws StoreSpecificException {
        return new ESUSession((ESUStoreUser)storeUser, this);
    }

    @Override
    public StoreType getType() {
        return StoreType.ESU;
    }

    @Override
    public XQueryExecutor getXQueryExecutor() {
        return null;
    }

    @Override
    public XMLNode getXMLNode(Location location) {
        return new ESUXMLNode(location);
    }

    @Override
    public XMLNode getXMLNode(Container container, String xpointer) {
        return new ESUXMLNode(container, xpointer);
    }

    @Override
    public XMLNode getXMLNode(Location location, Container container, String xpointer, Node node) {
        return new ESUXMLNode(location, container, xpointer, node);
    }
}

