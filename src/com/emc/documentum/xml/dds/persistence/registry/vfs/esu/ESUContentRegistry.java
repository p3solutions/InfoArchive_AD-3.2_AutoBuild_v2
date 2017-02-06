/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  com.xhive.dom.interfaces.XhiveNodeIf
 *  com.xhive.query.interfaces.XhiveXQueryValueIf
 */
package com.emc.documentum.xml.dds.persistence.registry.vfs.esu;

import com.emc.documentum.xml.dds.DDS;
import com.emc.documentum.xml.dds.application.Application;
import com.emc.documentum.xml.dds.exception.DDSException;
import com.emc.documentum.xml.dds.operation.exception.OperationException;
import com.emc.documentum.xml.dds.operation.framework.Operation;
import com.emc.documentum.xml.dds.operation.library.persistence.CreateLocationOperation;
import com.emc.documentum.xml.dds.operation.library.persistence.ExistsStoreChildOperation;
import com.emc.documentum.xml.dds.operation.library.persistence.PersistOperation;
import com.emc.documentum.xml.dds.operation.library.xquery.ExecuteXQueryOperation;
import com.emc.documentum.xml.dds.persistence.Container;
import com.emc.documentum.xml.dds.persistence.ContentDescriptor;
import com.emc.documentum.xml.dds.persistence.Data;
import com.emc.documentum.xml.dds.persistence.Location;
import com.emc.documentum.xml.dds.persistence.LocationOptions;
import com.emc.documentum.xml.dds.persistence.Store;
import com.emc.documentum.xml.dds.persistence.StoreChild;
import com.emc.documentum.xml.dds.persistence.content.XMLContentDescriptor;
import com.emc.documentum.xml.dds.persistence.data.StringData;
import com.emc.documentum.xml.dds.persistence.exception.DeadlockException;
import com.emc.documentum.xml.dds.persistence.exception.IllegalActionException;
import com.emc.documentum.xml.dds.persistence.exception.LocationAlreadyExistsException;
import com.emc.documentum.xml.dds.persistence.exception.LocationNotFoundException;
import com.emc.documentum.xml.dds.persistence.exception.StoreSpecificException;
import com.emc.documentum.xml.dds.persistence.exception.TypeConflictException;
import com.emc.documentum.xml.dds.persistence.registry.ContentRegistry;
import com.emc.documentum.xml.dds.user.User;
import com.emc.documentum.xml.dds.xquery.XQueryResultHandler;
import com.xhive.dom.interfaces.XhiveNodeIf;
import com.xhive.query.interfaces.XhiveXQueryValueIf;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class ESUContentRegistry
implements ContentRegistry {
    private static final String DOCNAME = "ContentRegistry.xml";
    private static final String ROOTELEMENTNAME = "cr";
    private static final String LOCATIONELEMENTNAME = "loc";
    private static final String CONTAINERELEMENTNAME = "con";
    private static final String NAMEATTRIBUTE = "name";
    private final Application application;
    private final Location crLocation;
    private final Container crContainer;

    public ESUContentRegistry(Application application, Location crLocation) {
        this.application = application;
        this.crLocation = crLocation;
        this.crContainer = this.crLocation.getChildContainer("ContentRegistry.xml");
    }

    public Location getContentRegistryLocation() {
        return this.crLocation;
    }

    public Container getContentRegistryContainer() {
        return this.crContainer;
    }

    public void initialize() throws DDSException {
        if (!((Boolean)this.application.execute(this.application.getApplicationUser(), new ExistsStoreChildOperation(this.crContainer.getLocation()))).booleanValue()) {
            this.application.execute(this.application.getApplicationUser(), new CreateLocationOperation(this.crContainer.getLocation(), null, true));
        }
        if (!((Boolean)this.application.execute(this.application.getApplicationUser(), new ExistsStoreChildOperation(this.crContainer))).booleanValue()) {
            this.application.execute(this.application.getApplicationUser(), new PersistOperation(this.crContainer, new XMLContentDescriptor(), new StringData("<cr/>"), true));
        }
    }

    @Override
    public boolean existsLocation(Location location) throws OperationException {
        if (location.isRoot()) {
            return true;
        }
        return (Boolean)this.application.execute(DDS.getApplication().getApplicationUser(), new ExecuteXQueryOperation(this.crContainer.getStore(), this.getFullXpathExpression(location), null, (XQueryResultHandler)new ExistsResultHandler(), true));
    }

    @Override
    public void createLocation(Location location, Map<String, String> attributes, boolean createPath) throws LocationAlreadyExistsException, LocationNotFoundException, TypeConflictException, StoreSpecificException, DeadlockException {
        if (location.isRoot()) {
            throw new LocationAlreadyExistsException("Create failed : Root Location cannot be created.");
        }
        try {
            if (this.existsLocation(location)) {
                throw new LocationAlreadyExistsException("Create failed : Location already exists :" + location.getPath());
            }
            if (!createPath && !location.getParent().isRoot() && this.existsContainer(location.getParent().getParent().getChildContainer(location.getParent().getName()))) {
                throw new TypeConflictException("Create Location failed : Path contains a Container : " + location.getPath());
            }
            if (!createPath && !this.existsLocation(location.getParent())) {
                throw new LocationNotFoundException("Create failed : Path does not exist : " + location.getPath());
            }
        }
        catch (OperationException oe) {
            if (oe.getCause() instanceof DeadlockException) {
                throw (DeadlockException)oe.getCause();
            }
            throw new StoreSpecificException("Create failed : ContentRegistry access issue.", oe);
        }
        StringBuilder xQuery = new StringBuilder("(");
        xQuery.append("fn:document('");
        xQuery.append(this.crContainer.getPath());
        xQuery.append("'),");
        ArrayList<String> locationList = new ArrayList<String>();
        Location currentLocation = location;
        while (!currentLocation.isRoot()) {
            locationList.add(0, this.getFullXpathExpression(currentLocation));
            locationList.add(0, this.getFullXpathExpression(currentLocation.getParent().getChildContainer(currentLocation.getName())));
            currentLocation = currentLocation.getParent();
        }
        for (int counter = 0; counter < locationList.size(); ++counter) {
            xQuery.append((String)locationList.get(counter));
            if (counter >= locationList.size() - 1) continue;
            xQuery.append(",");
        }
        xQuery.append(")");
        try {
            CreateLocationResultHandler handler = new CreateLocationResultHandler(location, attributes);
            DDS.getApplication().execute(DDS.getApplication().getApplicationUser(), new ExecuteXQueryOperation(this.crContainer.getStore(), xQuery.toString(), null, (XQueryResultHandler)handler, false));
            if (handler.isTypeConflict()) {
                throw new TypeConflictException("Create Location failed : Path contains a Container : " + location.getPath());
            }
        }
        catch (OperationException oe) {
            if (oe.getCause() instanceof DeadlockException) {
                throw (DeadlockException)oe.getCause();
            }
            throw new StoreSpecificException("Create failed : ContentRegistry access issue.", oe);
        }
    }

    @Override
    public void moveLocation(Location source, Location target) throws DDSException {
        if (source.isRoot()) {
            throw new IllegalActionException("Tried to move root Location.");
        }
        StringBuilder xQuery = new StringBuilder("(");
        xQuery.append(this.getFullXpathExpression(source));
        xQuery.append(",");
        xQuery.append(this.getFullXpathExpression(target.getParent()));
        xQuery.append(")");
        DDS.getApplication().execute(DDS.getApplication().getApplicationUser(), new ExecuteXQueryOperation(this.crContainer.getStore(), xQuery.toString(), null, (XQueryResultHandler)new MoveResultHandler(this, target.getName()), false));
    }

    @Override
    public void deleteLocation(Location location) throws LocationNotFoundException, IllegalActionException, StoreSpecificException, DeadlockException {
        if (location.isRoot()) {
            throw new IllegalActionException("Delete Failed : Tried to delete root Location.");
        }
        try {
            if (!this.existsLocation(location)) {
                throw new LocationNotFoundException("Delete Failed : Location not found :" + location.getPath());
            }
            DDS.getApplication().execute(DDS.getApplication().getApplicationUser(), new ExecuteXQueryOperation(this.crContainer.getStore(), this.getFullXpathExpression(location), null, (XQueryResultHandler)new DeleteResultHandler(), false));
        }
        catch (OperationException oe) {
            if (oe.getCause() instanceof DeadlockException) {
                throw (DeadlockException)oe.getCause();
            }
            throw new StoreSpecificException("Create failed : ContentRegistry access issue.", oe);
        }
    }

    @Override
    public List<StoreChild> listChildren(Location location, boolean includeLocations, boolean includeContainers) throws LocationNotFoundException, StoreSpecificException, DeadlockException {
        try {
            if (!this.existsLocation(location)) {
                throw new LocationNotFoundException("ListChildren failed : Location not found.");
            }
        }
        catch (OperationException oe) {
            if (oe.getCause() instanceof DeadlockException) {
                throw (DeadlockException)oe.getCause();
            }
            throw new StoreSpecificException("ListChildren failed : ContentRegistry access issue.", oe);
        }
        if (!includeLocations && !includeContainers) {
            return new ArrayList<StoreChild>();
        }
        StringBuilder xQuery = new StringBuilder(this.getFullXpathExpression(location));
        xQuery.append("/");
        if (includeLocations && !includeContainers) {
            xQuery.append("loc");
        } else if (!includeLocations && includeContainers) {
            xQuery.append("con");
        } else {
            xQuery.append("*");
        }
        try {
            return (List)DDS.getApplication().execute(DDS.getApplication().getApplicationUser(), new ExecuteXQueryOperation(this.crContainer.getStore(), xQuery.toString(), null, (XQueryResultHandler)new ListChildrenResultHandler(location), true));
        }
        catch (OperationException oe) {
            throw new StoreSpecificException("ListChildren failed : ContentRegistry access issue.", oe);
        }
    }

    @Override
    public String getAttribute(Location location, String name) throws DDSException {
        StringBuilder xQuery = new StringBuilder(this.getFullXpathExpression(location));
        xQuery.append("/@").append(name);
        return (String)DDS.getApplication().execute(DDS.getApplication().getApplicationUser(), new ExecuteXQueryOperation(this.crContainer.getStore(), xQuery.toString(), null, (XQueryResultHandler)new GetAttributeResultHandler(), true));
    }

    @Override
    public Map<String, String> getAttributes(Location location, List<String> names) throws DDSException {
        StringBuilder xQuery = new StringBuilder(this.getFullXpathExpression(location));
        return (Map)DDS.getApplication().execute(DDS.getApplication().getApplicationUser(), new ExecuteXQueryOperation(this.crContainer.getStore(), xQuery.toString(), null, (XQueryResultHandler)new GetAttributesResultHandler(names), true));
    }

    public String getEsuObjectId(Container container) throws StoreSpecificException {
        try {
            StringBuilder xQuery = new StringBuilder(this.getFullXpathExpression(container));
            xQuery.append("/@esuobjectid");
            return (String)DDS.getApplication().execute(DDS.getApplication().getApplicationUser(), new ExecuteXQueryOperation(this.crContainer.getStore(), xQuery.toString(), null, (XQueryResultHandler)new GetAttributeResultHandler(), true));
        }
        catch (OperationException oe) {
            throw new StoreSpecificException("ESU object id could not be retrieved : Failed to access Content Registry.", oe);
        }
    }

    @Override
    public boolean existsContainer(Container container) throws OperationException {
        return (Boolean)DDS.getApplication().execute(DDS.getApplication().getApplicationUser(), new ExecuteXQueryOperation(this.crContainer.getStore(), this.getFullXpathExpression(container), null, (XQueryResultHandler)new ExistsResultHandler(), true));
    }

    @Override
    public void createContainer(Container container, Map<String, String> attributes, boolean overwrite) throws OperationException {
        StringBuilder xQuery = new StringBuilder("(");
        ArrayList<String> locationList = new ArrayList<String>();
        xQuery.append("fn:document('");
        xQuery.append(this.crContainer.getPath());
        xQuery.append("'),");
        Location currentLocation = container.getLocation();
        while (!currentLocation.isRoot()) {
            locationList.add(0, this.getFullXpathExpression(currentLocation));
            currentLocation = currentLocation.getParent();
        }
        for (int counter = 0; counter < locationList.size(); ++counter) {
            xQuery.append((String)locationList.get(counter));
            xQuery.append(",");
        }
        xQuery.append(this.getFullXpathExpression(container));
        xQuery.append(")");
        DDS.getApplication().execute(DDS.getApplication().getApplicationUser(), new ExecuteXQueryOperation(this.crContainer.getStore(), xQuery.toString(), null, (XQueryResultHandler)new CreateContainerResultHandler(container, attributes), false));
    }

    @Override
    public void moveContainer(Container source, Container target) throws DDSException {
        StringBuilder xQuery = new StringBuilder("(");
        xQuery.append(this.getFullXpathExpression(source));
        xQuery.append(",");
        xQuery.append(this.getFullXpathExpression(target.getLocation()));
        xQuery.append(")");
        DDS.getApplication().execute(DDS.getApplication().getApplicationUser(), new ExecuteXQueryOperation(this.crContainer.getStore(), xQuery.toString(), null, (XQueryResultHandler)new MoveResultHandler(this, target.getName()), false));
    }

    @Override
    public void deleteContainer(Container container) throws DDSException {
        DDS.getApplication().execute(DDS.getApplication().getApplicationUser(), new ExecuteXQueryOperation(this.crContainer.getStore(), this.getFullXpathExpression(container), null, (XQueryResultHandler)new DeleteResultHandler(), false));
    }

    @Override
    public String getAttribute(Container container, String name) throws DDSException {
        StringBuilder xQuery = new StringBuilder(this.getFullXpathExpression(container));
        xQuery.append("/@").append(name);
        return (String)DDS.getApplication().execute(DDS.getApplication().getApplicationUser(), new ExecuteXQueryOperation(this.crContainer.getStore(), xQuery.toString(), null, (XQueryResultHandler)new GetAttributeResultHandler(), true));
    }

    @Override
    public Map<String, String> getAttributes(Container container, List<String> names) throws DDSException {
        StringBuilder xQuery = new StringBuilder(this.getFullXpathExpression(container));
        return (Map)DDS.getApplication().execute(DDS.getApplication().getApplicationUser(), new ExecuteXQueryOperation(this.crContainer.getStore(), xQuery.toString(), null, (XQueryResultHandler)new GetAttributesResultHandler(names), true));
    }

    private String getFullXpathExpression(Location location) {
        StringBuilder result = new StringBuilder("fn:document('");
        result.append(this.crContainer.getPath());
        result.append("')/");
        result.append("cr");
        result.append(this.getXpathExpression(location));
        return result.toString();
    }

    private String getXpathExpression(Location location) {
        StringBuilder result = new StringBuilder();
        for (String component : location.getPathComponents()) {
            result.append("/loc[@name='").append(component).append("']");
        }
        return result.toString();
    }

    private String getFullXpathExpression(Container container) {
        StringBuilder result = new StringBuilder("fn:document('");
        result.append(this.crContainer.getPath());
        result.append("')/");
        result.append("cr");
        result.append(this.getXpathExpression(container));
        return result.toString();
    }

    private String getXpathExpression(Container container) {
        StringBuilder result = new StringBuilder(this.getXpathExpression(container.getLocation()));
        result.append("/con[@name='").append(container.getName()).append("']");
        return result.toString();
    }

    private class GetAttributesResultHandler
    implements XQueryResultHandler {
        private final List<String> names;

        public GetAttributesResultHandler(List<String> names) {
            this.names = names;
        }

        @Override
        public Map<String, String> transformXQueryResult(Object input) {
            Iterator iterator = (Iterator)input;
            Element element = (Element)((XhiveXQueryValueIf)iterator.next()).asNode();
            HashMap<String, String> result = new HashMap<String, String>();
            for (String name : this.names) {
                result.put(name, element.getAttribute(name));
            }
            return result;
        }
    }

    private class GetAttributeResultHandler
    implements XQueryResultHandler {
        @Override
        public String transformXQueryResult(Object input) {
            Iterator iterator = (Iterator)input;
            return ((XhiveXQueryValueIf)iterator.next()).asString();
        }
    }

    private class ListChildrenResultHandler
    implements XQueryResultHandler {
        private final Location location;

        public ListChildrenResultHandler(Location location) {
            this.location = location;
        }

        @Override
        public List<StoreChild> transformXQueryResult(Object input) {
            Iterator iterator = (Iterator)input;
            ArrayList<StoreChild> result = new ArrayList<StoreChild>();
            while (iterator.hasNext()) {
                Element element = (Element)((XhiveXQueryValueIf)iterator.next()).asNode();
                if ("con".equals(element.getTagName())) {
                    result.add(this.location.getChildContainer(element.getAttribute("name")));
                    continue;
                }
                if (!"loc".equals(element.getTagName())) continue;
                result.add(this.location.getChildLocation(element.getAttribute("name")));
            }
            return result;
        }
    }

    private class DeleteResultHandler
    implements XQueryResultHandler {
        @Override
        public Object transformXQueryResult(Object input) {
            Iterator iterator = (Iterator)input;
            XhiveNodeIf node = ((XhiveXQueryValueIf)iterator.next()).asNode();
            node.getParentNode().removeChild((Node)node);
            return null;
        }
    }

    private class MoveResultHandler
    implements XQueryResultHandler {
        private String targetName;
        final /* synthetic */ ESUContentRegistry this$0;

        public MoveResultHandler(ESUContentRegistry eSUContentRegistry) {
            this.this$0 = eSUContentRegistry;
        }

        public MoveResultHandler(ESUContentRegistry eSUContentRegistry, String targetName) {
            this.this$0 = eSUContentRegistry;
            this.targetName = targetName;
        }

        @Override
        public Object transformXQueryResult(Object input) {
            Iterator iterator = (Iterator)input;
            XhiveNodeIf sourceNode = ((XhiveXQueryValueIf)iterator.next()).asNode();
            XhiveNodeIf newLocationNode = ((XhiveXQueryValueIf)iterator.next()).asNode();
            sourceNode.getParentNode().removeChild((Node)sourceNode);
            if (this.targetName != null) {
                ((Element)sourceNode).setAttribute("name", this.targetName);
            }
            newLocationNode.appendChild((Node)sourceNode);
            return null;
        }
    }

    private class CreateContainerResultHandler
    implements XQueryResultHandler {
        private final Container container;
        private final Map<String, String> attributes;

        public CreateContainerResultHandler(Container container, Map<String, String> attributes) {
            this.container = container;
            this.attributes = attributes;
        }

        @Override
        public Object transformXQueryResult(Object input) {
            Element element;
            Iterator iterator = (Iterator)input;
            Iterator<String> pathComponents = this.container.getLocation().getPathComponents().iterator();
            Element lastNode = ((Document)((XhiveXQueryValueIf)iterator.next()).asNode()).getDocumentElement();
            while (iterator.hasNext()) {
                lastNode = (Element) ((XhiveXQueryValueIf)iterator.next()).asNode();
                if (!pathComponents.hasNext()) continue;
                pathComponents.next();
            }
            Document document = lastNode.getOwnerDocument();
            while (pathComponents.hasNext()) {
                element = document.createElement("loc");
                element.setAttribute("name", pathComponents.next());
                lastNode.appendChild(element);
                lastNode = element;
            }
            if ("con".equals(lastNode.getTagName())) {
                lastNode.getParentNode().removeChild(lastNode);
            }
            element = document.createElement("con");
            element.setAttribute("name", this.container.getName());
            if (this.attributes != null) {
                for (Map.Entry<String, String> attribute : this.attributes.entrySet()) {
                    element.setAttribute(attribute.getKey(), attribute.getValue());
                }
            }
            lastNode.appendChild(element);
            return null;
        }
    }

    private class CreateLocationResultHandler
    implements XQueryResultHandler {
        private final Location location;
        private final Map<String, String> attributes;
        private boolean typeConflict;

        public CreateLocationResultHandler(Location location, Map<String, String> attributes) {
            this.location = location;
            this.attributes = attributes;
        }

        @Override
        public Object transformXQueryResult(Object input) {
            Iterator iterator = (Iterator)input;
            Iterator<String> pathComponents = this.location.getPathComponents().iterator();
            Element lastNode = ((Document)((XhiveXQueryValueIf)iterator.next()).asNode()).getDocumentElement();
            while (iterator.hasNext()) {
                lastNode = (Element) ((XhiveXQueryValueIf)iterator.next()).asNode();
                if (lastNode.getNodeType() == 1 && "con".equals(lastNode.getTagName())) {
                    this.typeConflict = true;
                    return null;
                }
                pathComponents.next();
            }
            Document document = lastNode.getOwnerDocument();
            while (pathComponents.hasNext()) {
                Element element = document.createElement("loc");
                element.setAttribute("name", pathComponents.next());
                if (this.attributes != null) {
                    for (Map.Entry<String, String> attribute : this.attributes.entrySet()) {
                        element.setAttribute(attribute.getKey(), attribute.getValue());
                    }
                }
                lastNode.appendChild(element);
                lastNode = element;
            }
            return null;
        }

        private boolean isTypeConflict() {
            return this.typeConflict;
        }
    }

    private class ExistsResultHandler
    implements XQueryResultHandler {
        private ExistsResultHandler() {
        }

        @Override
        public Boolean transformXQueryResult(Object input) {
            Iterator iterator = (Iterator)input;
            return iterator.hasNext();
        }
    }

}

