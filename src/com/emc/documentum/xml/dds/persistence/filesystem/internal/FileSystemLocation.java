/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.persistence.filesystem.internal;

import com.emc.documentum.xml.dds.persistence.Container;
import com.emc.documentum.xml.dds.persistence.Location;
import com.emc.documentum.xml.dds.persistence.LocationOptions;
import com.emc.documentum.xml.dds.persistence.Session;
import com.emc.documentum.xml.dds.persistence.Store;
import com.emc.documentum.xml.dds.persistence.StoreChild;
import com.emc.documentum.xml.dds.persistence.StoreType;
import com.emc.documentum.xml.dds.persistence.XMLNode;
import com.emc.documentum.xml.dds.persistence.exception.DeadlockException;
import com.emc.documentum.xml.dds.persistence.exception.IllegalActionException;
import com.emc.documentum.xml.dds.persistence.exception.LocationAlreadyExistsException;
import com.emc.documentum.xml.dds.persistence.exception.LocationNotFoundException;
import com.emc.documentum.xml.dds.persistence.exception.StoreSpecificException;
import com.emc.documentum.xml.dds.persistence.exception.TypeConflictException;
import com.emc.documentum.xml.dds.persistence.filesystem.internal.FileSystemContainer;
import com.emc.documentum.xml.dds.persistence.filesystem.internal.FileSystemLocationUtil;
import com.emc.documentum.xml.dds.persistence.filesystem.internal.FileSystemStore;
import com.emc.documentum.xml.dds.persistence.filesystem.internal.FileSystemXMLNode;
import com.emc.documentum.xml.dds.persistence.internal.AbstractLocation;
import com.emc.documentum.xml.dds.persistence.internal.PersistenceUtil;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class FileSystemLocation
extends AbstractLocation {
    protected FileSystemLocation(Store store) {
        super(store);
    }

    protected FileSystemLocation(Store store, String path) {
        super(store, path);
    }

    protected FileSystemLocation(Store store, List<String> pathComponents) {
        super(store, pathComponents);
    }

    @Override
    public String getPath(boolean trailingSeparator) {
        if (this.getPathComponents().size() == 0) {
            return ((FileSystemStore)this.getStore()).getPrefix() + this.getStoreType().getSeparator();
        }
        StringBuilder path = new StringBuilder(((FileSystemStore)this.getStore()).getPrefix());
        for (String pathComponent : this.getPathComponents()) {
            path.append(this.getStoreType().getSeparator()).append(pathComponent);
        }
        if (trailingSeparator) {
            path.append(this.getStore().getSeparator());
        }
        return path.toString();
    }

    @Override
    public Location getParent() {
        if (this.getPathComponents().size() == 0) {
            return null;
        }
        return new FileSystemLocation(this.getStore(), this.getPathComponents().subList(0, this.getPathComponents().size() - 1));
    }

    @Override
    public Location getChildLocation(String childName) {
        List<String> childComponents = this.getPathComponents();
        childComponents.add(childName);
        return new FileSystemLocation(this.getStore(), childComponents);
    }

    @Override
    public Container getChildContainer(String childName) {
        return new FileSystemContainer(this, childName);
    }

    @Override
    public Location getDescendantLocation(List<String> relativePathComponents) {
        List<String> descendantComponents = this.getPathComponents();
        descendantComponents.addAll(relativePathComponents);
        return new FileSystemLocation(this.getStore(), descendantComponents);
    }

    @Override
    public XMLNode asXMLNode() {
        return new FileSystemXMLNode(this);
    }

    @Override
    public FileSystemLocation deepCopy() {
        return new FileSystemLocation(this.getStore(), this.getPathComponents());
    }

    @Override
    public boolean exists(Session session) throws StoreSpecificException, DeadlockException {
        if (this.isRoot()) {
            return true;
        }
        File directory = new File(this.getPath(true));
        return directory.exists() && directory.isDirectory();
    }

    @Override
    public Collection<StoreChild> listChildren(Session session, boolean includeLocations, boolean includeContainers, boolean recurse) throws LocationNotFoundException, StoreSpecificException, DeadlockException {
        File directory = new File(this.getPath(true));
        if (!directory.exists() || !directory.isDirectory()) {
            throw new LocationNotFoundException("List Children failed : Location does not exist : " + this.getPath());
        }
        ArrayList<StoreChild> result = new ArrayList<StoreChild>();
        if (!includeLocations && !includeContainers) {
            return result;
        }
        this.addChildren(result, directory, includeLocations, includeContainers, recurse);
        return result;
    }

    private void addChildren(List<StoreChild> result, File file, boolean includeLocations, boolean includeContainers, boolean recurse) throws DeadlockException, StoreSpecificException {
        for (File child : file.listFiles()) {
            if (child.isDirectory()) {
                if (includeLocations) {
                    result.add(this.getChildLocation(child.getName()));
                }
                if (!recurse) continue;
                this.addChildren(result, child, includeLocations, includeContainers, recurse);
                continue;
            }
            if (!includeContainers || !child.isFile()) continue;
            result.add(this.getChildContainer(child.getName()));
        }
    }

    @Override
    public void create(Session session, LocationOptions options, boolean createPath) throws LocationNotFoundException, LocationAlreadyExistsException, TypeConflictException, StoreSpecificException, DeadlockException {
        if (this.isRoot()) {
            throw new LocationAlreadyExistsException("Create failed : Root Location cannot be created.");
        }
        File file = new File(this.getPath());
        if (file.exists()) {
            if (file.isDirectory()) {
                throw new LocationAlreadyExistsException("Create failed : Location already exists : " + this.getPath());
            }
            throw new TypeConflictException("Create failed :Container already exists with same path : " + this.getPath());
        }
        FileSystemLocationUtil.createLocation(this, createPath);
    }

    @Override
    public void delete(Session session) throws LocationNotFoundException, StoreSpecificException, DeadlockException, IllegalActionException {
        if (this.isRoot()) {
            throw new IllegalActionException("Delete Location failed : The Root Directory cannot be deleted.");
        }
        File directory = new File(this.getPath(true));
        if (directory.isFile()) {
            throw new LocationNotFoundException("Delete Location failed : Location does not map to a Directory : " + this.getPath(false));
        }
        if (directory.isDirectory()) {
            FileSystemLocationUtil.deleteFully(directory);
            return;
        }
        throw new LocationNotFoundException("Delete Location failed : Directory does not exist : " + this.getPath(false));
    }

    @Override
    public LocationOptions getOptions(Session session) throws LocationNotFoundException, StoreSpecificException, DeadlockException {
        return null;
    }

    @Override
    public void move(Session session, Location target, boolean replace) throws LocationNotFoundException, LocationAlreadyExistsException, IllegalActionException, TypeConflictException, StoreSpecificException, DeadlockException {
        PersistenceUtil.move(session, this, session, target, replace);
    }

    @Override
    public void copy(Session session, Location target, boolean replace) throws LocationNotFoundException, LocationAlreadyExistsException, IllegalActionException, TypeConflictException, StoreSpecificException, DeadlockException {
        PersistenceUtil.copy(session, this, session, target, replace);
    }
}

