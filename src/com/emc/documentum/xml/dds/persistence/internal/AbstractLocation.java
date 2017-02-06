/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.persistence.internal;

import com.emc.documentum.xml.dds.persistence.Container;
import com.emc.documentum.xml.dds.persistence.Location;
import com.emc.documentum.xml.dds.persistence.Store;
import com.emc.documentum.xml.dds.persistence.StoreType;
import com.emc.documentum.xml.dds.persistence.internal.AbstractStoreChild;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;

public abstract class AbstractLocation
extends AbstractStoreChild
implements Location {
    private final Store store;
    private final List<String> pathComponents;

    protected AbstractLocation(Store store) {
        this.store = store;
        this.pathComponents = new ArrayList<String>();
    }

    protected AbstractLocation(Store store, List<String> pathComponents) {
        this.store = store;
        this.pathComponents = new ArrayList<String>(pathComponents);
    }

    protected AbstractLocation(Store store, String path) {
        this.store = store;
        this.pathComponents = AbstractLocation.parsePath(path);
    }

    @Override
    public String getName() {
        if (this.pathComponents.size() == 0) {
            return "";
        }
        return this.pathComponents.get(this.pathComponents.size() - 1);
    }

    @Override
    public String getPath() {
        return this.getPath(true);
    }

    @Override
    public String getPath(boolean trailingSeparator) {
        if (this.pathComponents.size() == 0) {
            return this.getStoreType().getSeparator();
        }
        StringBuilder path = new StringBuilder();
        for (String pathComponent : this.pathComponents) {
            path.append(this.getStoreType().getSeparator()).append(pathComponent);
        }
        if (trailingSeparator) {
            path.append(this.getStoreType().getSeparator());
        }
        return path.toString();
    }

    @Override
    public String getCanonicalPath() {
        return this.getCanonicalPath(true);
    }

    public String getCanonicalPath(boolean trailingSeparator) {
        if (this.pathComponents.size() == 0) {
            return "/";
        }
        StringBuilder path = new StringBuilder();
        for (String pathComponent : this.pathComponents) {
            path.append("/").append(pathComponent);
        }
        if (trailingSeparator) {
            path.append("/");
        }
        return path.toString();
    }

    @Override
    public List<String> getPathComponents() {
        return new ArrayList<String>(this.pathComponents);
    }

    @Override
    public boolean isRoot() {
        return this.pathComponents.size() == 0;
    }

    public String getStoreAlias() {
        return this.store.getAlias();
    }

    @Override
    public StoreType getStoreType() {
        return this.store.getType();
    }

    @Override
    public Store getStore() {
        return this.store;
    }

    @Override
    public boolean isLocation() {
        return true;
    }

    @Override
    public boolean isContainer() {
        return false;
    }

    @Override
    public boolean isXMLNode() {
        return false;
    }

    @Override
    public Location getDescendantLocation(String relativePath) {
        return this.getDescendantLocation(AbstractLocation.parsePath(relativePath));
    }

    @Override
    public Container getDescendantContainer(String relativePath, String name) {
        return this.getDescendantLocation(AbstractLocation.parsePath(relativePath)).getChildContainer(name);
    }

    public static final List<String> parsePath(String path) {
        ArrayList<String> components = new ArrayList<String>();
        if (path == null) {
            return components;
        }
        StringTokenizer tokenizer = new StringTokenizer(path, "/");
        while (tokenizer.hasMoreTokens()) {
            String nextToken = tokenizer.nextToken();
            if ("".equals(nextToken)) continue;
            components.add(nextToken);
        }
        return components;
    }
}

