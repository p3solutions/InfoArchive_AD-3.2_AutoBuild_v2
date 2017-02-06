/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.persistence.internal;

import com.emc.documentum.xml.dds.persistence.Container;
import com.emc.documentum.xml.dds.persistence.ContentDescriptor;
import com.emc.documentum.xml.dds.persistence.Data;
import com.emc.documentum.xml.dds.persistence.Location;
import com.emc.documentum.xml.dds.persistence.Session;
import com.emc.documentum.xml.dds.persistence.Store;
import com.emc.documentum.xml.dds.persistence.StoreChild;
import com.emc.documentum.xml.dds.persistence.XMLNode;
import com.emc.documentum.xml.dds.persistence.content.XMLContentDescriptor;
import com.emc.documentum.xml.dds.persistence.data.InputStreamData;
import com.emc.documentum.xml.dds.persistence.exception.ContainerAlreadyExistsException;
import com.emc.documentum.xml.dds.persistence.exception.DeadlockException;
import com.emc.documentum.xml.dds.persistence.exception.LocationNotFoundException;
import com.emc.documentum.xml.dds.persistence.exception.NodeNotFoundException;
import com.emc.documentum.xml.dds.persistence.exception.StoreChildNotFoundException;
import com.emc.documentum.xml.dds.persistence.exception.StoreSpecificException;
import com.emc.documentum.xml.dds.persistence.exception.TypeConflictException;
import com.emc.documentum.xml.dds.persistence.internal.AbstractStoreChild;
import com.emc.documentum.xml.dds.serialization.exception.SerializationException;
import com.emc.documentum.xml.dds.structure.Structure;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSParser;
import org.w3c.dom.ls.LSSerializer;

public abstract class AbstractXMLNode
extends AbstractStoreChild
implements XMLNode {
    private Location location;
    private Container container;
    private String xpointer;
    private Node node;

    protected AbstractXMLNode(Location location) {
        this.location = location;
    }

    protected AbstractXMLNode(Container container) {
        this.container = container;
    }

    protected AbstractXMLNode(Container container, String xpointer) {
        this.container = container;
        this.xpointer = xpointer;
    }

    protected AbstractXMLNode(Location location, Container container, String xpointer, Node node) {
        this.location = location;
        this.container = container;
        this.xpointer = xpointer;
        this.node = node;
    }

    @Override
    public Container getContainer() {
        return this.container;
    }

    @Override
    public String getXPointer() {
        return this.xpointer;
    }

    @Override
    public Node asNode() {
        return this.node;
    }

    @Override
    public String getPath() {
        if (this.location == null && this.container == null && this.xpointer == null && this.node == null) {
            return null;
        }
        if (this.asNode() != null) {
            if (this.container != null) {
                if (this.xpointer != null) {
                    return this.container.getPath() + "#" + this.xpointer;
                }
                return this.container.getPath();
            }
            if (this.location != null) {
                return this.location.getPath();
            }
            return null;
        }
        if (this.representsLocation()) {
            return this.asLocation().getPath();
        }
        if (this.representsContainer()) {
            return this.container.getPath();
        }
        return this.getContainer().getPath() + (this.getXPointer() == null ? "" : new StringBuilder().append("#").append(this.getXPointer()).toString());
    }

    @Override
    public String getCanonicalPath() {
        if (this.representsLocation()) {
            return this.asLocation().getCanonicalPath();
        }
        if (this.representsContainer()) {
            return this.container.getCanonicalPath();
        }
        return this.getContainer().getCanonicalPath() + (this.getXPointer() == null ? "" : new StringBuilder().append("#").append(this.getXPointer()).toString());
    }

    @Override
    public String getName() {
        return this.node == null ? null : this.node.getNodeName();
    }

    @Override
    public Store getStore() {
        if (this.location != null) {
            return this.location.getStore();
        }
        return this.container.getStore();
    }

    public String getStoreAlias() {
        Store store = this.getStore();
        return store == null ? null : store.getAlias();
    }

    @Override
    public boolean isLocation() {
        return false;
    }

    @Override
    public boolean isContainer() {
        return false;
    }

    @Override
    public boolean isXMLNode() {
        return true;
    }

    @Override
    public boolean representsLocation() {
        return this.location != null;
    }

    @Override
    public boolean representsContainer() {
        return this.container != null && this.xpointer == null;
    }

    @Override
    public boolean representsNode() {
        return this.container != null && this.xpointer != null || this.node != null;
    }

    @Override
    public Location asLocation() {
        return this.location;
    }

    @Override
    public Container asContainer() {
        return this.container;
    }

    @Override
    public Structure getContext() {
        if (this.location != null) {
            return this.location.getContext();
        }
        if (this.container != null) {
            return this.container.getContext();
        }
        return null;
    }

    protected List<XMLNode> getChildrenForLocation(Session session) throws NodeNotFoundException, DeadlockException, StoreSpecificException {
        try {
            ArrayList<XMLNode> result = new ArrayList<XMLNode>();
            for (StoreChild child : this.asLocation().listChildren(session, true, true, false)) {
                result.add(child.asXMLNode());
            }
            return result;
        }
        catch (LocationNotFoundException lnfe) {
            throw new NodeNotFoundException("GetChildren failed : Location represented by XMLNode does not exist : " + this.getPath());
        }
    }

    protected List<XMLNode> getChildrenForLocationByType(Session session, short nodeType) throws NodeNotFoundException, DeadlockException, StoreSpecificException {
        try {
            ArrayList<XMLNode> result = new ArrayList<XMLNode>();
            for (StoreChild child : this.asLocation().listChildren(session, nodeType == 201, nodeType == 9 || nodeType == 203, false)) {
                result.add(child.asXMLNode());
            }
            return result;
        }
        catch (LocationNotFoundException lnfe) {
            throw new NodeNotFoundException("GetChildren failed : Location represented by XMLNode does not exist : " + this.getPath());
        }
    }

    protected List<XMLNode> getChildrenForLocationByRange(Session session, int first, int last) throws NodeNotFoundException, DeadlockException, StoreSpecificException {
        try {
            int counter;
            ArrayList<StoreChild> children = new ArrayList<StoreChild>();
            children.addAll(this.asLocation().listChildren(session, true, true, false));
            ArrayList<XMLNode> result = new ArrayList<XMLNode>();
            if (first >= children.size()) {
                return result;
            }
            int end = last > children.size() ? children.size() : last;
            int n = counter = first >= 0 ? first : 0;
            while (counter < end) {
                result.add(((StoreChild)children.get(counter)).asXMLNode());
                ++counter;
            }
            return result;
        }
        catch (LocationNotFoundException lnfe) {
            throw new NodeNotFoundException("GetChildrenRange failed : Location represented by XMLNode does not exist : " + this.getPath());
        }
    }

    protected Node insertXMLFragment(Session session, Node domNode, String xml, boolean before) throws DeadlockException, StoreSpecificException {
        DOMImplementation domImpl = domNode.getOwnerDocument().getImplementation();
        DOMImplementationLS domImplLS = (DOMImplementationLS)domImpl.getFeature("LS", "3.0");
        LSParser parser = domImplLS.createLSParser((short) 1, null);
        parser.getDomConfig().setParameter("namespaces", Boolean.TRUE);
        LSInput lsInput = domImplLS.createLSInput();
        lsInput.setStringData(xml);
        lsInput.setSystemId("");
        Node newNode = parser.parse(lsInput);
        Document document = domNode.getOwnerDocument();
        if (newNode.getNodeType() == 9) {
            newNode = ((Document) newNode).getDocumentElement();
        }
        newNode = document.importNode(newNode, true);
        if (before) {
            domNode.getParentNode().insertBefore(newNode, domNode);
        } else {
            domNode.appendChild(newNode);
        }
        LSSerializer serializer = domImplLS.createLSSerializer();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        LSOutput lsOutput = domImplLS.createLSOutput();
        lsOutput.setByteStream(outputStream);
        serializer.write(domNode.getOwnerDocument(), lsOutput);
        try {
            this.getContainer().persist(session, new XMLContentDescriptor(), new InputStreamData(new ByteArrayInputStream(outputStream.toByteArray())), true);
        }
        catch (TypeConflictException tce) {
            throw new StoreSpecificException("Internal error.");
        }
        catch (ContainerAlreadyExistsException caee) {
            throw new StoreSpecificException("Internal error.");
        }
        catch (StoreChildNotFoundException caee) {
            throw new StoreSpecificException("Internal error.");
        }
        catch (SerializationException se) {
            throw new StoreSpecificException("Internal error.");
        }
        return newNode;
    }

    protected Node moveFrom(Session session, Node sourceNode) throws DeadlockException, StoreSpecificException {
        Node resultNode = sourceNode.cloneNode(true);
        sourceNode.getParentNode().removeChild(sourceNode);
        DOMImplementation domImpl = sourceNode.getOwnerDocument().getImplementation();
        DOMImplementationLS domImplLS = (DOMImplementationLS)domImpl.getFeature("LS", "3.0");
        LSSerializer serializer = domImplLS.createLSSerializer();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        LSOutput lsOutput = domImplLS.createLSOutput();
        lsOutput.setByteStream(outputStream);
        serializer.write(sourceNode.getOwnerDocument(), lsOutput);
        try {
            this.getContainer().persist(session, new XMLContentDescriptor(), new InputStreamData(new ByteArrayInputStream(outputStream.toByteArray())), true);
        }
        catch (TypeConflictException tce) {
            throw new StoreSpecificException("Internal error.");
        }
        catch (ContainerAlreadyExistsException caee) {
            throw new StoreSpecificException("Internal error.");
        }
        catch (StoreChildNotFoundException scnfe) {
            throw new StoreSpecificException("Internal error.");
        }
        catch (SerializationException se) {
            throw new StoreSpecificException("Internal error.");
        }
        return resultNode;
    }

    protected Node moveTo(Session session, Node importNode, XMLNode target, Node targetNode, boolean before) throws DeadlockException, StoreSpecificException {
        Document targetDocument = targetNode.getOwnerDocument();
        Node importedNode = targetDocument.importNode(importNode, true);
        DOMImplementation domImpl = targetNode.getOwnerDocument().getImplementation();
        DOMImplementationLS domImplLS = (DOMImplementationLS)domImpl.getFeature("LS", "3.0");
        LSSerializer serializer = domImplLS.createLSSerializer();
        if (before) {
            Node parentNode = targetNode.getParentNode();
            parentNode.insertBefore(importedNode, targetNode);
        } else {
            targetNode.appendChild(importedNode);
        }
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        LSOutput lsOutput = domImplLS.createLSOutput();
        lsOutput.setByteStream(outputStream);
        serializer.write(targetNode.getOwnerDocument(), lsOutput);
        try {
            target.getContainer().persist(session, new XMLContentDescriptor(), new InputStreamData(new ByteArrayInputStream(outputStream.toByteArray())), true);
        }
        catch (TypeConflictException tce) {
            throw new StoreSpecificException("Internal error.");
        }
        catch (ContainerAlreadyExistsException caee) {
            throw new StoreSpecificException("Internal error.");
        }
        catch (StoreChildNotFoundException scnfe) {
            throw new StoreSpecificException("Internal error.");
        }
        catch (SerializationException se) {
            throw new StoreSpecificException("Internal error.");
        }
        return importedNode;
    }

    protected Node copy(Session session, Node sourceNode, XMLNode target, Node targetNode, boolean before) throws DeadlockException, StoreSpecificException {
        Document targetDocument = targetNode.getOwnerDocument();
        Node resultNode = targetDocument.importNode(sourceNode, true);
        if (before) {
            Node parentNode = targetNode.getParentNode();
            parentNode.insertBefore(resultNode, targetNode);
        } else {
            targetNode.appendChild(resultNode);
        }
        DOMImplementation domImpl = targetNode.getOwnerDocument().getImplementation();
        DOMImplementationLS domImplLS = (DOMImplementationLS)domImpl.getFeature("LS", "3.0");
        LSSerializer serializer = domImplLS.createLSSerializer();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        LSOutput lsOutput = domImplLS.createLSOutput();
        lsOutput.setByteStream(outputStream);
        serializer.write(targetNode.getOwnerDocument(), lsOutput);
        try {
            target.getContainer().persist(session, new XMLContentDescriptor(), new InputStreamData(new ByteArrayInputStream(outputStream.toByteArray())), true);
        }
        catch (TypeConflictException tce) {
            throw new StoreSpecificException("Internal error.");
        }
        catch (ContainerAlreadyExistsException caee) {
            throw new StoreSpecificException("Internal error.");
        }
        catch (StoreChildNotFoundException scnfe) {
            throw new StoreSpecificException("Internal error.");
        }
        catch (SerializationException se) {
            throw new StoreSpecificException("Internal error.");
        }
        return resultNode;
    }

    protected void setAttribute(Session session, Node aNode, String name, String value) throws DeadlockException, StoreSpecificException {
        if (!(aNode instanceof Element)) {
            throw new DOMException((short) 12, "Attributes can only be set on an Element");
        }
        ((Element)aNode).setAttribute(name, value);
        DOMImplementation domImpl = aNode.getOwnerDocument().getImplementation();
        DOMImplementationLS domImplLS = (DOMImplementationLS)domImpl.getFeature("LS", "3.0");
        LSSerializer serializer = domImplLS.createLSSerializer();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        LSOutput lsOutput = domImplLS.createLSOutput();
        lsOutput.setByteStream(outputStream);
        serializer.write(aNode.getOwnerDocument(), lsOutput);
        try {
            this.getContainer().persist(session, new XMLContentDescriptor(), new InputStreamData(new ByteArrayInputStream(outputStream.toByteArray())), true);
        }
        catch (TypeConflictException tce) {
            throw new StoreSpecificException("Internal error.");
        }
        catch (ContainerAlreadyExistsException caee) {
            throw new StoreSpecificException("Internal error.");
        }
        catch (StoreChildNotFoundException scnfe) {
            throw new StoreSpecificException("Internal error.");
        }
        catch (SerializationException se) {
            throw new StoreSpecificException("Internal error.");
        }
    }

    protected void setAttributes(Session session, Node aNode, Map<String, String> attributes) throws DeadlockException, StoreSpecificException {
        if (!(aNode instanceof Element)) {
            throw new DOMException((short) 12, "Attributes can only be set on an Element");
        }
        for (Map.Entry<String, String> attribute : attributes.entrySet()) {
            ((Element)aNode).setAttribute(attribute.getKey(), attribute.getValue());
        }
        DOMImplementation domImpl = aNode.getOwnerDocument().getImplementation();
        DOMImplementationLS domImplLS = (DOMImplementationLS)domImpl.getFeature("LS", "3.0");
        LSSerializer serializer = domImplLS.createLSSerializer();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        LSOutput lsOutput = domImplLS.createLSOutput();
        lsOutput.setByteStream(outputStream);
        serializer.write(aNode.getOwnerDocument(), lsOutput);
        try {
            this.getContainer().persist(session, new XMLContentDescriptor(), new InputStreamData(new ByteArrayInputStream(outputStream.toByteArray())), true);
        }
        catch (TypeConflictException tce) {
            throw new StoreSpecificException("Internal error.");
        }
        catch (ContainerAlreadyExistsException caee) {
            throw new StoreSpecificException("Internal error.");
        }
        catch (StoreChildNotFoundException scnfe) {
            throw new StoreSpecificException("Internal error.");
        }
        catch (SerializationException se) {
            throw new StoreSpecificException("Internal error.");
        }
    }
}

