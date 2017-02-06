/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.persistence.filesystem.internal;

import com.emc.documentum.xml.dds.configuration.persistence.StoreConfiguration;
import com.emc.documentum.xml.dds.configuration.persistence.filesystem.FileSystemStoreConfiguration;
import com.emc.documentum.xml.dds.persistence.Container;
import com.emc.documentum.xml.dds.persistence.Location;
import com.emc.documentum.xml.dds.persistence.Session;
import com.emc.documentum.xml.dds.persistence.Store;
import com.emc.documentum.xml.dds.persistence.StoreType;
import com.emc.documentum.xml.dds.persistence.StoreUser;
import com.emc.documentum.xml.dds.persistence.XMLNode;
import com.emc.documentum.xml.dds.persistence.exception.StoreSpecificException;
import com.emc.documentum.xml.dds.persistence.filesystem.internal.FileSystemContainer;
import com.emc.documentum.xml.dds.persistence.filesystem.internal.FileSystemLocation;
import com.emc.documentum.xml.dds.persistence.filesystem.internal.FileSystemSession;
import com.emc.documentum.xml.dds.persistence.filesystem.internal.FileSystemType;
import com.emc.documentum.xml.dds.persistence.filesystem.internal.FileSystemXMLNode;
import com.emc.documentum.xml.dds.persistence.internal.AbstractStore;
import com.emc.documentum.xml.dds.xquery.XQueryExecutor;
import java.util.List;
import org.w3c.dom.Node;

public abstract class FileSystemStore
extends AbstractStore {
    private final String virtualRoot;

    public FileSystemStore(FileSystemStoreConfiguration configuration) {
        super(configuration);
        this.virtualRoot = configuration.getVirtualRoot();
    }

    public abstract FileSystemType getFileSystemType();

    public abstract String getPrefix();

    public String getVirtualRoot() {
        return this.virtualRoot;
    }

    @Override
    public Container getContainer(Location location, String name) {
        return new FileSystemContainer(location, name);
    }

    @Override
    public Container getContainer(String path, String name) {
        return new FileSystemContainer(new FileSystemLocation((Store)this, path), name);
    }

    @Override
    public Location getLocation(List<String> pathComponents) {
        return new FileSystemLocation((Store)this, pathComponents);
    }

    @Override
    public Location getLocation(String path) {
        return new FileSystemLocation((Store)this, path);
    }

    @Override
    public XMLNode getXMLNode(Location location) {
        return new FileSystemXMLNode(location);
    }

    @Override
    public XMLNode getXMLNode(Container container, String xpointer) {
        return new FileSystemXMLNode(null, container, xpointer, null);
    }

    @Override
    public XMLNode getXMLNode(Location location, Container container, String xpointer, Node node) {
        return new FileSystemXMLNode(location, container, xpointer, node);
    }

    @Override
    public Session getSession(StoreUser storeUser, boolean readOnly) throws StoreSpecificException {
        return new FileSystemSession(storeUser, this);
    }

    @Override
    public XQueryExecutor getXQueryExecutor() {
        return null;
    }

    @Override
    public StoreType getType() {
        return StoreType.FILESYSTEM;
    }
}

