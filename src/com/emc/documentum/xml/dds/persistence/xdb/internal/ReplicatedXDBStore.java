/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.persistence.xdb.internal;

import com.emc.documentum.xml.dds.configuration.persistence.StoreConfiguration;
import com.emc.documentum.xml.dds.configuration.persistence.xdb.ReplicatedXDBStoreConfiguration;
import com.emc.documentum.xml.dds.configuration.persistence.xdb.XDBStoreConfiguration;
import com.emc.documentum.xml.dds.persistence.Container;
import com.emc.documentum.xml.dds.persistence.Location;
import com.emc.documentum.xml.dds.persistence.Session;
import com.emc.documentum.xml.dds.persistence.Store;
import com.emc.documentum.xml.dds.persistence.StoreFactory;
import com.emc.documentum.xml.dds.persistence.StoreType;
import com.emc.documentum.xml.dds.persistence.StoreUser;
import com.emc.documentum.xml.dds.persistence.XMLNode;
import com.emc.documentum.xml.dds.persistence.exception.StoreSpecificException;
import com.emc.documentum.xml.dds.persistence.internal.AbstractStore;
import com.emc.documentum.xml.dds.persistence.xdb.ReplicaSelector;
import com.emc.documentum.xml.dds.persistence.xdb.internal.ReplicatedXDBSession;
import com.emc.documentum.xml.dds.persistence.xdb.internal.XDBContainer;
import com.emc.documentum.xml.dds.persistence.xdb.internal.XDBLocation;
import com.emc.documentum.xml.dds.persistence.xdb.internal.XDBStore;
import com.emc.documentum.xml.dds.persistence.xdb.internal.XDBXMLNode;
import com.emc.documentum.xml.dds.xquery.XQueryExecutor;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.w3c.dom.Node;

public final class ReplicatedXDBStore
extends AbstractStore {
    private final XDBStore master;
    private final Map<String, XDBStore> slaves;
    private final ReplicaSelector replicaSelector;
    private final long timeout;

    public ReplicatedXDBStore(ReplicatedXDBStoreConfiguration configuration) {
        super(configuration);
        this.master = (XDBStore)StoreFactory.newStore(configuration.getMaster());
        this.master.setDefaultStoreUser(this.getDefaultStoreUser());
        this.slaves = new HashMap<String, XDBStore>();
        for (XDBStoreConfiguration slaveConfiguration : configuration.getSlaves()) {
            slaveConfiguration.setDatabaseName(configuration.getMaster().getDatabaseName());
            XDBStore slave = (XDBStore)StoreFactory.newStore(slaveConfiguration);
            slave.setDefaultStoreUser(this.getDefaultStoreUser());
            this.slaves.put(slaveConfiguration.getAlias(), slave);
        }
        this.timeout = configuration.getTimeout();
        this.replicaSelector = configuration.getReplicaSelector();
        this.replicaSelector.initialize(this);
    }

    public XDBStore getMaster() {
        return this.master;
    }

    public Collection<XDBStore> getSlaves() {
        return this.slaves.values();
    }

    public XDBStore getSlave(String alias) {
        return this.slaves.get(alias);
    }

    @Override
    public void setDefaultStoreUser(StoreUser storeUser) {
        super.setDefaultStoreUser(storeUser);
        this.master.setDefaultStoreUser(storeUser);
        for (Store slave : this.slaves.values()) {
            slave.setDefaultStoreUser(storeUser);
        }
    }

    @Override
    public Session getSession(StoreUser storeUser, boolean readOnly) throws StoreSpecificException {
        return new ReplicatedXDBSession(this, readOnly ? this.replicaSelector.pickStore().getSession(storeUser, true) : this.master.getSession(storeUser, false), this.timeout);
    }

    @Override
    public Container getContainer(Location location, String name) {
        return new XDBContainer(new XDBLocation((Store)this, location.getPathComponents()), name);
    }

    @Override
    public Container getContainer(String path, String name) {
        return new XDBContainer(new XDBLocation((Store)this, path), name);
    }

    @Override
    public Location getLocation(List<String> pathComponents) {
        return new XDBLocation((Store)this, pathComponents);
    }

    @Override
    public Location getLocation(String path) {
        return new XDBLocation((Store)this, path);
    }

    @Override
    public XMLNode getXMLNode(Location location) {
        return new XDBXMLNode(location);
    }

    @Override
    public XMLNode getXMLNode(Container container, String xpointer) {
        return new XDBXMLNode(null, container, xpointer, null);
    }

    @Override
    public XMLNode getXMLNode(Location location, Container container, String xpointer, Node node) {
        return new XDBXMLNode(location, container, xpointer, node);
    }

    @Override
    public StoreType getType() {
        return StoreType.REPLICATEDXDB;
    }

    @Override
    public XQueryExecutor getXQueryExecutor() {
        return this.master.getXQueryExecutor();
    }
}

