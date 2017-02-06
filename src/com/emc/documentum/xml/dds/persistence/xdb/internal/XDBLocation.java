/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  com.xhive.core.interfaces.XhiveDatabaseIf
 *  com.xhive.core.interfaces.XhiveSessionIf
 *  com.xhive.dom.interfaces.XhiveBlobNodeIf
 *  com.xhive.dom.interfaces.XhiveDocumentIf
 *  com.xhive.dom.interfaces.XhiveLibraryChildIf
 *  com.xhive.dom.interfaces.XhiveLibraryIf
 *  com.xhive.dom.interfaces.XhiveNodeIf
 *  com.xhive.error.XhiveDeadlockException
 *  com.xhive.error.XhiveException
 *  com.xhive.util.interfaces.IterableIterator
 */
package com.emc.documentum.xml.dds.persistence.xdb.internal;

import com.emc.documentum.xml.dds.persistence.Container;
import com.emc.documentum.xml.dds.persistence.Location;
import com.emc.documentum.xml.dds.persistence.LocationOptions;
import com.emc.documentum.xml.dds.persistence.Session;
import com.emc.documentum.xml.dds.persistence.Store;
import com.emc.documentum.xml.dds.persistence.StoreChild;
import com.emc.documentum.xml.dds.persistence.XMLNode;
import com.emc.documentum.xml.dds.persistence.exception.AmbiguousXPointerException;
import com.emc.documentum.xml.dds.persistence.exception.DeadlockException;
import com.emc.documentum.xml.dds.persistence.exception.IllegalActionException;
import com.emc.documentum.xml.dds.persistence.exception.LocationAlreadyExistsException;
import com.emc.documentum.xml.dds.persistence.exception.LocationNotFoundException;
import com.emc.documentum.xml.dds.persistence.exception.NodeNotFoundException;
import com.emc.documentum.xml.dds.persistence.exception.StoreSpecificException;
import com.emc.documentum.xml.dds.persistence.exception.TypeConflictException;
import com.emc.documentum.xml.dds.persistence.internal.AbstractLocation;
import com.emc.documentum.xml.dds.persistence.xdb.XDBLibraryOptions;
import com.emc.documentum.xml.dds.persistence.xdb.internal.XDBContainer;
import com.emc.documentum.xml.dds.persistence.xdb.internal.XDBLocationUtil;
import com.emc.documentum.xml.dds.persistence.xdb.internal.XDBXMLNode;
import com.emc.documentum.xml.dds.persistence.xdb.internal.XDBXMLUtil;
import com.xhive.core.interfaces.XhiveDatabaseIf;
import com.xhive.core.interfaces.XhiveSessionIf;
import com.xhive.dom.interfaces.XhiveBlobNodeIf;
import com.xhive.dom.interfaces.XhiveDocumentIf;
import com.xhive.dom.interfaces.XhiveLibraryChildIf;
import com.xhive.dom.interfaces.XhiveLibraryIf;
import com.xhive.dom.interfaces.XhiveNodeIf;
import com.xhive.error.XhiveDeadlockException;
import com.xhive.error.XhiveException;
import com.xhive.util.interfaces.IterableIterator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.w3c.dom.Node;

public class XDBLocation
extends AbstractLocation {
    protected XDBLocation(Store store) {
        super(store);
    }

    protected XDBLocation(Store store, String path) {
        super(store, path);
    }

    protected XDBLocation(Store store, List<String> pathComponents) {
        super(store, pathComponents);
    }

    @Override
    public Location getParent() {
        if (this.getPathComponents().size() == 0) {
            return null;
        }
        ArrayList<String> parentPathComponents = new ArrayList<String>();
        for (int counter = 0; counter < this.getPathComponents().size() - 1; ++counter) {
            parentPathComponents.add(this.getPathComponents().get(counter));
        }
        return new XDBLocation(this.getStore(), parentPathComponents);
    }

    @Override
    public Location getChildLocation(String childName) {
        List<String> childComponents = this.getPathComponents();
        childComponents.add(childName);
        return new XDBLocation(this.getStore(), childComponents);
    }

    @Override
    public Container getChildContainer(String childName) {
        return new XDBContainer(this, childName);
    }

    @Override
    public Location getDescendantLocation(List<String> relativePathComponents) {
        List<String> descendantComponents = this.getPathComponents();
        descendantComponents.addAll(relativePathComponents);
        return new XDBLocation(this.getStore(), descendantComponents);
    }

    @Override
    public XDBLocation deepCopy() {
        return new XDBLocation(this.getStore(), this.getPathComponents());
    }

    @Override
    public XMLNode asXMLNode() {
        return new XDBXMLNode(this);
    }

    @Override
    public boolean exists(Session session) throws StoreSpecificException, DeadlockException {
        if (this.isRoot()) {
            return true;
        }
        try {
            XhiveLibraryChildIf libraryChild = ((XhiveSessionIf)session.getSession()).getDatabase().getRoot().getByPath(this.getPath(false));
            return libraryChild != null && libraryChild instanceof XhiveLibraryIf;
        }
        catch (XhiveDeadlockException xde) {
            throw new DeadlockException((Throwable)xde);
        }
        catch (XhiveException xe) {
            throw new StoreSpecificException((Throwable)xe);
        }
    }

    @Override
    public Collection<StoreChild> listChildren(Session session, boolean includeLocations, boolean includeContainers, boolean recurse) throws LocationNotFoundException, StoreSpecificException, DeadlockException {
        XhiveLibraryIf library;
        XhiveLibraryIf root = ((XhiveSessionIf)session.getSession()).getDatabase().getRoot();
        if (this.isRoot()) {
            library = root;
        } else {
            XhiveLibraryChildIf libraryChild = root.getByPath(this.getPath(false));
            if (libraryChild == null || !(libraryChild instanceof XhiveLibraryIf)) {
                throw new LocationNotFoundException("List Children failed : Location does not exist : " + this.getPath());
            }
            library = (XhiveLibraryIf)libraryChild;
        }
        ArrayList<StoreChild> result = new ArrayList<StoreChild>();
        if (!includeLocations && !includeContainers) {
            return result;
        }
        this.addChildren(result, library, includeLocations, includeContainers, recurse);
        return result;
    }

    private void addChildren(List<StoreChild> result, XhiveLibraryIf library, boolean includeLocations, boolean includeContainers, boolean recurse) throws DeadlockException, StoreSpecificException {
        try {
            IterableIterator iterator = library.getChildren();
            while (iterator.hasNext()) {
                XhiveLibraryChildIf child = (XhiveLibraryChildIf)iterator.next();
                if (child instanceof XhiveLibraryIf) {
                    if (includeLocations) {
                        result.add(this.getChildLocation(child.getName()));
                    }
                    if (!recurse) continue;
                    this.addChildren(result, (XhiveLibraryIf)child, includeLocations, includeContainers, recurse);
                    continue;
                }
                if (!includeContainers || !(child instanceof XhiveDocumentIf) && !(child instanceof XhiveBlobNodeIf)) continue;
                result.add(this.getChildContainer(child.getName()));
            }
        }
        catch (XhiveDeadlockException xde) {
            throw new DeadlockException((Throwable)xde);
        }
        catch (XhiveException xe) {
            throw new StoreSpecificException((Throwable)xe);
        }
    }

    @Override
    public void create(Session session, LocationOptions options, boolean createPath) throws LocationNotFoundException, LocationAlreadyExistsException, TypeConflictException, StoreSpecificException, DeadlockException {
        try {
            if (this.isRoot()) {
                throw new LocationAlreadyExistsException("Create failed : Root Location cannot be created.");
            }
            switch (XDBLocationUtil.checkPath((XhiveSessionIf)session.getSession(), this.getPath())) {
                case LOCATION: {
                    throw new LocationAlreadyExistsException("Create failed : Location already exists : " + this.getPath());
                }
                case CONTAINER: {
                    throw new TypeConflictException("Create failed : Container already exists with same path : " + this.getPath());
                }
            }
            XDBLocationUtil.createLocation((XhiveSessionIf)session.getSession(), this, (XDBLibraryOptions)options, createPath);
        }
        catch (XhiveDeadlockException xde) {
            throw new DeadlockException((Throwable)xde);
        }
        catch (XhiveException xe) {
            throw new StoreSpecificException((Throwable)xe);
        }
    }

    @Override
    public void delete(Session session) throws LocationNotFoundException, StoreSpecificException, DeadlockException, IllegalActionException {
        try {
            if (this.isRoot()) {
                throw new IllegalActionException("Delete Location failed : The Root Library cannot be deleted.");
            }
            XhiveLibraryIf root = ((XhiveSessionIf)session.getSession()).getDatabase().getRoot();
            XhiveLibraryChildIf libraryChild = root.getByPath(this.getPath(false));
            if (libraryChild == null) {
                throw new LocationNotFoundException("Delete Location failed : Library does not exist : " + this.getPath(false));
            }
            if (!(libraryChild instanceof XhiveLibraryIf)) {
                throw new LocationNotFoundException("Delete Location failed : Location does not map to a Library : " + this.getPath(false));
            }
            XhiveLibraryIf parent = libraryChild.getOwnerLibrary();
            parent.removeChild((Node)libraryChild);
        }
        catch (XhiveDeadlockException xde) {
            throw new DeadlockException((Throwable)xde);
        }
        catch (XhiveException xe) {
            throw new StoreSpecificException((Throwable)xe);
        }
    }

    @Override
    public LocationOptions getOptions(Session session) throws LocationNotFoundException, StoreSpecificException, DeadlockException {
        return new XDBLibraryOptions(XDBLocationUtil.getLibraryOptions((XhiveSessionIf)session.getSession(), this));
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    public void move(Session session, Location target, boolean replace) throws LocationNotFoundException, LocationAlreadyExistsException, IllegalActionException, TypeConflictException, StoreSpecificException, DeadlockException {
        XhiveLibraryIf parentNode;
        XhiveLibraryChildIf sourceNode;
        Location targetLocation = target;
        Location parentLocation = targetLocation.getParent();
        if (target.exists(session)) {
            if (!replace) throw new LocationAlreadyExistsException("Target Location for move already exists.");
            target.delete(session);
        } else if (!parentLocation.exists(session)) {
            throw new LocationNotFoundException("Parent Location does not exist for Location move.");
        }
        try {
            parentNode = (XhiveLibraryIf)XDBXMLUtil.retrieveNode((XhiveSessionIf)session.getSession(), new XDBXMLNode(parentLocation));
            sourceNode = (XhiveLibraryChildIf)XDBXMLUtil.retrieveNode((XhiveSessionIf)session.getSession(), new XDBXMLNode(this));
        }
        catch (NodeNotFoundException nnfe) {
            if (!this.getParent().getChildContainer(this.getName()).exists(session)) throw new LocationNotFoundException("Move failed, a Location could not be found : " + nnfe);
            throw new TypeConflictException("Move failed : source maps to a Container.");
        }
        catch (AmbiguousXPointerException axpe) {
            throw new StoreSpecificException("Internal error.", axpe);
        }
        try {
            XhiveLibraryChildIf newNode = parentNode.adoptNode((Node)sourceNode);
            newNode.setName(target.getName());
            parentNode.appendChild((Node)newNode);
            return;
        }
        catch (XhiveException xhe) {
            if (xhe.getErrorCode() != 106) throw new StoreSpecificException((Throwable)xhe);
            throw new TypeConflictException((Throwable)xhe);
        }
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    public void copy(Session session, Location target, boolean replace) throws LocationNotFoundException, LocationAlreadyExistsException, IllegalActionException, TypeConflictException, StoreSpecificException, DeadlockException {
        XhiveLibraryIf parentNode;
        XhiveLibraryChildIf sourceNode;
        Location targetLocation = target;
        Location parentLocation = targetLocation.getParent();
        if (target.exists(session)) {
            if (!replace) throw new LocationAlreadyExistsException("Target Location for copy already exists.");
            target.delete(session);
        } else if (!parentLocation.exists(session)) {
            throw new LocationNotFoundException("Parent Location does not exist for Location copy.");
        }
        try {
            parentNode = (XhiveLibraryIf)XDBXMLUtil.retrieveNode((XhiveSessionIf)session.getSession(), new XDBXMLNode(parentLocation));
            sourceNode = (XhiveLibraryChildIf)XDBXMLUtil.retrieveNode((XhiveSessionIf)session.getSession(), new XDBXMLNode(this));
        }
        catch (NodeNotFoundException nnfe) {
            if (!this.getParent().getChildContainer(this.getName()).exists(session)) throw new LocationNotFoundException("Move failed, a Location could not be found : " + nnfe);
            throw new TypeConflictException("Move failed : source maps to a Container.");
        }
        catch (AmbiguousXPointerException axpe) {
            throw new StoreSpecificException("Internal error.", axpe);
        }
        try {
            XhiveLibraryChildIf newNode = (XhiveLibraryChildIf)parentNode.importNode((Node)sourceNode, true);
            newNode.setName(target.getName());
            parentNode.appendChild((Node)newNode);
            return;
        }
        catch (XhiveException xhe) {
            if (xhe.getErrorCode() != 106) throw new StoreSpecificException((Throwable)xhe);
            throw new TypeConflictException((Throwable)xhe);
        }
    }

}

