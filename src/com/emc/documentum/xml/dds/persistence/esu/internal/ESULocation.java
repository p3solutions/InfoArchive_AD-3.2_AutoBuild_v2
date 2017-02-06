/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.persistence.esu.internal;

import com.emc.documentum.xml.dds.operation.exception.OperationException;
import com.emc.documentum.xml.dds.persistence.Container;
import com.emc.documentum.xml.dds.persistence.Location;
import com.emc.documentum.xml.dds.persistence.LocationOptions;
import com.emc.documentum.xml.dds.persistence.Session;
import com.emc.documentum.xml.dds.persistence.Store;
import com.emc.documentum.xml.dds.persistence.StoreChild;
import com.emc.documentum.xml.dds.persistence.XMLNode;
import com.emc.documentum.xml.dds.persistence.esu.internal.ESUContainer;
import com.emc.documentum.xml.dds.persistence.esu.internal.ESUStore;
import com.emc.documentum.xml.dds.persistence.esu.internal.ESUXMLNode;
import com.emc.documentum.xml.dds.persistence.exception.DeadlockException;
import com.emc.documentum.xml.dds.persistence.exception.IllegalActionException;
import com.emc.documentum.xml.dds.persistence.exception.LocationAlreadyExistsException;
import com.emc.documentum.xml.dds.persistence.exception.LocationNotFoundException;
import com.emc.documentum.xml.dds.persistence.exception.StoreSpecificException;
import com.emc.documentum.xml.dds.persistence.exception.TypeConflictException;
import com.emc.documentum.xml.dds.persistence.internal.AbstractLocation;
import com.emc.documentum.xml.dds.persistence.internal.PersistenceUtil;
import com.emc.documentum.xml.dds.persistence.registry.vfs.esu.ESUContentRegistry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class ESULocation
extends AbstractLocation {
    protected ESULocation(Store store) {
        super(store);
    }

    protected ESULocation(Store store, String path) {
        super(store, path);
    }

    protected ESULocation(Store store, List<String> pathComponents) {
        super(store, pathComponents);
    }

    @Override
    public Location getParent() {
        if (this.getPathComponents().size() == 0) {
            return null;
        }
        return new ESULocation(this.getStore(), this.getPathComponents().subList(0, this.getPathComponents().size() - 1));
    }

    @Override
    public Location getChildLocation(String childName) {
        List<String> childComponents = this.getPathComponents();
        childComponents.add(childName);
        return new ESULocation(this.getStore(), childComponents);
    }

    @Override
    public Container getChildContainer(String childName) {
        return new ESUContainer(this, childName);
    }

    @Override
    public Location getDescendantLocation(List<String> relativePathComponents) {
        List<String> descendantComponents = this.getPathComponents();
        descendantComponents.addAll(relativePathComponents);
        return new ESULocation(this.getStore(), descendantComponents);
    }

    @Override
    public XMLNode asXMLNode() {
        return new ESUXMLNode(this);
    }

    @Override
    public Location deepCopy() {
        return new ESULocation(this.getStore(), this.getPathComponents());
    }

    @Override
    public boolean exists(Session session) throws StoreSpecificException, DeadlockException {
        try {
            return ((ESUStore)this.getStore()).getContentRegistry().existsLocation(this);
        }
        catch (OperationException oe) {
            throw new StoreSpecificException(oe);
        }
    }

    @Override
    public Collection<StoreChild> listChildren(Session session, boolean includeLocations, boolean includeContainers, boolean recurse) throws LocationNotFoundException, StoreSpecificException, DeadlockException {
        ESUContentRegistry cr = ((ESUStore)this.getStore()).getContentRegistry();
        ArrayList<StoreChild> result = new ArrayList<StoreChild>();
        for (StoreChild child : cr.listChildren(this, includeLocations || recurse, includeContainers)) {
            if (includeContainers && child.isContainer() || includeLocations && child.isLocation()) {
                result.add(child);
            }
            if (!recurse || !child.isLocation()) continue;
            result.addAll(((Location)child).listChildren(session, includeLocations, includeContainers, recurse));
        }
        return result;
    }

    @Override
    public void create(Session session, LocationOptions options, boolean createPath) throws LocationNotFoundException, LocationAlreadyExistsException, TypeConflictException, StoreSpecificException, DeadlockException {
        ((ESUStore)this.getStore()).getContentRegistry().createLocation(this, null, createPath);
    }

    @Override
    public void delete(Session session) throws LocationNotFoundException, StoreSpecificException, DeadlockException, IllegalActionException {
        ESUContentRegistry cr = ((ESUStore)this.getStore()).getContentRegistry();
        cr.deleteLocation(this);
    }

    @Override
    public LocationOptions getOptions(Session session) throws LocationNotFoundException, StoreSpecificException, DeadlockException {
        return null;
    }

    @Override
    public void copy(Session session, Location target, boolean replace) throws LocationNotFoundException, LocationAlreadyExistsException, IllegalActionException, TypeConflictException, StoreSpecificException, DeadlockException {
        PersistenceUtil.copy(session, this, session, target, replace);
    }

    @Override
    public void move(Session session, Location target, boolean replace) throws LocationNotFoundException, LocationAlreadyExistsException, IllegalActionException, TypeConflictException, StoreSpecificException, DeadlockException {
        PersistenceUtil.move(session, this, session, target, replace);
    }
}

