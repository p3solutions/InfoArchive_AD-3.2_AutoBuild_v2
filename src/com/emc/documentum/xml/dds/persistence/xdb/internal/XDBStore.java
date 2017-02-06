/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  com.xhive.core.interfaces.XhiveDriverIf
 *  com.xhive.core.interfaces.XhiveSessionIf
 */
package com.emc.documentum.xml.dds.persistence.xdb.internal;

import com.emc.documentum.xml.dds.configuration.persistence.StoreConfiguration;
import com.emc.documentum.xml.dds.configuration.persistence.xdb.XDBStoreConfiguration;
import com.emc.documentum.xml.dds.persistence.Container;
import com.emc.documentum.xml.dds.persistence.Location;
import com.emc.documentum.xml.dds.persistence.Session;
import com.emc.documentum.xml.dds.persistence.Store;
import com.emc.documentum.xml.dds.persistence.StoreType;
import com.emc.documentum.xml.dds.persistence.StoreUser;
import com.emc.documentum.xml.dds.persistence.XMLNode;
import com.emc.documentum.xml.dds.persistence.exception.StoreSpecificException;
import com.emc.documentum.xml.dds.persistence.internal.AbstractStore;
import com.emc.documentum.xml.dds.persistence.xdb.internal.XDBContainer;
import com.emc.documentum.xml.dds.persistence.xdb.internal.XDBLocation;
import com.emc.documentum.xml.dds.persistence.xdb.internal.XDBSession;
import com.emc.documentum.xml.dds.persistence.xdb.internal.XDBStoreUser;
import com.emc.documentum.xml.dds.persistence.xdb.internal.XDBXMLNode;
import com.emc.documentum.xml.dds.util.internal.FederationSupport;
import com.emc.documentum.xml.dds.xquery.XQueryExecutor;
import com.emc.documentum.xml.dds.xquery.xdb.XDBXQueryExecutor;
import com.xhive.core.interfaces.XhiveDriverIf;
import com.xhive.core.interfaces.XhiveSessionIf;
import java.util.List;
import org.w3c.dom.Node;

public final class XDBStore
extends AbstractStore {
    private static final Object DRIVERMUTEX = new Object();
    private final String databaseName;
    private final String bootstrap;
    private final int cachePages;
    private XhiveDriverIf driver;
    private XDBXQueryExecutor xqueryExecutor;
    private boolean initializedDriver;

    public XDBStore(XDBStoreConfiguration configuration) {
        super(configuration);
        this.databaseName = configuration.getDatabaseName();
        this.bootstrap = configuration.getBootstrap();
        this.cachePages = configuration.getCachePages();
    }

    public String getDatabaseName() {
        return this.databaseName;
    }

    public String getBootstrap() {
        return this.bootstrap;
    }

    public int getCachePages() {
        return this.cachePages;
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
    public Container getContainer(Location location, String name) {
        return new XDBContainer(new XDBLocation((Store)this, location.getPathComponents()), name);
    }

    @Override
    public Container getContainer(String path, String name) {
        return new XDBContainer(new XDBLocation((Store)this, path), name);
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
    public XQueryExecutor getXQueryExecutor() {
        if (this.xqueryExecutor == null) {
            this.xqueryExecutor = new XDBXQueryExecutor();
        }
        return this.xqueryExecutor;
    }

    @Override
    public Session getSession(StoreUser user, boolean readOnly) throws StoreSpecificException {
        XDBStoreUser xdbUser = (XDBStoreUser)user;
        if (xdbUser == null) {
            throw new StoreSpecificException("XDB Store : Tried to connect with null StoreUser.");
        }
        try {
            this.connect();
            XhiveSessionIf session = this.driver.createSession();
            xdbUser.connect(session, this.getDatabaseName());
            if (readOnly) {
                session.setReadOnlyMode(readOnly);
            }
            return new XDBSession(session, (XDBStoreUser)user, this);
        }
        catch (Exception e) {
            throw new StoreSpecificException("Connection to xDB Store failed : ", e);
        }
    }

    @Override
    public XDBStoreUser getDefaultStoreUser() {
        return (XDBStoreUser)super.getDefaultStoreUser();
    }

    @Override
    public StoreType getType() {
        return StoreType.XDB;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void connect() {
        Object object = DRIVERMUTEX;
        synchronized (object) {
            if (this.driver == null) {
                this.driver = FederationSupport.getInstance().getDriver(this.bootstrap);
            }
            if (!this.driver.isInitialized()) {
                this.initializedDriver = true;
                this.driver.init(this.cachePages);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void disconnect() {
        Object object = DRIVERMUTEX;
        synchronized (object) {
            if (this.driver == null) {
                return;
            }
            if (this.initializedDriver && this.driver.isInitialized()) {
                this.driver.close();
                this.initializedDriver = false;
                this.driver = null;
            }
        }
    }

    public XhiveDriverIf getDriver() {
        return this.driver;
    }
}

