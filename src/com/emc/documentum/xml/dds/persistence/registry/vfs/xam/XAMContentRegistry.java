/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  com.xhive.core.interfaces.XhiveDatabaseIf
 *  com.xhive.core.interfaces.XhiveNodeIteratorIf
 *  com.xhive.core.interfaces.XhiveSessionIf
 *  com.xhive.dom.interfaces.XhiveDocumentIf
 *  com.xhive.dom.interfaces.XhiveElementIf
 *  com.xhive.dom.interfaces.XhiveLibraryChildIf
 *  com.xhive.dom.interfaces.XhiveLibraryIf
 *  com.xhive.dom.interfaces.XhiveNodeIf
 *  com.xhive.error.XhiveDeadlockException
 *  com.xhive.error.XhiveException
 *  com.xhive.index.interfaces.XhiveIndexIf
 *  com.xhive.index.interfaces.XhiveIndexListIf
 */
package com.emc.documentum.xml.dds.persistence.registry.vfs.xam;

import com.emc.documentum.xml.dds.application.Application;
import com.emc.documentum.xml.dds.exception.DDSException;
import com.emc.documentum.xml.dds.logging.LogCenter;
import com.emc.documentum.xml.dds.operation.exception.BeginFailedException;
import com.emc.documentum.xml.dds.operation.exception.CommitFailedException;
import com.emc.documentum.xml.dds.operation.exception.RollbackFailedException;
import com.emc.documentum.xml.dds.operation.framework.Operation;
import com.emc.documentum.xml.dds.operation.library.persistence.CreateLocationOperation;
import com.emc.documentum.xml.dds.operation.library.persistence.ExistsStoreChildOperation;
import com.emc.documentum.xml.dds.operation.library.persistence.PersistOperation;
import com.emc.documentum.xml.dds.persistence.Container;
import com.emc.documentum.xml.dds.persistence.ContentDescriptor;
import com.emc.documentum.xml.dds.persistence.Data;
import com.emc.documentum.xml.dds.persistence.Location;
import com.emc.documentum.xml.dds.persistence.LocationOptions;
import com.emc.documentum.xml.dds.persistence.Session;
import com.emc.documentum.xml.dds.persistence.Store;
import com.emc.documentum.xml.dds.persistence.StoreChild;
import com.emc.documentum.xml.dds.persistence.StoreUser;
import com.emc.documentum.xml.dds.persistence.content.XMLContentDescriptor;
import com.emc.documentum.xml.dds.persistence.data.StringData;
import com.emc.documentum.xml.dds.persistence.exception.ContainerAlreadyExistsException;
import com.emc.documentum.xml.dds.persistence.exception.ContainerNotFoundException;
import com.emc.documentum.xml.dds.persistence.exception.DeadlockException;
import com.emc.documentum.xml.dds.persistence.exception.IllegalActionException;
import com.emc.documentum.xml.dds.persistence.exception.LocationAlreadyExistsException;
import com.emc.documentum.xml.dds.persistence.exception.LocationNotFoundException;
import com.emc.documentum.xml.dds.persistence.exception.StoreSpecificException;
import com.emc.documentum.xml.dds.persistence.exception.TypeConflictException;
import com.emc.documentum.xml.dds.persistence.registry.ContentRegistry;
import com.emc.documentum.xml.dds.persistence.xam.internal.XAMContainer;
import com.emc.documentum.xml.dds.user.User;
import com.xhive.core.interfaces.XhiveDatabaseIf;
import com.xhive.core.interfaces.XhiveNodeIteratorIf;
import com.xhive.core.interfaces.XhiveSessionIf;
import com.xhive.dom.interfaces.XhiveDocumentIf;
import com.xhive.dom.interfaces.XhiveElementIf;
import com.xhive.dom.interfaces.XhiveLibraryChildIf;
import com.xhive.dom.interfaces.XhiveLibraryIf;
import com.xhive.dom.interfaces.XhiveNodeIf;
import com.xhive.error.XhiveDeadlockException;
import com.xhive.error.XhiveException;
import com.xhive.index.interfaces.XhiveIndexIf;
import com.xhive.index.interfaces.XhiveIndexListIf;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.w3c.dom.Attr;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XAMContentRegistry
implements ContentRegistry {
    private static final String CTR_DOC_NM = "Containers.xml";
    private static final String CTR_ROOT_TAG = "containers";
    private static final String CTR_TAG = "con";
    private static final String NAME_ATT = "name";
    private static final String PATH_ATT = "path";
    private static final String XUID_ATT = "xuid";
    private static final String NOXUID = "NOXUIDASSIGNED";
    private static final StringData CTR_DOC_EMPTY_CONTENT = new StringData("<containers/>");
    private static final int MAX_RETRY = 200;
    private final Application app;
    private final User appUsr;
    private final Location crLocation;

    public XAMContentRegistry(Application app, Location crLocation) {
        this.app = app;
        this.appUsr = app.getApplicationUser();
        this.crLocation = crLocation;
    }

    public Location getContentRegistryLocation() {
        return this.crLocation;
    }

    public void initialize() throws DDSException {
        Container ctrDoc;
        if (!((Boolean)this.app.execute(this.appUsr, new ExistsStoreChildOperation(this.crLocation))).booleanValue()) {
            this.app.execute(this.appUsr, new CreateLocationOperation(this.crLocation, null, true));
            this.createIndexes(this.crLocation);
        }
        if (!((Boolean)this.app.execute(this.appUsr, new ExistsStoreChildOperation(ctrDoc = this.crLocation.getChildContainer("Containers.xml")))).booleanValue()) {
            this.app.execute(this.appUsr, new PersistOperation(ctrDoc, new XMLContentDescriptor(), CTR_DOC_EMPTY_CONTENT, false));
        }
    }

    @Override
    public boolean existsLocation(Location location) throws StoreSpecificException {
        if (location.isRoot()) {
            return true;
        }
        try {
            return (Boolean)new ExistsLocationTransaction(location).execute();
        }
        catch (Exception e) {
            if (e instanceof StoreSpecificException) {
                throw (StoreSpecificException)e;
            }
            throw new StoreSpecificException(e);
        }
    }

    @Override
    public void createLocation(Location location, Map<String, String> attributes, boolean createPath) throws LocationAlreadyExistsException, LocationNotFoundException, TypeConflictException, StoreSpecificException {
        if (location.isRoot()) {
            throw new LocationAlreadyExistsException("Create failed : Root Location cannot be created.");
        }
        try {
            new CreateLocationTransaction(location, createPath).execute();
        }
        catch (Exception e) {
            if (e instanceof StoreSpecificException) {
                throw (StoreSpecificException)e;
            }
            if (e instanceof LocationAlreadyExistsException) {
                throw (LocationAlreadyExistsException)e;
            }
            if (e instanceof LocationNotFoundException) {
                throw (LocationNotFoundException)e;
            }
            if (e instanceof TypeConflictException) {
                throw (TypeConflictException)e;
            }
            throw new StoreSpecificException(e);
        }
    }

    @Override
    public void moveLocation(Location source, Location target) throws LocationNotFoundException, LocationAlreadyExistsException, IllegalActionException, TypeConflictException, StoreSpecificException {
        this.moveLocation(source, target, true);
    }

    public List<String> moveLocation(Location source, Location target, boolean replace) throws LocationNotFoundException, LocationAlreadyExistsException, IllegalActionException, TypeConflictException, StoreSpecificException {
        if (source.isRoot()) {
            throw new IllegalActionException("Tried to move root Location.");
        }
        try {
            return (List)new MoveOrCopyLocationTransaction(source, target, replace, true).execute();
        }
        catch (Exception e) {
            if (e instanceof StoreSpecificException) {
                throw (StoreSpecificException)e;
            }
            if (e instanceof LocationNotFoundException) {
                throw (LocationNotFoundException)e;
            }
            if (e instanceof LocationAlreadyExistsException) {
                throw (LocationAlreadyExistsException)e;
            }
            if (e instanceof IllegalActionException) {
                throw (IllegalActionException)e;
            }
            if (e instanceof TypeConflictException) {
                throw (TypeConflictException)e;
            }
            throw new StoreSpecificException(e);
        }
    }

    public List<String> copyLocation(Location source, Location target, boolean replace) throws LocationNotFoundException, LocationAlreadyExistsException, IllegalActionException, TypeConflictException, StoreSpecificException {
        try {
            return (List)new MoveOrCopyLocationTransaction(source, target, replace, false).execute();
        }
        catch (Exception e) {
            if (e instanceof StoreSpecificException) {
                throw (StoreSpecificException)e;
            }
            if (e instanceof LocationNotFoundException) {
                throw (LocationNotFoundException)e;
            }
            if (e instanceof LocationAlreadyExistsException) {
                throw (LocationAlreadyExistsException)e;
            }
            if (e instanceof IllegalActionException) {
                throw (IllegalActionException)e;
            }
            if (e instanceof TypeConflictException) {
                throw (TypeConflictException)e;
            }
            throw new StoreSpecificException(e);
        }
    }

    @Override
    public void deleteLocation(Location location) throws LocationNotFoundException, IllegalActionException, StoreSpecificException {
        this.deleteLocation(location, true);
    }

    public List<String> deleteLocation(Location location, boolean dummy) throws LocationNotFoundException, IllegalActionException, StoreSpecificException {
        if (location.isRoot()) {
            throw new IllegalActionException("Delete Failed : Tried to delete root Location.");
        }
        try {
            return (List)new DeleteLocationTransaction(location).execute();
        }
        catch (Exception e) {
            if (e instanceof StoreSpecificException) {
                throw (StoreSpecificException)e;
            }
            if (e instanceof LocationNotFoundException) {
                throw (LocationNotFoundException)e;
            }
            if (e instanceof IllegalActionException) {
                throw (IllegalActionException)e;
            }
            throw new StoreSpecificException(e);
        }
    }

    @Override
    public List<StoreChild> listChildren(Location location, boolean includeLocations, boolean includeContainers) throws LocationNotFoundException, StoreSpecificException {
        try {
            return (List)new ListChildrenTransaction(location, includeLocations, includeContainers, false).execute();
        }
        catch (Exception e) {
            if (e instanceof StoreSpecificException) {
                throw (StoreSpecificException)e;
            }
            if (e instanceof LocationNotFoundException) {
                throw (LocationNotFoundException)e;
            }
            throw new StoreSpecificException(e);
        }
    }

    public List<StoreChild> listChildren(Location location, boolean includeLocations, boolean includeContainers, boolean recurse) throws LocationNotFoundException, StoreSpecificException {
        try {
            return (List)new ListChildrenTransaction(location, includeLocations, includeContainers, recurse).execute();
        }
        catch (Exception e) {
            if (e instanceof StoreSpecificException) {
                throw (StoreSpecificException)e;
            }
            if (e instanceof LocationNotFoundException) {
                throw (LocationNotFoundException)e;
            }
            throw new StoreSpecificException(e);
        }
    }

    @Override
    public String getAttribute(Location location, String name) throws DDSException {
        return null;
    }

    @Override
    public Map<String, String> getAttributes(Location location, List<String> names) throws DDSException {
        return null;
    }

    public String getXUID(Container container) throws StoreSpecificException {
        try {
            return (String)new GetXUIDTransaction(container).execute();
        }
        catch (Exception e) {
            if (e instanceof StoreSpecificException) {
                throw (StoreSpecificException)e;
            }
            throw new StoreSpecificException(e);
        }
    }

    public int getXUIDCount(String xuid) throws StoreSpecificException {
        try {
            return (Integer)new GetXUIDCountTransaction(xuid).execute();
        }
        catch (Exception e) {
            if (e instanceof StoreSpecificException) {
                throw (StoreSpecificException)e;
            }
            throw new StoreSpecificException(e);
        }
    }

    @Override
    public boolean existsContainer(Container container) throws StoreSpecificException {
        try {
            return (Boolean)new ExistsContainerTransaction(container).execute();
        }
        catch (Exception e) {
            if (e instanceof StoreSpecificException) {
                throw (StoreSpecificException)e;
            }
            throw new StoreSpecificException(e);
        }
    }

    public String createTempContainer(Container container, Map<String, String> attributes, boolean replace) throws LocationNotFoundException, ContainerAlreadyExistsException, TypeConflictException, StoreSpecificException {
        try {
            return (String)new CreateTempContainerTransaction(container, attributes, replace).execute();
        }
        catch (Exception e) {
            if (e instanceof StoreSpecificException) {
                throw (StoreSpecificException)e;
            }
            if (e instanceof LocationNotFoundException) {
                throw (LocationNotFoundException)e;
            }
            if (e instanceof ContainerAlreadyExistsException) {
                throw (ContainerAlreadyExistsException)e;
            }
            if (e instanceof TypeConflictException) {
                throw (TypeConflictException)e;
            }
            throw new StoreSpecificException(e);
        }
    }

    @Override
    public void createContainer(Container container, Map<String, String> attributes, boolean replace) throws StoreSpecificException {
        try {
            new CreateTempContainerTransaction(container, attributes, replace).execute();
            new UpdateContainerTransaction(container, attributes, replace).execute();
        }
        catch (Exception e) {
            if (e instanceof StoreSpecificException) {
                throw (StoreSpecificException)e;
            }
            throw new StoreSpecificException(e);
        }
    }

    public void updateContainer(Container container, Map<String, String> attributes, boolean replace) throws StoreSpecificException {
        try {
            new UpdateContainerTransaction(container, attributes, replace).execute();
        }
        catch (Exception e) {
            if (e instanceof StoreSpecificException) {
                throw (StoreSpecificException)e;
            }
            throw new StoreSpecificException(e);
        }
    }

    @Override
    public void moveContainer(Container source, Container target) throws LocationNotFoundException, ContainerNotFoundException, ContainerAlreadyExistsException, TypeConflictException, IllegalActionException, StoreSpecificException, DeadlockException {
        this.moveContainer(source, target, true);
    }

    public String moveContainer(Container source, Container target, boolean replace) throws LocationNotFoundException, ContainerNotFoundException, ContainerAlreadyExistsException, TypeConflictException, IllegalActionException, StoreSpecificException, DeadlockException {
        try {
            return (String)new MoveOrCopyContainerTransaction(source, target, replace, true).execute();
        }
        catch (Exception e) {
            if (e instanceof StoreSpecificException) {
                throw (StoreSpecificException)e;
            }
            if (e instanceof DeadlockException) {
                throw (DeadlockException)e;
            }
            if (e instanceof LocationNotFoundException) {
                throw (LocationNotFoundException)e;
            }
            if (e instanceof ContainerNotFoundException) {
                throw (ContainerNotFoundException)e;
            }
            if (e instanceof ContainerAlreadyExistsException) {
                throw (ContainerAlreadyExistsException)e;
            }
            if (e instanceof TypeConflictException) {
                throw (TypeConflictException)e;
            }
            if (e instanceof IllegalActionException) {
                throw (IllegalActionException)e;
            }
            throw new StoreSpecificException(e);
        }
    }

    public String copyContainer(Container source, Container target, boolean replace) throws LocationNotFoundException, ContainerNotFoundException, ContainerAlreadyExistsException, TypeConflictException, IllegalActionException, StoreSpecificException, DeadlockException {
        try {
            return (String)new MoveOrCopyContainerTransaction(source, target, replace, false).execute();
        }
        catch (Exception e) {
            if (e instanceof StoreSpecificException) {
                throw (StoreSpecificException)e;
            }
            if (e instanceof DeadlockException) {
                throw (DeadlockException)e;
            }
            if (e instanceof LocationNotFoundException) {
                throw (LocationNotFoundException)e;
            }
            if (e instanceof ContainerNotFoundException) {
                throw (ContainerNotFoundException)e;
            }
            if (e instanceof ContainerAlreadyExistsException) {
                throw (ContainerAlreadyExistsException)e;
            }
            if (e instanceof TypeConflictException) {
                throw (TypeConflictException)e;
            }
            if (e instanceof IllegalActionException) {
                throw (IllegalActionException)e;
            }
            throw new StoreSpecificException(e);
        }
    }

    @Override
    public void deleteContainer(Container container) throws ContainerNotFoundException, StoreSpecificException, DeadlockException {
        this.deleteContainer(container, true);
    }

    public String deleteContainer(Container container, boolean dummy) throws ContainerNotFoundException, StoreSpecificException, DeadlockException {
        try {
            return (String)new DeleteContainerTransaction(container).execute();
        }
        catch (Exception e) {
            if (e instanceof ContainerNotFoundException) {
                throw (ContainerNotFoundException)e;
            }
            if (e instanceof StoreSpecificException) {
                throw (StoreSpecificException)e;
            }
            if (e instanceof DeadlockException) {
                throw (DeadlockException)e;
            }
            if (e instanceof ContainerNotFoundException) {
                throw (ContainerNotFoundException)e;
            }
            throw new StoreSpecificException(e);
        }
    }

    @Override
    public String getAttribute(Container container, String name) throws StoreSpecificException {
        try {
            return (String)new GetAttributeTransaction(container, name).execute();
        }
        catch (Exception e) {
            if (e instanceof StoreSpecificException) {
                throw (StoreSpecificException)e;
            }
            throw new StoreSpecificException(e);
        }
    }

    @Override
    public Map<String, String> getAttributes(Container container, List<String> names) throws StoreSpecificException {
        try {
            return (Map)new GetAttributesTransaction(container, names).execute();
        }
        catch (Exception e) {
            if (e instanceof StoreSpecificException) {
                throw (StoreSpecificException)e;
            }
            throw new StoreSpecificException(e);
        }
    }

    private Location getVFSLocation(Location location) {
        return this.crLocation.getDescendantLocation(location.getPathComponents());
    }

    private void createIndexes(Location location) {
        Session session = null;
        try {
            Store store = location.getStore();
            session = store.getSession(this.appUsr.getStoreUser(store.getAlias()), false);
            session.begin();
            XhiveSessionIf xSession = (XhiveSessionIf)session.getSession();
            XhiveLibraryIf library = (XhiveLibraryIf)xSession.getDatabase().getRoot().getByPath(location.getCanonicalPath());
            XhiveIndexListIf indexList = library.getIndexList();
            indexList.addValueIndex("Con Path", null, "con", null, "path", 16);
            indexList.addValueIndex("Con Xuid", null, "con", null, "xuid", 16);
            session.commit();
        }
        catch (BeginFailedException bfe) {
            try {
                session.rollback();
            }
            catch (RollbackFailedException rfe) {
                LogCenter.exception(this, (Throwable)rfe);
            }
        }
        catch (CommitFailedException bfe) {
            try {
                session.rollback();
            }
            catch (RollbackFailedException rfe) {
                LogCenter.exception(this, (Throwable)rfe);
            }
        }
        catch (StoreSpecificException sse) {
            LogCenter.exception(this, (Throwable)sse);
        }
    }

    private boolean doExistsLocation(Session session, Location location) throws StoreSpecificException, DeadlockException {
        if (location.isRoot()) {
            return true;
        }
        return this.getVFSLocation(location).exists(session);
    }

    private void doCreateLocation(Session session, Location location, boolean createPath) throws LocationAlreadyExistsException, LocationNotFoundException, TypeConflictException, StoreSpecificException, DeadlockException {
        if (location.isRoot()) {
            throw new LocationAlreadyExistsException("Create failed : Root Location cannot be created.");
        }
        try {
            XhiveSessionIf xSession = (XhiveSessionIf)session.getSession();
            XhiveLibraryIf crLib = (XhiveLibraryIf)xSession.getDatabase().getRoot().getByPath(this.crLocation.getCanonicalPath());
            XhiveIndexListIf indexList = crLib.getIndexList();
            XhiveIndexIf index = indexList.getIndex("Con Path");
            Location vfsLocation = this.getVFSLocation(location);
            XhiveLibraryIf root = xSession.getDatabase().getRoot();
            XhiveLibraryIf library = (XhiveLibraryIf)root.getByPath(vfsLocation.getCanonicalPath());
            if (library != null) {
                throw new LocationAlreadyExistsException("Location " + location.getCanonicalPath() + " already exists.");
            }
            if (index.getNodeByKey((Object)location.getParent().getChildContainer(location.getName()).getCanonicalPath()) != null) {
                throw new TypeConflictException("Location " + location.getCanonicalPath() + " could not be created, a Container already exists at the same Location.");
            }
            this.doCreateInParent(xSession, root, index, location, vfsLocation, createPath);
        }
        catch (XhiveDeadlockException xde) {
            throw new DeadlockException((Throwable)xde);
        }
        catch (XhiveException xe) {
            throw new StoreSpecificException((Throwable)xe);
        }
    }

    private XhiveLibraryIf doCreateInParent(XhiveSessionIf xSession, XhiveLibraryIf root, XhiveIndexIf index, Location location, Location vfsLocation, boolean createPath) throws LocationNotFoundException, TypeConflictException {
        Location parent = location.getParent();
        if (!parent.isRoot() && index.getNodeByKey((Object)parent.getParent().getChildContainer(parent.getName()).getCanonicalPath()) != null) {
            throw new TypeConflictException("Location " + location.getCanonicalPath() + " could not be created, a Container already exists at the same Location.");
        }
        Location vfsParent = vfsLocation.getParent();
        XhiveLibraryIf parentLib = (XhiveLibraryIf)root.getByPath(vfsParent.getCanonicalPath());
        if (parentLib == null) {
            if (createPath) {
                parentLib = this.doCreateInParent(xSession, root, index, location.getParent(), vfsLocation.getParent(), createPath);
            } else {
                throw new LocationNotFoundException("Location " + vfsLocation.getCanonicalPath() + " could not be created, parent Location was not found.");
            }
        }
        XhiveLibraryIf childLib = parentLib.createLibrary(128);
        childLib.setName(vfsLocation.getName());
        parentLib.appendChild((Node)childLib);
        XhiveDocumentIf libraryChild = childLib.createDocument(null, "containers", null);
        libraryChild.setName("Containers.xml");
        childLib.appendChild((Node)libraryChild);
        return childLib;
    }

    private List<String> doDeleteLocation(Session session, Location location) throws LocationNotFoundException, IllegalActionException, StoreSpecificException, DeadlockException {
        List<String> result = this.getObsoleteXUIDs(session, location);
        this.getVFSLocation(location).delete(session);
        return result;
    }

    private List<StoreChild> doListChildren(Session session, Location location, boolean includeLocations, boolean includeContainers) throws LocationNotFoundException, StoreSpecificException, DeadlockException {
        try {
            ArrayList<StoreChild> result = new ArrayList<StoreChild>();
            if (!this.doExistsLocation(session, location)) {
                throw new LocationNotFoundException("Children could not be listed for Location " + location.getCanonicalPath() + ", the Location does not exist.");
            }
            if (includeLocations) {
                Collection<StoreChild> vfsChildren = this.getVFSLocation(location).listChildren(session, includeLocations, false, false);
                for (StoreChild child : vfsChildren) {
                    result.add(location.getChildLocation(child.getName()));
                }
            }
            if (includeContainers) {
                XhiveSessionIf xSession = (XhiveSessionIf)session.getSession();
                XhiveLibraryIf root = xSession.getDatabase().getRoot();
                XhiveDocumentIf document = (XhiveDocumentIf)root.getByPath(this.getVFSLocation(location).getCanonicalPath() + "/" + "Containers.xml");
                XhiveElementIf rootElement = document.getDocumentElement();
                NodeList children = rootElement.getChildNodes();
                for (int counter = 0; counter < children.getLength(); ++counter) {
                    Element childElement = (Element)children.item(counter);
                    String name = childElement.getAttribute("name");
                    String xuid = childElement.getAttribute("xuid");
                    Container childContainer = location.getChildContainer(name);
                    ((XAMContainer)childContainer).setXUID(xuid);
                    result.add(childContainer);
                }
            }
            return result;
        }
        catch (XhiveDeadlockException xde) {
            throw new DeadlockException((Throwable)xde);
        }
        catch (XhiveException xe) {
            throw new StoreSpecificException((Throwable)xe);
        }
    }

    private List<StoreChild> doListChildren(Session session, Location location, boolean includeLocations, boolean includeContainers, boolean recurse) throws LocationNotFoundException, StoreSpecificException, DeadlockException {
        ArrayList<StoreChild> result = new ArrayList<StoreChild>();
        for (StoreChild child : this.doListChildren(session, location, includeLocations || recurse, includeContainers)) {
            if (includeContainers && child.isContainer() || includeLocations && child.isLocation()) {
                result.add(child);
            }
            if (!recurse || !child.isLocation()) continue;
            result.addAll(this.doListChildren(session, (Location)child, includeLocations, includeContainers, recurse));
        }
        return result;
    }

    private String doGetXUID(Session session, Container container) throws StoreSpecificException, DeadlockException {
        return this.doGetAttribute(session, container, "xuid");
    }

    private int doGetXUIDCount(Session session, String xuid) {
        XhiveSessionIf xSession = (XhiveSessionIf)session.getSession();
        XhiveLibraryIf crLibrary = (XhiveLibraryIf)xSession.getDatabase().getRoot().getByPath(this.crLocation.getCanonicalPath());
        XhiveIndexListIf indexList = crLibrary.getIndexList();
        XhiveIndexIf index = indexList.getIndex("Con Xuid");
        int count = 0;
        XhiveNodeIteratorIf iterator = index.getNodesByKey((Object)xuid);
        if (iterator != null) {
            while (iterator.hasNext()) {
                XhiveNodeIf node = iterator.next();
                if (node == null) continue;
                ++count;
            }
        }
        return count;
    }

    private boolean doExistsContainer(Session session, Container container) throws StoreSpecificException, DeadlockException {
        try {
            XhiveSessionIf xSession = (XhiveSessionIf)session.getSession();
            XhiveLibraryIf library = (XhiveLibraryIf)xSession.getDatabase().getRoot().getByPath(this.crLocation.getCanonicalPath());
            XhiveIndexListIf indexList = library.getIndexList();
            XhiveIndexIf index = indexList.getIndex("Con Path");
            return index.getNodeByKey((Object)container.getCanonicalPath()) != null;
        }
        catch (XhiveDeadlockException xde) {
            throw new DeadlockException((Throwable)xde);
        }
        catch (XhiveException xe) {
            throw new StoreSpecificException((Throwable)xe);
        }
    }

    private String doCreateTempContainer(Session session, Container container, Map<String, String> attributes, boolean overwrite) throws LocationNotFoundException, ContainerAlreadyExistsException, TypeConflictException, StoreSpecificException, DeadlockException {
        try {
            XhiveSessionIf xSession = (XhiveSessionIf)session.getSession();
            XhiveLibraryIf crLibrary = (XhiveLibraryIf)xSession.getDatabase().getRoot().getByPath(this.crLocation.getCanonicalPath());
            XhiveLibraryChildIf parentLibrary = crLibrary.getByPath(container.getLocation().getCanonicalPath().substring(1));
            if (parentLibrary == null) {
                throw new LocationNotFoundException("Parent Location for Container " + container.getCanonicalPath() + " does not exist.");
            }
            XhiveLibraryChildIf conflictLibrary = parentLibrary.getByPath(container.getName());
            if (conflictLibrary != null && conflictLibrary instanceof XhiveLibraryIf) {
                throw new TypeConflictException("Location already exists at path for Container " + container.getCanonicalPath());
            }
            XhiveIndexListIf indexList = crLibrary.getIndexList();
            XhiveIndexIf index = indexList.getIndex("Con Path");
            Element element = (Element)index.getNodeByKey((Object)container.getCanonicalPath());
            if (element != null) {
                if (overwrite) {
                    String xuid = element.getAttribute("xuid");
                    element.setAttribute("xuid", "NOXUIDASSIGNED");
                    if (this.doGetXUIDCount(session, xuid) == 0) {
                        return xuid;
                    }
                    return null;
                }
                throw new ContainerAlreadyExistsException("Container already exists at path for Container " + container.getCanonicalPath());
            }
            XhiveLibraryChildIf ctrsDoc = crLibrary.getByPath(container.getLocation().getCanonicalPath().substring(1) + "Containers.xml");
            if (ctrsDoc != null && ctrsDoc instanceof XhiveDocumentIf) {
                XhiveDocumentIf doc = (XhiveDocumentIf)ctrsDoc;
                XhiveElementIf root = doc.getDocumentElement();
                XhiveElementIf newElement = doc.createElement("con");
                if (attributes != null) {
                    for (Map.Entry<String, String> attribute : attributes.entrySet()) {
                        newElement.setAttribute(attribute.getKey(), attribute.getValue());
                    }
                }
                newElement.setAttribute("name", container.getName());
                newElement.setAttribute("path", container.getCanonicalPath());
                root.appendChild((Node)newElement);
            }
            return null;
        }
        catch (XhiveDeadlockException xde) {
            throw new DeadlockException((Throwable)xde);
        }
        catch (XhiveException xe) {
            throw new StoreSpecificException((Throwable)xe);
        }
    }

    private void doUpdateContainer(Session session, Container container, Map<String, String> attributes, boolean overwrite) throws StoreSpecificException, DeadlockException {
        try {
            XhiveSessionIf xSession = (XhiveSessionIf)session.getSession();
            XhiveLibraryIf crLibrary = (XhiveLibraryIf)xSession.getDatabase().getRoot().getByPath(this.crLocation.getCanonicalPath());
            XhiveIndexListIf indexList = crLibrary.getIndexList();
            XhiveIndexIf index = indexList.getIndex("Con Path");
            Element element = (Element)index.getNodeByKey((Object)container.getCanonicalPath());
            if (element != null && attributes != null) {
                for (Map.Entry<String, String> attribute : attributes.entrySet()) {
                    element.setAttribute(attribute.getKey(), attribute.getValue());
                }
            }
        }
        catch (XhiveDeadlockException xde) {
            throw new DeadlockException((Throwable)xde);
        }
        catch (XhiveException xe) {
            throw new StoreSpecificException((Throwable)xe);
        }
    }

    private String doDeleteContainer(Session session, Container container) throws ContainerNotFoundException, StoreSpecificException, DeadlockException {
        try {
            XhiveSessionIf xSession = (XhiveSessionIf)session.getSession();
            XhiveLibraryIf library = (XhiveLibraryIf)xSession.getDatabase().getRoot().getByPath(this.crLocation.getCanonicalPath());
            XhiveIndexListIf indexList = library.getIndexList();
            XhiveIndexIf index = indexList.getIndex("Con Path");
            Element element = (Element)index.getNodeByKey((Object)container.getCanonicalPath());
            if (element != null) {
                String xuid = element.getAttribute("xuid");
                element.getParentNode().removeChild(element);
                if (this.doGetXUIDCount(session, xuid) == 0) {
                    return xuid;
                }
                return null;
            }
            throw new ContainerNotFoundException("Container with path " + container.getCanonicalPath() + " could not be found for delete().");
        }
        catch (XhiveDeadlockException xde) {
            throw new DeadlockException((Throwable)xde);
        }
        catch (XhiveException xe) {
            throw new StoreSpecificException((Throwable)xe);
        }
    }

    private String doGetAttribute(Session session, Container container, String name) throws StoreSpecificException, DeadlockException {
        try {
            XhiveSessionIf xSession = (XhiveSessionIf)session.getSession();
            XhiveLibraryIf library = (XhiveLibraryIf)xSession.getDatabase().getRoot().getByPath(this.crLocation.getCanonicalPath());
            XhiveIndexListIf indexList = library.getIndexList();
            XhiveIndexIf index = indexList.getIndex("Con Path");
            Element element = (Element)index.getNodeByKey((Object)container.getCanonicalPath());
            return element == null ? null : element.getAttribute(name);
        }
        catch (XhiveDeadlockException xde) {
            throw new DeadlockException((Throwable)xde);
        }
        catch (XhiveException xe) {
            throw new StoreSpecificException((Throwable)xe);
        }
    }

    private Map<String, String> doGetAttributes(Session session, Container container, List<String> names) throws StoreSpecificException, DeadlockException {
        try {
            XhiveSessionIf xSession = (XhiveSessionIf)session.getSession();
            XhiveLibraryIf library = (XhiveLibraryIf)xSession.getDatabase().getRoot().getByPath(this.crLocation.getCanonicalPath());
            XhiveIndexListIf indexList = library.getIndexList();
            XhiveIndexIf index = indexList.getIndex("Con Path");
            Element element = (Element)index.getNodeByKey((Object)container.getCanonicalPath());
            HashMap<String, String> attrs = new HashMap<String, String>();
            if (element != null) {
                NamedNodeMap map = element.getAttributes();
                for (int counter = 0; counter < map.getLength(); ++counter) {
                    Attr attr = (Attr)map.item(counter);
                    attrs.put(attr.getName(), attr.getValue());
                }
            }
            return attrs;
        }
        catch (XhiveDeadlockException xde) {
            throw new DeadlockException((Throwable)xde);
        }
        catch (XhiveException xe) {
            throw new StoreSpecificException((Throwable)xe);
        }
    }

    private List<String> doMoveLocation(Session session, Location source, Location target, boolean replace) throws LocationNotFoundException, LocationAlreadyExistsException, IllegalActionException, TypeConflictException, StoreSpecificException, DeadlockException {
        return this.doMoveOrCopyLocation(session, source, target, replace, true);
    }

    private List<String> doCopyLocation(Session session, Location source, Location target, boolean replace) throws LocationNotFoundException, LocationAlreadyExistsException, IllegalActionException, TypeConflictException, StoreSpecificException, DeadlockException {
        return this.doMoveOrCopyLocation(session, source, target, replace, false);
    }

    private List<String> doMoveOrCopyLocation(Session session, Location source, Location target, boolean replace, boolean move) throws LocationNotFoundException, LocationAlreadyExistsException, IllegalActionException, TypeConflictException, StoreSpecificException, DeadlockException {
        try {
            String opName;
            String string = opName = move ? "Move" : "Copy";
            if (source.isRoot() && move) {
                throw new IllegalActionException("The Root Location cannot be moved.");
            }
            boolean sourceExists = this.doExistsLocation(session, source);
            boolean targetExists = this.doExistsLocation(session, target);
            if (targetExists && !replace) {
                throw new LocationAlreadyExistsException("Target Location for " + opName + " already exists at path " + target.getCanonicalPath());
            }
            if (this.doExistsContainer(session, source.getParent().getChildContainer(source.getName()))) {
                throw new TypeConflictException("Container exists at path for source Location " + source.getCanonicalPath());
            }
            ArrayList<String> result = new ArrayList<String>();
            if (targetExists) {
                if (!sourceExists) {
                    throw new LocationNotFoundException("Source Location for " + opName + " doesn't exist at path " + source.getCanonicalPath());
                }
                result.addAll(this.doDeleteLocation(session, target));
                this.doCreateLocation(session, target, false);
            } else {
                if (!sourceExists) {
                    throw new LocationNotFoundException("Source Location for " + opName + " doesn't exist at path " + source.getCanonicalPath());
                }
                if (this.doExistsContainer(session, target.getParent().getChildContainer(target.getName()))) {
                    throw new TypeConflictException("Container exists at path for target Location " + target.getCanonicalPath());
                }
                boolean targetParentExists = this.doExistsLocation(session, target.getParent());
                if (!targetParentExists) {
                    throw new LocationNotFoundException("Target parent does not exist for " + opName + " for path " + target.getCanonicalPath());
                }
                this.doCreateLocation(session, target, false);
            }
            this.doMoveOrCopyChildren(session, source, target, move);
            if (move) {
                this.doDeleteLocation(session, source);
            }
            return result;
        }
        catch (XhiveDeadlockException xde) {
            throw new DeadlockException((Throwable)xde);
        }
        catch (XhiveException xe) {
            throw new StoreSpecificException((Throwable)xe);
        }
    }

    private void doMoveOrCopyChildren(Session session, Location source, Location target, boolean move) throws DeadlockException, StoreSpecificException, LocationNotFoundException, TypeConflictException, LocationAlreadyExistsException, IllegalActionException {
        List<StoreChild> children = this.listChildren(source, true, true);
        for (StoreChild child : children) {
            if (child instanceof Location) {
                Location newChild = target.getChildLocation(child.getName());
                this.doCreateLocation(session, newChild, false);
                this.doMoveOrCopyChildren(session, (Location)child, newChild, move);
                if (!move) continue;
                this.doDeleteLocation(session, (Location)child);
                continue;
            }
            Container newChild = target.getChildContainer(child.getName());
            try {
                this.doMoveOrCopyContainer(session, (Container)child, newChild, false, move);
            }
            catch (Exception e) {
                LogCenter.exception(this, (Throwable)e);
            }
        }
    }

    private String doMoveContainer(Session session, Container source, Container target, boolean replace) throws LocationNotFoundException, ContainerNotFoundException, ContainerAlreadyExistsException, TypeConflictException, IllegalActionException, StoreSpecificException, DeadlockException {
        return this.doMoveOrCopyContainer(session, source, target, replace, true);
    }

    private String doCopyContainer(Session session, Container source, Container target, boolean replace) throws LocationNotFoundException, ContainerNotFoundException, ContainerAlreadyExistsException, TypeConflictException, IllegalActionException, StoreSpecificException, DeadlockException {
        return this.doMoveOrCopyContainer(session, source, target, replace, false);
    }

    private String doMoveOrCopyContainer(Session session, Container source, Container target, boolean replace, boolean move) throws LocationNotFoundException, ContainerNotFoundException, ContainerAlreadyExistsException, TypeConflictException, IllegalActionException, StoreSpecificException, DeadlockException {
        try {
            String opName;
            String string = opName = move ? "Move" : "Copy";
            if (this.doExistsLocation(session, source.getLocation().getChildLocation(source.getName()))) {
                throw new TypeConflictException("Location exists at path for source Container " + source.getCanonicalPath());
            }
            String sourceXuid = this.doGetXUID(session, source);
            if (sourceXuid == null) {
                throw new ContainerNotFoundException("Source Container for " + opName + " doesn't exist at path " + source.getCanonicalPath());
            }
            String result = null;
            String targetXuid = this.getXUID(target);
            if (targetXuid != null) {
                if (replace) {
                    result = this.doDeleteContainer(session, target);
                } else {
                    throw new ContainerAlreadyExistsException("Target Container for " + opName + " already exists at path " + target.getCanonicalPath());
                }
            }
            if (!this.doExistsLocation(session, target.getLocation())) {
                throw new LocationNotFoundException("Parent Location not found for target Container " + target.getCanonicalPath());
            }
            if (this.doExistsLocation(session, target.getLocation().getChildLocation(target.getName()))) {
                throw new TypeConflictException("Location exists at path for target Container " + target.getCanonicalPath());
            }
            Map<String, String> attributes = this.doGetAttributes(session, source, null);
            attributes.remove("path");
            this.doCreateTempContainer(session, target, attributes, replace);
            this.doUpdateContainer(session, target, attributes, replace);
            if (move) {
                this.doDeleteContainer(session, source);
            }
            return result;
        }
        catch (XhiveDeadlockException xde) {
            throw new DeadlockException((Throwable)xde);
        }
        catch (XhiveException xe) {
            throw new StoreSpecificException((Throwable)xe);
        }
    }

    private List<String> getObsoleteXUIDs(Session session, Location location) throws LocationNotFoundException, StoreSpecificException, DeadlockException {
        List<StoreChild> containers = this.doListChildren(session, location, false, true, true);
        HashMap<String, Integer> countMap = new HashMap<String, Integer>();
        for (StoreChild container : containers) {
            XAMContainer xamContainer = (XAMContainer)container;
            String xuid = xamContainer.getXUID();
            Integer count = (Integer)countMap.get(xuid);
            if (count == null) {
                countMap.put(xuid, 1);
                continue;
            }
            countMap.put(xuid, count + 1);
        }
        ArrayList<String> result = new ArrayList<String>();
        for (Map.Entry entry : countMap.entrySet()) {
            if (this.doGetXUIDCount(session, (String)entry.getKey()) != ((Integer)entry.getValue()).intValue()) continue;
            result.add((String)entry.getKey());
        }
        return result;
    }

    private class MoveOrCopyContainerTransaction
    extends CRTransaction<String> {
        private final Container source;
        private final Container target;
        private final boolean replace;
        private final boolean move;

        protected MoveOrCopyContainerTransaction(Container source, Container target, boolean replace, boolean move) {
            super(false);
            this.source = source;
            this.target = target;
            this.replace = replace;
            this.move = move;
        }

        @Override
        protected String doLogic(Session session) throws Exception {
            if (this.move) {
                return XAMContentRegistry.this.doMoveContainer(session, this.source, this.target, this.replace);
            }
            return XAMContentRegistry.this.doCopyContainer(session, this.source, this.target, this.replace);
        }
    }

    private class MoveOrCopyLocationTransaction
    extends CRTransaction<List<String>> {
        private final Location source;
        private final Location target;
        private final boolean replace;
        private final boolean move;

        protected MoveOrCopyLocationTransaction(Location source, Location target, boolean replace, boolean move) {
            super(false);
            this.source = source;
            this.target = target;
            this.replace = replace;
            this.move = move;
        }

        @Override
        protected List<String> doLogic(Session session) throws Exception {
            if (this.move) {
                return XAMContentRegistry.this.doMoveLocation(session, this.source, this.target, this.replace);
            }
            return XAMContentRegistry.this.doCopyLocation(session, this.source, this.target, this.replace);
        }
    }

    private class GetAttributesTransaction
    extends CRTransaction<Map<String, String>> {
        private final Container container;
        private final List<String> names;

        protected GetAttributesTransaction(Container container, List<String> names) {
            super(true);
            this.container = container;
            this.names = names;
        }

        @Override
        protected Map<String, String> doLogic(Session session) throws Exception {
            return XAMContentRegistry.this.doGetAttributes(session, this.container, this.names);
        }
    }

    private class GetAttributeTransaction
    extends CRTransaction<String> {
        private final Container container;
        private final String name;

        protected GetAttributeTransaction(Container container, String name) {
            super(true);
            this.container = container;
            this.name = name;
        }

        @Override
        protected String doLogic(Session session) throws Exception {
            return XAMContentRegistry.this.doGetAttribute(session, this.container, this.name);
        }
    }

    private class DeleteContainerTransaction
    extends CRTransaction<String> {
        private final Container container;

        protected DeleteContainerTransaction(Container container) {
            super(false);
            this.container = container;
        }

        @Override
        protected String doLogic(Session session) throws Exception {
            return XAMContentRegistry.this.doDeleteContainer(session, this.container);
        }
    }

    private class UpdateContainerTransaction
    extends CRTransaction<Void> {
        private final Container container;
        private final Map<String, String> attributes;
        private final boolean replace;

        protected UpdateContainerTransaction(Container container, Map<String, String> attributes, boolean replace) {
            super(false);
            this.container = container;
            this.attributes = attributes;
            this.replace = replace;
        }

        @Override
        protected Void doLogic(Session session) throws Exception {
            try {
                XAMContentRegistry.this.doUpdateContainer(session, this.container, this.attributes, this.replace);
            }
            catch (DeadlockException de) {
                throw de;
            }
            catch (Exception e) {
                XAMContentRegistry.this.doDeleteContainer(session, this.container);
            }
            return null;
        }
    }

    private class CreateTempContainerTransaction
    extends CRTransaction<String> {
        private final Container container;
        private final Map<String, String> attributes;
        private final boolean replace;

        protected CreateTempContainerTransaction(Container container, Map<String, String> attributes, boolean replace) {
            super(false);
            this.container = container;
            this.attributes = attributes;
            this.replace = replace;
        }

        @Override
        protected String doLogic(Session session) throws Exception {
            return XAMContentRegistry.this.doCreateTempContainer(session, this.container, this.attributes, this.replace);
        }
    }

    private class ExistsContainerTransaction
    extends CRTransaction<Boolean> {
        private final Container container;

        protected ExistsContainerTransaction(Container container) {
            super(true);
            this.container = container;
        }

        @Override
        protected Boolean doLogic(Session session) throws Exception {
            return XAMContentRegistry.this.doExistsContainer(session, this.container);
        }
    }

    private class GetXUIDTransaction
    extends CRTransaction<String> {
        private final Container container;

        protected GetXUIDTransaction(Container container) {
            super(true);
            this.container = container;
        }

        @Override
        protected String doLogic(Session session) throws Exception {
            return XAMContentRegistry.this.doGetXUID(session, this.container);
        }
    }

    private class GetXUIDCountTransaction
    extends CRTransaction<Integer> {
        private final String xuid;

        protected GetXUIDCountTransaction(String xuid) {
            super(true);
            this.xuid = xuid;
        }

        @Override
        protected Integer doLogic(Session session) throws Exception {
            return XAMContentRegistry.this.doGetXUIDCount(session, this.xuid);
        }
    }

    private class ListChildrenTransaction
    extends CRTransaction<List<StoreChild>> {
        private final Location location;
        private final boolean includeLocations;
        private final boolean includeContainers;
        private final boolean recurse;

        protected ListChildrenTransaction(Location location, boolean includeLocations, boolean includeContainers, boolean recurse) {
            super(true);
            this.location = location;
            this.includeLocations = includeLocations;
            this.includeContainers = includeContainers;
            this.recurse = recurse;
        }

        @Override
        protected List<StoreChild> doLogic(Session session) throws Exception {
            return XAMContentRegistry.this.doListChildren(session, this.location, this.includeLocations, this.includeContainers, this.recurse);
        }
    }

    private class DeleteLocationTransaction
    extends CRTransaction<List<String>> {
        private final Location location;

        protected DeleteLocationTransaction(Location location) {
            super(false);
            this.location = location;
        }

        @Override
        protected List<String> doLogic(Session session) throws Exception {
            return XAMContentRegistry.this.doDeleteLocation(session, this.location);
        }
    }

    private class CreateLocationTransaction
    extends CRTransaction<Void> {
        private final Location location;
        private final boolean createPath;

        protected CreateLocationTransaction(Location location, boolean createPath) {
            super(false);
            this.location = location;
            this.createPath = createPath;
        }

        @Override
        protected Void doLogic(Session session) throws Exception {
            XAMContentRegistry.this.doCreateLocation(session, this.location, this.createPath);
            return null;
        }
    }

    private class ExistsLocationTransaction
    extends CRTransaction<Boolean> {
        private final Location location;

        protected ExistsLocationTransaction(Location location) {
            super(true);
            this.location = location;
        }

        @Override
        protected Boolean doLogic(Session session) throws Exception {
            return XAMContentRegistry.this.doExistsLocation(session, this.location);
        }
    }

    private abstract class CRTransaction<T> {
        private final boolean readOnly;

        protected CRTransaction(boolean readOnly) {
            this.readOnly = readOnly;
        }

        abstract T doLogic(Session var1) throws Exception;

        /*
         * Loose catch block
         * Enabled aggressive block sorting
         * Enabled unnecessary exception pruning
         * Enabled aggressive exception aggregation
         * Lifted jumps to return sites
         */
        protected T execute() throws Exception {
            Store store = XAMContentRegistry.this.crLocation.getStore();
            Session session = store.getSession(XAMContentRegistry.this.appUsr.getStoreUser(store.getAlias()), this.readOnly);
            int counter = 0;
            do {
                try {
                    session.begin();
                    T result = this.doLogic(session);
                    session.commit();
                    return result;
                }
                catch (DeadlockException de) {
                    if (counter == 200) {
                        session.rollback();
                        throw new StoreSpecificException("Concurrent transaction failure in Content Registry :", de);
                    }
                    ((XhiveSessionIf)session.getSession()).rollback();
                    ++counter;
                    continue;
                }
                catch (XhiveDeadlockException xde) {
                    if (counter == 200) {
                        session.rollback();
                        throw new StoreSpecificException("Concurrent transaction failure in Content Registry :", (Throwable)xde);
                    }
                    ((XhiveSessionIf)session.getSession()).rollback();
                    ++counter;
                    continue;
                }
                catch (Exception e) {
                    session.rollback();
                    throw e;
                }
                catch (Throwable t) {
                    session.rollback();
                    throw new StoreSpecificException("Error in " + this.getClass().getCanonicalName(), t);
                }
            } while (true);
            
        }
    }

}

