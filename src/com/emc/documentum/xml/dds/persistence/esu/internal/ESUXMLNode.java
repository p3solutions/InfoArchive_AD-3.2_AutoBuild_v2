/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.persistence.esu.internal;

import com.emc.documentum.xml.dds.DDS;
import com.emc.documentum.xml.dds.exception.DDSException;
import com.emc.documentum.xml.dds.operation.exception.OperationException;
import com.emc.documentum.xml.dds.operation.exception.OperationFailedException;
import com.emc.documentum.xml.dds.operation.framework.Operation;
import com.emc.documentum.xml.dds.operation.library.persistence.RetrieveOperation;
import com.emc.documentum.xml.dds.persistence.Container;
import com.emc.documentum.xml.dds.persistence.ContentDescriptor;
import com.emc.documentum.xml.dds.persistence.Data;
import com.emc.documentum.xml.dds.persistence.Location;
import com.emc.documentum.xml.dds.persistence.Session;
import com.emc.documentum.xml.dds.persistence.Store;
import com.emc.documentum.xml.dds.persistence.StoreChild;
import com.emc.documentum.xml.dds.persistence.StoreType;
import com.emc.documentum.xml.dds.persistence.XMLNode;
import com.emc.documentum.xml.dds.persistence.content.XMLContentDescriptor;
import com.emc.documentum.xml.dds.persistence.data.InputStreamData;
import com.emc.documentum.xml.dds.persistence.esu.internal.ESUXMLUtil;
import com.emc.documentum.xml.dds.persistence.exception.AmbiguousXPointerException;
import com.emc.documentum.xml.dds.persistence.exception.ContainerNotFoundException;
import com.emc.documentum.xml.dds.persistence.exception.DeadlockException;
import com.emc.documentum.xml.dds.persistence.exception.IllegalActionException;
import com.emc.documentum.xml.dds.persistence.exception.LocationNotFoundException;
import com.emc.documentum.xml.dds.persistence.exception.NodeNotFoundException;
import com.emc.documentum.xml.dds.persistence.exception.StoreSpecificException;
import com.emc.documentum.xml.dds.persistence.internal.AbstractXMLNode;
import com.emc.documentum.xml.dds.persistence.internal.PersistenceUtil;
import com.emc.documentum.xml.dds.user.User;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.InputSource;

public class ESUXMLNode
extends AbstractXMLNode {
    private Store store;

    public ESUXMLNode(Location location) {
        super(location);
    }

    public ESUXMLNode(Container container) {
        super(container);
    }

    public ESUXMLNode(Container container, String xpointer) {
        super(container, xpointer);
    }

    protected ESUXMLNode(Location location, Container container, String xpointer, Node node) {
        super(location, container, xpointer, node);
    }

    @Override
    public Store getStore() {
        if (this.store != null) {
            return this.store;
        }
        if (!this.representsLocation()) {
            return super.getStore();
        }
        return this.asLocation().getStore();
    }

    @Override
    public StoreType getStoreType() {
        return StoreType.ESU;
    }

    @Override
    public String getPath() {
        Node node = this.asNode();
        if (node != null) {
            return null;
        }
        if (this.representsLocation()) {
            return this.asLocation().getPath();
        }
        Container container = this.getContainer();
        if (container != null) {
            String xpointer;
            return container.getPath() + ((xpointer = this.getXPointer()) == null ? "" : new StringBuilder().append("#").append(xpointer).toString());
        }
        return null;
    }

    @Override
    public XMLNode asXMLNode() {
        return this;
    }

    @Override
    public boolean exists(Session session) throws AmbiguousXPointerException, StoreSpecificException, DeadlockException {
        if (this.asNode() != null) {
            return true;
        }
        if (this.representsLocation()) {
            return this.asLocation().exists(session);
        }
        if (this.getContainer() != null && this.getXPointer() == null) {
            return this.getContainer().exists(session);
        }
        try {
            InputStream is = ((InputStreamData)DDS.getApplication().execute(DDS.getApplication().getApplicationUser(), new RetrieveOperation(this.getContainer(), new XMLContentDescriptor()))).content();
            return PersistenceUtil.existsXMLNode(new InputSource(is), this);
        }
        catch (OperationFailedException ofe) {
            return false;
        }
        catch (OperationException lnfe) {
            return false;
        }
    }

    @Override
    public void delete(Session session) throws NodeNotFoundException, AmbiguousXPointerException, StoreSpecificException, DeadlockException, IllegalActionException {
        if (this.representsLocation()) {
            try {
                this.asLocation().delete(session);
            }
            catch (LocationNotFoundException lnfe) {
                throw new NodeNotFoundException("Location represented by XMLNode does not exist : " + this.getPath());
            }
            return;
        }
        if (this.representsContainer()) {
            try {
                this.asContainer().delete(session);
            }
            catch (ContainerNotFoundException lnfe) {
                throw new NodeNotFoundException("Container represented by XMLNode does not exist : " + this.getPath());
            }
            return;
        }
        Node node = ESUXMLUtil.retrieveNode(this);
        try {
            node.getParentNode().removeChild(node);
            DOMImplementation domImpl = node.getOwnerDocument().getImplementation();
            DOMImplementationLS domImplLS = (DOMImplementationLS)domImpl.getFeature("LS", "3.0");
            LSSerializer serializer = domImplLS.createLSSerializer();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            LSOutput lsOutput = domImplLS.createLSOutput();
            lsOutput.setByteStream(outputStream);
            serializer.write(node.getOwnerDocument(), lsOutput);
            this.getContainer().persist(session, new XMLContentDescriptor(), new InputStreamData(new ByteArrayInputStream(outputStream.toByteArray())), true);
        }
        catch (DDSException de) {
            throw new StoreSpecificException(de);
        }
    }

    @Override
    public int getChildCount(Session session) throws NodeNotFoundException, AmbiguousXPointerException, DeadlockException, StoreSpecificException {
        if (this.representsLocation()) {
            try {
                return this.asLocation().listChildren(session, true, true, false).size();
            }
            catch (LocationNotFoundException lnfe) {
                throw new NodeNotFoundException("GetChildCount failed : Location represented by XMLNode does not exist : " + this.getPath());
            }
        }
        if (this.representsContainer() && !this.exists(session)) {
            throw new NodeNotFoundException("Container represented by XMLNode could not  be found : " + this.getPath());
        }
        Node node = ESUXMLUtil.retrieveNode(this);
        return node.getChildNodes().getLength();
    }

    @Override
    public List<XMLNode> getChildren(Session session) throws NodeNotFoundException, AmbiguousXPointerException, DeadlockException, StoreSpecificException {
        if (this.representsLocation()) {
            return this.getChildrenForLocation(session);
        }
        Node node = ESUXMLUtil.retrieveNode(this);
        if (node == null) {
            throw new NodeNotFoundException("Node not found : " + this.getPath() + "#xpointer(" + this.getXPointer() + ")");
        }
        ArrayList<XMLNode> children = new ArrayList<XMLNode>();
        NodeList list = node.getChildNodes();
        for (int counter = 0; counter < list.getLength(); ++counter) {
            children.add(new ESUXMLNode(this.asLocation(), this.getContainer(), "", list.item(counter)));
        }
        return children;
    }

    @Override
    public List<XMLNode> getChildren(Session session, short nodeType) throws NodeNotFoundException, AmbiguousXPointerException, DeadlockException, StoreSpecificException {
        if (this.representsLocation()) {
            return this.getChildrenForLocationByType(session, nodeType);
        }
        Node node = ESUXMLUtil.retrieveNode(this);
        if (node == null) {
            throw new NodeNotFoundException("Node not found : " + this.getPath() + "#xpointer(" + this.getXPointer() + ")");
        }
        ArrayList<XMLNode> children = new ArrayList<XMLNode>();
        NodeList list = node.getChildNodes();
        for (int counter = 0; counter < list.getLength(); ++counter) {
            Node child = list.item(counter);
            if (child.getNodeType() != nodeType) continue;
            children.add(new ESUXMLNode(this.asLocation(), this.getContainer(), "", child));
        }
        return children;
    }

    @Override
    public List<XMLNode> getChildrenRange(Session session, int first, int last) throws NodeNotFoundException, AmbiguousXPointerException, DeadlockException, StoreSpecificException {
        int counter;
        if (this.representsLocation()) {
            return this.getChildrenForLocationByRange(session, first, last);
        }
        Node node = ESUXMLUtil.retrieveNode(this);
        if (node == null) {
            throw new NodeNotFoundException("Node not found : " + this.getPath() + "#xpointer(" + this.getXPointer() + ")");
        }
        ArrayList<XMLNode> children = new ArrayList<XMLNode>();
        NodeList list = node.getChildNodes();
        if (first >= list.getLength()) {
            return children;
        }
        int end = last > list.getLength() ? list.getLength() : last;
        int n = counter = first >= 0 ? first : 0;
        while (counter < end) {
            children.add(new ESUXMLNode(this.asLocation(), this.getContainer(), "", list.item(counter)));
            ++counter;
        }
        return children;
    }

    @Override
    public XMLNode getNode(Session session) throws NodeNotFoundException, AmbiguousXPointerException, DeadlockException, StoreSpecificException {
        if (this.representsLocation()) {
            return this;
        }
        return new ESUXMLNode(this.asLocation(), this.getContainer(), this.getXPointer(), ESUXMLUtil.retrieveNode(this));
    }

    @Override
    public XMLNode insert(Session session, String xml, boolean before) throws NodeNotFoundException, AmbiguousXPointerException, DeadlockException, StoreSpecificException {
        if (this.representsLocation() || this.representsContainer()) {
            throw new StoreSpecificException("This action is not supported for XMLNodes representing Locations or Containers.");
        }
        Node node = ESUXMLUtil.retrieveNode(this);
        if (node == null) {
            throw new NodeNotFoundException("Node not found : " + this.getPath() + "#xpointer(" + this.getXPointer() + ")");
        }
        return new ESUXMLNode(this.asLocation(), this.getContainer(), "", this.insertXMLFragment(session, node, xml, before));
    }

    @Override
    public XMLNode move(Session session, XMLNode target, boolean before) throws NodeNotFoundException, AmbiguousXPointerException, DeadlockException, StoreSpecificException {
        if (this.representsLocation() || this.representsContainer() || target.representsLocation() || target.representsContainer()) {
            throw new StoreSpecificException("This action is not supported for XMLNodes representing Locations or Containers.");
        }
        Node sourceNode = ESUXMLUtil.retrieveNode(this);
        Node resultNode = this.moveFrom(session, sourceNode);
        Node targetNode = ESUXMLUtil.retrieveNode(target);
        return new ESUXMLNode(this.asLocation(), this.getContainer(), "", this.moveTo(session, resultNode, target, targetNode, before));
    }

    @Override
    public XMLNode copy(Session session, XMLNode target, boolean before) throws NodeNotFoundException, AmbiguousXPointerException, DeadlockException, StoreSpecificException {
        if (this.representsLocation() || this.representsContainer() || target.representsLocation() || target.representsContainer()) {
            throw new StoreSpecificException("This action is not supported for XMLNodes representing Locations or Containers.");
        }
        Node sourceNode = ESUXMLUtil.retrieveNode(this);
        Node targetNode = ESUXMLUtil.retrieveNode(target);
        return new ESUXMLNode(this.asLocation(), this.getContainer(), "", this.copy(session, sourceNode, target, targetNode, before));
    }

    @Override
    public void setAttribute(Session session, String name, String value) throws NodeNotFoundException, AmbiguousXPointerException, DeadlockException, StoreSpecificException {
        if (this.representsLocation() || this.representsContainer()) {
            throw new StoreSpecificException("This action is not supported for XMLNodes representing Locations or Containers.");
        }
        Node node = ESUXMLUtil.retrieveNode(this);
        this.setAttribute(session, node, name, value);
    }

    @Override
    public void setAttributes(Session session, Map<String, String> attributes) throws NodeNotFoundException, AmbiguousXPointerException, DeadlockException, StoreSpecificException {
        if (this.representsLocation() || this.representsContainer()) {
            throw new StoreSpecificException("This action is not supported for XMLNodes representing Locations or Containers.");
        }
        Node node = ESUXMLUtil.retrieveNode(this);
        this.setAttributes(session, node, attributes);
    }
}

