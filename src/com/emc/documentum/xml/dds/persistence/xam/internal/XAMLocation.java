/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.snia.xam.XAMException
 *  org.snia.xam.XSystem
 *  org.snia.xam.XUID
 *  org.snia.xam.toolkit.XAMXUID
 */
package com.emc.documentum.xml.dds.persistence.xam.internal;

import com.emc.documentum.xml.dds.logging.LogCenter;
import com.emc.documentum.xml.dds.persistence.Container;
import com.emc.documentum.xml.dds.persistence.Location;
import com.emc.documentum.xml.dds.persistence.LocationOptions;
import com.emc.documentum.xml.dds.persistence.Session;
import com.emc.documentum.xml.dds.persistence.Store;
import com.emc.documentum.xml.dds.persistence.StoreChild;
import com.emc.documentum.xml.dds.persistence.XMLNode;
import com.emc.documentum.xml.dds.persistence.exception.DeadlockException;
import com.emc.documentum.xml.dds.persistence.exception.IllegalActionException;
import com.emc.documentum.xml.dds.persistence.exception.LocationAlreadyExistsException;
import com.emc.documentum.xml.dds.persistence.exception.LocationNotFoundException;
import com.emc.documentum.xml.dds.persistence.exception.StoreSpecificException;
import com.emc.documentum.xml.dds.persistence.exception.TypeConflictException;
import com.emc.documentum.xml.dds.persistence.internal.AbstractLocation;
import com.emc.documentum.xml.dds.persistence.registry.vfs.xam.XAMContentRegistry;
import com.emc.documentum.xml.dds.persistence.xam.internal.XAMContainer;
import com.emc.documentum.xml.dds.persistence.xam.internal.XAMSession;
import com.emc.documentum.xml.dds.persistence.xam.internal.XAMStore;
import com.emc.documentum.xml.dds.persistence.xam.internal.XAMXMLNode;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.snia.xam.XAMException;
import org.snia.xam.XSystem;
import org.snia.xam.XUID;
import org.snia.xam.toolkit.XAMXUID;

public class XAMLocation
extends AbstractLocation {
    protected XAMLocation(Store store) {
        super(store);
    }

    protected XAMLocation(Store store, String path) {
        super(store, path);
    }

    protected XAMLocation(Store store, List<String> pathComponents) {
        super(store, pathComponents);
    }

    @Override
    public Location getParent() {
        if (this.getPathComponents().size() == 0) {
            return null;
        }
        return new XAMLocation(this.getStore(), this.getPathComponents().subList(0, this.getPathComponents().size() - 1));
    }

    @Override
    public Location getChildLocation(String childName) {
        List<String> childComponents = this.getPathComponents();
        childComponents.add(childName);
        return new XAMLocation(this.getStore(), childComponents);
    }

    @Override
    public Container getChildContainer(String childName) {
        return new XAMContainer(this, childName);
    }

    @Override
    public Location getDescendantLocation(List<String> relativePathComponents) {
        List<String> descendantComponents = this.getPathComponents();
        descendantComponents.addAll(relativePathComponents);
        return new XAMLocation(this.getStore(), descendantComponents);
    }

    @Override
    public XMLNode asXMLNode() {
        return new XAMXMLNode(this);
    }

    @Override
    public Location deepCopy() {
        return new XAMLocation(this.getStore(), this.getPathComponents());
    }

    @Override
    public boolean exists(Session session) throws StoreSpecificException, DeadlockException {
        XAMContentRegistry cr = ((XAMStore)this.getStore()).getContentRegistry();
        return cr.existsLocation(this);
    }

    @Override
    public Collection<StoreChild> listChildren(Session session, boolean includeLocations, boolean includeContainers, boolean recurse) throws LocationNotFoundException, StoreSpecificException, DeadlockException {
        XAMContentRegistry cr = ((XAMStore)this.getStore()).getContentRegistry();
        return cr.listChildren(this, includeLocations, includeContainers, recurse);
    }

    @Override
    public void create(Session session, LocationOptions options, boolean createPath) throws LocationNotFoundException, LocationAlreadyExistsException, TypeConflictException, StoreSpecificException, DeadlockException {
        XAMContentRegistry cr = ((XAMStore)this.getStore()).getContentRegistry();
        cr.createLocation(this, null, createPath);
    }

    @Override
    public void delete(Session session) throws LocationNotFoundException, StoreSpecificException, DeadlockException, IllegalActionException {
        XAMContentRegistry cr = ((XAMStore)this.getStore()).getContentRegistry();
        List<String> xuids = cr.deleteLocation(this, true);
        for (String xuid : xuids) {
            try {
                ((XAMSession)session).getXSystem().deleteXSet((XUID)new XAMXUID(xuid));
            }
            catch (XAMException xe) {
                LogCenter.exception(this, "XSet with XUID " + xuid + " was not deleted successfully.", (Throwable)xe);
            }
        }
    }

    @Override
    public LocationOptions getOptions(Session session) throws LocationNotFoundException, StoreSpecificException, DeadlockException {
        return null;
    }

    @Override
    public void move(Session session, Location target, boolean replace) throws LocationNotFoundException, LocationAlreadyExistsException, IllegalActionException, TypeConflictException, StoreSpecificException, DeadlockException {
        XAMContentRegistry cr = ((XAMStore)this.getStore()).getContentRegistry();
        List<String> xuids = cr.moveLocation(this, target, replace);
        for (String xuid : xuids) {
            try {
                ((XAMSession)session).getXSystem().deleteXSet((XUID)new XAMXUID(xuid));
            }
            catch (XAMException xe) {
                LogCenter.exception(this, "XSet with XUID " + xuid + " was not deleted successfully.", (Throwable)xe);
            }
        }
    }

    @Override
    public void copy(Session session, Location target, boolean replace) throws LocationNotFoundException, LocationAlreadyExistsException, TypeConflictException, IllegalActionException, StoreSpecificException, DeadlockException {
        XAMContentRegistry cr = ((XAMStore)this.getStore()).getContentRegistry();
        List<String> xuids = cr.copyLocation(this, target, replace);
        for (String xuid : xuids) {
            try {
                ((XAMSession)session).getXSystem().deleteXSet((XUID)new XAMXUID(xuid));
            }
            catch (XAMException xe) {
                LogCenter.exception(this, "XSet with XUID " + xuid + " was not deleted successfully.", (Throwable)xe);
            }
        }
    }
}

