/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  com.xhive.core.interfaces.XhiveSessionIf
 *  com.xhive.dom.interfaces.XhiveDocumentIf
 *  com.xhive.dom.interfaces.XhiveLSParserIf
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
import com.emc.documentum.xml.dds.persistence.Session;
import com.emc.documentum.xml.dds.persistence.StoreType;
import com.emc.documentum.xml.dds.persistence.XMLNode;
import com.emc.documentum.xml.dds.persistence.exception.AmbiguousXPointerException;
import com.emc.documentum.xml.dds.persistence.exception.ContainerNotFoundException;
import com.emc.documentum.xml.dds.persistence.exception.DeadlockException;
import com.emc.documentum.xml.dds.persistence.exception.IllegalActionException;
import com.emc.documentum.xml.dds.persistence.exception.LocationNotFoundException;
import com.emc.documentum.xml.dds.persistence.exception.NodeNotFoundException;
import com.emc.documentum.xml.dds.persistence.exception.StoreSpecificException;
import com.emc.documentum.xml.dds.persistence.internal.AbstractXMLNode;
import com.emc.documentum.xml.dds.persistence.xdb.internal.XDBContainer;
import com.emc.documentum.xml.dds.persistence.xdb.internal.XDBXMLUtil;
import com.xhive.core.interfaces.XhiveSessionIf;
import com.xhive.dom.interfaces.XhiveDocumentIf;
import com.xhive.dom.interfaces.XhiveLSParserIf;
import com.xhive.dom.interfaces.XhiveLibraryChildIf;
import com.xhive.dom.interfaces.XhiveLibraryIf;
import com.xhive.dom.interfaces.XhiveNodeIf;
import com.xhive.error.XhiveDeadlockException;
import com.xhive.error.XhiveException;
import com.xhive.util.interfaces.IterableIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.ls.LSInput;

public class XDBXMLNode
extends AbstractXMLNode {
    protected XDBXMLNode(Location location) {
        super(location);
    }

    protected XDBXMLNode(Container container) {
        super(container);
    }

    protected XDBXMLNode(Container container, String xpointer) {
        super(container, xpointer);
    }

    protected XDBXMLNode(Location location, Container container, String xpointer, Node node) {
        super(location, container, xpointer, node);
    }

    @Override
    public StoreType getStoreType() {
        return StoreType.XDB;
    }

    @Override
    public XMLNode asXMLNode() {
        return this;
    }

    @Override
    public String getPath() {
        if (this.asNode() != null) {
            return XDBXMLUtil.generatePath(this.asNode());
        }
        return super.getPath();
    }

    @Override
    public String getCanonicalPath() {
        if (this.asNode() != null) {
            return XDBXMLUtil.generatePath(this.asNode());
        }
        return super.getPath();
    }

    @Override
    public boolean exists(Session session) throws AmbiguousXPointerException, StoreSpecificException, DeadlockException {
        if (this.asNode() != null) {
            return true;
        }
        if (this.representsLocation()) {
            return this.asLocation().exists(session);
        }
        if (this.representsContainer()) {
            return this.asContainer().exists(session);
        }
        return XDBXMLUtil.existsNode((XhiveSessionIf)session.getSession(), (XDBContainer)this.getContainer(), this.getXPointer());
    }

    @Override
    public void delete(Session session) throws NodeNotFoundException, AmbiguousXPointerException, StoreSpecificException, DeadlockException, IllegalActionException {
        if (this.representsLocation()) {
            try {
                this.asLocation().delete(session);
                return;
            }
            catch (LocationNotFoundException lnfe) {
                throw new NodeNotFoundException("Delete failed : Library represented by XMLNode not found : " + this.getPath());
            }
        }
        if (this.getContainer() != null && this.getXPointer() == null) {
            try {
                this.getContainer().delete(session);
                return;
            }
            catch (ContainerNotFoundException cnfe) {
                throw new NodeNotFoundException("Delete failed : Document represented by XMLNode not found : " + this.getPath());
            }
        }
        XhiveNodeIf node = XDBXMLUtil.retrieveNode((XhiveSessionIf)session.getSession(), this);
        node.getParentNode().removeChild((Node)node);
    }

    @Override
    public int getChildCount(Session session) throws NodeNotFoundException, AmbiguousXPointerException, DeadlockException, StoreSpecificException {
        try {
            XhiveNodeIf node = XDBXMLUtil.retrieveNode((XhiveSessionIf)session.getSession(), this);
            IterableIterator children = node.getChildren();
            int counter = 0;
            while (children.hasNext()) {
                children.next();
                ++counter;
            }
            return counter;
        }
        catch (XhiveDeadlockException xde) {
            throw new DeadlockException((Throwable)xde);
        }
        catch (XhiveException xe) {
            throw new StoreSpecificException((Throwable)xe);
        }
    }

    @Override
    public List<XMLNode> getChildren(Session session) throws NodeNotFoundException, AmbiguousXPointerException, DeadlockException, StoreSpecificException {
        try {
            XhiveNodeIf node = XDBXMLUtil.retrieveNode((XhiveSessionIf)session.getSession(), this);
            ArrayList<XMLNode> result = new ArrayList<XMLNode>();
            IterableIterator children = node.getChildren();
            while (children.hasNext()) {
                XhiveNodeIf child = (XhiveNodeIf)children.next();
                if (child instanceof XhiveLibraryIf) {
                    result.add(new XDBXMLNode(this.asLocation().getChildLocation(((XhiveLibraryIf)child).getName()), null, null, (Node)child));
                    continue;
                }
                if (child instanceof XhiveLibraryChildIf) {
                    result.add(new XDBXMLNode(null, this.asLocation().getChildContainer(((XhiveLibraryChildIf)child).getName()), null, (Node)child));
                    continue;
                }
                result.add(new XDBXMLNode(null, this.getContainer(), null, (Node)child));
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

    @Override
    public List<XMLNode> getChildren(Session session, short nodeType) throws NodeNotFoundException, AmbiguousXPointerException, DeadlockException, StoreSpecificException {
        try {
            XhiveNodeIf node = XDBXMLUtil.retrieveNode((XhiveSessionIf)session.getSession(), this);
            ArrayList<XMLNode> result = new ArrayList<XMLNode>();
            if (node == null) {
                return result;
            }
            IterableIterator children = node.getChildren();
            while (children.hasNext()) {
                Node child = (Node)children.next();
                if (nodeType == 203 || nodeType == 9) {
                    if (child.getNodeType() != 9 && child.getNodeType() != 203) continue;
                    result.add(new XDBXMLNode(null, this.asLocation().getChildContainer(((XhiveLibraryChildIf)child).getName()), null, child));
                    continue;
                }
                if (child.getNodeType() != nodeType) continue;
                if (child instanceof XhiveLibraryIf) {
                    result.add(new XDBXMLNode(this.asLocation().getChildLocation(((XhiveLibraryIf)child).getName()), null, null, child));
                    continue;
                }
                result.add(new XDBXMLNode(null, this.getContainer(), null, child));
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

    @Override
    public List<XMLNode> getChildrenRange(Session session, int first, int last) throws NodeNotFoundException, AmbiguousXPointerException, DeadlockException, StoreSpecificException {
        try {
            XhiveNodeIf node = XDBXMLUtil.retrieveNode((XhiveSessionIf)session.getSession(), this);
            ArrayList<XMLNode> result = new ArrayList<XMLNode>();
            IterableIterator children = node.getChildren();
            int counter = 0;
            while (children.hasNext()) {
                Node child = (Node)children.next();
                if (counter >= first && counter < last) {
                    if (child instanceof XhiveLibraryIf) {
                        result.add(new XDBXMLNode(this.asLocation().getChildLocation(((XhiveLibraryIf)child).getName()), null, null, child));
                    } else if (child instanceof XhiveLibraryChildIf) {
                        result.add(new XDBXMLNode(null, this.asLocation().getChildContainer(((XhiveLibraryChildIf)child).getName()), null, child));
                    } else {
                        result.add(new XDBXMLNode(null, this.getContainer(), null, child));
                    }
                }
                ++counter;
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

    @Override
    public XMLNode getNode(Session session) throws NodeNotFoundException, AmbiguousXPointerException, DeadlockException, StoreSpecificException {
        return new XDBXMLNode(this.asLocation(), this.getContainer(), this.getXPointer(), (Node)XDBXMLUtil.retrieveNode((XhiveSessionIf)session.getSession(), this));
    }

    @Override
    public XMLNode insert(Session session, String xml, boolean before) throws NodeNotFoundException, AmbiguousXPointerException, DeadlockException, StoreSpecificException {
        if (this.representsLocation() || this.representsContainer()) {
            throw new StoreSpecificException("This action is not supported for XMLNodes representing Locations or Containers.");
        }
        try {
            XhiveNodeIf node = XDBXMLUtil.retrieveNode((XhiveSessionIf)session.getSession(), this);
            XhiveLibraryIf ownerLibrary = node instanceof Document ? ((XhiveDocumentIf)node).getOwnerLibrary() : node.getOwnerDocument().getOwnerLibrary();
            XhiveLSParserIf parser = ownerLibrary.createLSParser();
            parser.getDomConfig().setParameter("namespaces", Boolean.TRUE);
            LSInput lsInput = ownerLibrary.createLSInput();
            lsInput.setStringData(xml);
            lsInput.setSystemId("");
            return new XDBXMLNode(this.asLocation(), this.getContainer(), null, parser.parseWithContext(lsInput, (Node)node, (short) (before ? 3 : 1)));
        }
        catch (XhiveDeadlockException xde) {
            throw new DeadlockException((Throwable)xde);
        }
        catch (XhiveException xe) {
            throw new StoreSpecificException((Throwable)xe);
        }
    }

    @Override
    public XMLNode move(Session session, XMLNode target, boolean before) throws NodeNotFoundException, AmbiguousXPointerException, DeadlockException, StoreSpecificException {
        if (this.representsLocation() || this.representsContainer() || target.representsLocation() || target.representsContainer()) {
            throw new StoreSpecificException("This action is not supported for XMLNodes representing Locations or Containers.");
        }
        try {
            XhiveNodeIf sourceNode = XDBXMLUtil.retrieveNode((XhiveSessionIf)session.getSession(), this);
            XhiveNodeIf targetNode = XDBXMLUtil.retrieveNode((XhiveSessionIf)session.getSession(), target);
            Document targetDocument = targetNode.getOwnerDocument();
            Node resultNode = targetDocument.adoptNode((Node)sourceNode);
            try {
                if (before) {
                    Node parentNode = targetNode.getParentNode();
                    parentNode.insertBefore(resultNode, (Node)targetNode);
                } else {
                    targetNode.appendChild(resultNode);
                }
            }
            catch (NullPointerException npe) {
                resultNode = targetDocument.importNode((Node)sourceNode, true);
                if (before) {
                    Node parentNode = targetNode.getParentNode();
                    parentNode.insertBefore(resultNode, (Node)targetNode);
                } else {
                    targetNode.appendChild(resultNode);
                }
                sourceNode.getParentNode().removeChild((Node)sourceNode);
            }
            return new XDBXMLNode(this.asLocation(), this.getContainer(), null, resultNode);
        }
        catch (XhiveDeadlockException xde) {
            throw new DeadlockException((Throwable)xde);
        }
        catch (XhiveException xe) {
            throw new StoreSpecificException((Throwable)xe);
        }
    }

    @Override
    public XMLNode copy(Session session, XMLNode target, boolean before) throws NodeNotFoundException, AmbiguousXPointerException, DeadlockException, StoreSpecificException {
        if (this.representsLocation() || this.representsContainer() || target.representsLocation() || target.representsContainer()) {
            throw new StoreSpecificException("This action is not supported for XMLNodes representing Locations or Containers.");
        }
        try {
            XhiveNodeIf sourceNode = XDBXMLUtil.retrieveNode((XhiveSessionIf)session.getSession(), this);
            XhiveNodeIf targetNode = XDBXMLUtil.retrieveNode((XhiveSessionIf)session.getSession(), target);
            Document targetDocument = targetNode.getOwnerDocument();
            Node resultNode = targetDocument.importNode((Node)sourceNode, true);
            if (before) {
                Node parentNode = targetNode.getParentNode();
                parentNode.insertBefore(resultNode, (Node)targetNode);
            } else {
                targetNode.appendChild(resultNode);
            }
            return new XDBXMLNode(this.asLocation(), this.getContainer(), null, resultNode);
        }
        catch (XhiveDeadlockException xde) {
            throw new DeadlockException((Throwable)xde);
        }
        catch (XhiveException xe) {
            throw new StoreSpecificException((Throwable)xe);
        }
    }

    @Override
    public void setAttribute(Session session, String name, String value) throws NodeNotFoundException, AmbiguousXPointerException, DeadlockException, StoreSpecificException {
        if (this.representsLocation() || this.representsContainer()) {
            throw new StoreSpecificException("This action is not supported for XMLNodes representing Locations or Containers.");
        }
        try {
            XhiveNodeIf node = XDBXMLUtil.retrieveNode((XhiveSessionIf)session.getSession(), this);
            if (!(node instanceof Element)) {
                throw new DOMException((short) 12, "Attributes can only be set on an Element");
            }
            ((Element)node).setAttribute(name, value);
        }
        catch (XhiveDeadlockException xde) {
            throw new DeadlockException((Throwable)xde);
        }
        catch (XhiveException xe) {
            throw new StoreSpecificException((Throwable)xe);
        }
    }

    @Override
    public void setAttributes(Session session, Map<String, String> attributes) throws NodeNotFoundException, AmbiguousXPointerException, DeadlockException, StoreSpecificException {
        if (this.representsLocation() || this.representsContainer()) {
            throw new StoreSpecificException("This action is not supported for XMLNodes representing Locations or Containers.");
        }
        try {
            XhiveNodeIf node = XDBXMLUtil.retrieveNode((XhiveSessionIf)session.getSession(), this);
            if (!(node instanceof Element)) {
                throw new DOMException((short) 12, "Attributes can only be set on an Element");
            }
            for (Map.Entry<String, String> attribute : attributes.entrySet()) {
                ((Element)node).setAttribute(attribute.getKey(), attribute.getValue());
            }
        }
        catch (XhiveDeadlockException xde) {
            throw new DeadlockException((Throwable)xde);
        }
        catch (XhiveException xe) {
            throw new StoreSpecificException((Throwable)xe);
        }
    }
}

