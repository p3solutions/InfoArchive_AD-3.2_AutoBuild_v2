/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  com.xhive.core.interfaces.XhiveDriverIf
 *  com.xhive.core.interfaces.XhiveLocationIteratorIf
 *  com.xhive.core.interfaces.XhiveSessionIf
 *  com.xhive.dom.interfaces.XhiveBlobNodeIf
 *  com.xhive.dom.interfaces.XhiveLibraryChildIf
 *  com.xhive.dom.interfaces.XhiveLibraryIf
 *  com.xhive.dom.interfaces.XhiveLocationIf
 *  com.xhive.dom.interfaces.XhiveNodeIf
 *  com.xhive.error.XhiveException
 *  com.xhive.query.interfaces.XhiveQueryResultIf
 */
package com.emc.documentum.xml.dds.persistence.xdb.internal;

import com.emc.documentum.xml.dds.application.Application;
import com.emc.documentum.xml.dds.application.StoreManager;
import com.emc.documentum.xml.dds.persistence.Container;
import com.emc.documentum.xml.dds.persistence.Location;
import com.emc.documentum.xml.dds.persistence.Store;
import com.emc.documentum.xml.dds.persistence.StoreType;
import com.emc.documentum.xml.dds.persistence.XMLNode;
import com.emc.documentum.xml.dds.persistence.exception.AmbiguousXPointerException;
import com.emc.documentum.xml.dds.persistence.exception.ContainerNotFoundException;
import com.emc.documentum.xml.dds.persistence.exception.DeadlockException;
import com.emc.documentum.xml.dds.persistence.exception.InvalidContentException;
import com.emc.documentum.xml.dds.persistence.exception.LocationAlreadyExistsException;
import com.emc.documentum.xml.dds.persistence.exception.LocationNotFoundException;
import com.emc.documentum.xml.dds.persistence.exception.NodeNotFoundException;
import com.emc.documentum.xml.dds.persistence.exception.StoreSpecificException;
import com.emc.documentum.xml.dds.persistence.exception.TypeConflictException;
import com.emc.documentum.xml.dds.persistence.xdb.internal.XDBContainer;
import com.emc.documentum.xml.dds.persistence.xdb.internal.XDBContainerUtil;
import com.emc.documentum.xml.dds.persistence.xdb.internal.XDBLocation;
import com.emc.documentum.xml.dds.persistence.xdb.internal.XDBLocationUtil;
import com.emc.documentum.xml.dds.persistence.xdb.internal.XDBStore;
import com.emc.documentum.xml.dds.persistence.xdb.internal.XDBXMLNode;
import com.emc.documentum.xml.dds.util.internal.FederationSupport;
import com.xhive.core.interfaces.XhiveDriverIf;
import com.xhive.core.interfaces.XhiveLocationIteratorIf;
import com.xhive.core.interfaces.XhiveSessionIf;
import com.xhive.dom.interfaces.XhiveBlobNodeIf;
import com.xhive.dom.interfaces.XhiveLibraryChildIf;
import com.xhive.dom.interfaces.XhiveLibraryIf;
import com.xhive.dom.interfaces.XhiveLocationIf;
import com.xhive.dom.interfaces.XhiveNodeIf;
import com.xhive.error.XhiveException;
import com.xhive.query.interfaces.XhiveQueryResultIf;
import java.util.Collection;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public final class XDBXMLUtil {
    private static final String STEP_SEPARATOR = "/";
    private static final String XPOINTER_SEPARATOR = "#";
    private static final String QUOTE = "\"";
    private static final String XPOINTER_STRING = "xpointer";
    private static final String BRACKET_OPEN = "(";
    private static final String BRACKET_CLOSE = ")";
    private static final String ID_SEPARATOR = "id";

    private XDBXMLUtil() {
    }

    /*
     * Exception decompiling
     */
    public static boolean existsNode(XhiveSessionIf session, XDBContainer container, String xpointer) throws AmbiguousXPointerException, StoreSpecificException, DeadlockException {
        // This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
        // org.benf.cfr.reader.util.ConfusedCFRException: Tried to end blocks [1[TRYBLOCK], 0[TRYBLOCK]], but top level block is 4[CATCHBLOCK]
        // org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.processEndingBlocks(Op04StructuredStatement.java:397)
        // org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:449)
        // org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:2877)
        // org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:825)
        // org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:217)
        // org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:162)
        // org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:95)
        // org.benf.cfr.reader.entities.Method.analyse(Method.java:355)
        // org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:768)
        // org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:700)
        // org.benf.cfr.reader.Main.doJar(Main.java:134)
        // org.benf.cfr.reader.Main.main(Main.java:189)
        throw new IllegalStateException("Decompilation failed");
    }

    public static XhiveNodeIf retrieveNode(XhiveSessionIf session, XMLNode xmlNode) throws NodeNotFoundException, AmbiguousXPointerException, StoreSpecificException, DeadlockException {
        try {
            if (xmlNode.asNode() != null) {
                return (XhiveNodeIf)xmlNode.asNode();
            }
        }
        catch (ClassCastException cce) {
            throw new NodeNotFoundException(cce);
        }
        try {
            XDBXMLNode node;
            if (xmlNode instanceof XDBXMLNode && (node = (XDBXMLNode)xmlNode).representsLocation()) {
                return XDBLocationUtil.retrieveLibrary(session, (XDBLocation)node.asLocation(), null, false, false, false);
            }
        }
        catch (LocationNotFoundException lnfe) {
            throw new NodeNotFoundException("Library represented by Node could not be found :", lnfe);
        }
        catch (LocationAlreadyExistsException laee) {
            throw new StoreSpecificException("Internal Error.");
        }
        catch (TypeConflictException tce) {
            throw new NodeNotFoundException(tce);
        }
        XhiveLibraryChildIf child = null;
        try {
            child = XDBContainerUtil.retrieveLibraryChild(session, (XDBContainer)xmlNode.getContainer(), null);
        }
        catch (LocationNotFoundException lnfe) {
            throw new NodeNotFoundException("Location could not be found :", lnfe);
        }
        catch (ContainerNotFoundException lnfe) {
            throw new NodeNotFoundException("Container could not be found :", lnfe);
        }
        catch (ClassCastException cce) {
            throw new NodeNotFoundException("Container does not represent an XDB Library Child :", cce);
        }
        catch (TypeConflictException tce) {
            throw new NodeNotFoundException(tce);
        }
        catch (InvalidContentException ice) {
            throw new StoreSpecificException("Internal error.");
        }
        if (xmlNode.getXPointer() == null) {
            return child;
        }
        if (child instanceof XhiveBlobNodeIf) {
            throw new StoreSpecificException("XPointer not applicable to binary data.");
        }
        try {
            XhiveLocationIteratorIf nodeIterator = child.executeXPointerQuery(xmlNode.getXPointer()).getLocationSetValue();
            if (!nodeIterator.hasNext()) {
                throw new NodeNotFoundException("Node could not be found.");
            }
            XhiveNodeIf result = (XhiveNodeIf)nodeIterator.next();
            if (nodeIterator.hasNext()) {
                throw new AmbiguousXPointerException("XPointer expression resolves to multiple Nodes : " + xmlNode.getPath());
            }
            return result;
        }
        catch (XhiveException xe) {
            if (xe.getErrorCode() == 1012) {
                throw new NodeNotFoundException("Node could not be found");
            }
            throw new StoreSpecificException((Throwable)xe);
        }
    }

    public static Store findStore(Node node, Application application) {
        XhiveNodeIf xhiveNode = (XhiveNodeIf)node;
        XhiveSessionIf session = xhiveNode.getSession();
        XhiveDriverIf driver = session.getDriver();
        Store nodeStore = null;
        Collection<Store> stores = application.getStoreManager().getStores();
        if (stores != null) {
            for (Store store : stores) {
                if (store.getType() != StoreType.XDB || !XDBXMLUtil.isStoreBootDriverEqualsDriver(((XDBStore)store).getBootstrap(), driver)) continue;
                nodeStore = store;
                break;
            }
        }
        return nodeStore;
    }

    public static String generatePath(Node node) {
        if (node == null) {
            return null;
        }
        StringBuilder result = new StringBuilder();
        Node pointer = null;
        String idValue = null;
        if (node instanceof XhiveLibraryChildIf) {
            String path = ((XhiveLibraryChildIf)node).getFullPath();
            if (node.getNodeType() == 201 && !path.endsWith("/")) {
                path = path + '/';
            }
            return path;
        }
        if ("".equals(XDBXMLUtil.getXhiveLocationStep(node)) && node.getParentNode() == null) {
            return null;
        }
        if (node.getNodeType() == 1 && (idValue = XDBXMLUtil.getIDValue((Element)node)) != null) {
            result.append("#");
            result.append("xpointer");
            result.append("(");
            result.append("id");
            result.append("(");
            result.append("\"");
            result.append(idValue);
            result.append("\"");
            result.append(")");
            result.append(")");
            pointer = node.getOwnerDocument();
        } else {
            result.append(")");
            if (node.getNodeType() == 2) {
                pointer = ((Attr)node).getOwnerElement();
                result.insert(0, XDBXMLUtil.getXhiveLocationStep(node));
                result.insert(0, "/");
            } else {
                pointer = node;
            }
        }
        while (pointer != null) {
            String locationStep = XDBXMLUtil.getXhiveLocationStep(pointer);
            Node node2 = pointer instanceof XhiveLibraryChildIf ? ((XhiveLibraryChildIf)pointer).getOwnerLibrary() : pointer.getParentNode();
            pointer = node2;
            if (pointer == null) continue;
            result.insert(0, locationStep);
            if (pointer instanceof Document) {
                result.insert(0, "#xpointer(");
                continue;
            }
            result.insert(0, "/");
        }
        return new String(result);
    }

    private static String getXhiveLocationStep(Node node) {
        if (node.getNodeType() == 2) {
            return "" + '@' + node.getNodeName();
        }
        if (node instanceof XhiveLibraryChildIf) {
            String name = ((XhiveLibraryChildIf)node).getName();
            return name == null ? "" : name;
        }
        Node parentNode = node.getParentNode();
        return parentNode == null ? "" : "node()[" + (XDBXMLUtil.getIndexForChild(parentNode, node) + 1) + "]";
    }

    private static int getIndexForChild(Node parent, Node child) {
        int index = 0;
        for (Node pointer = parent.getFirstChild(); pointer != null; pointer = pointer.getNextSibling()) {
            if (pointer.equals(child)) {
                return index;
            }
            ++index;
        }
        return -1;
    }

    private static String getIDValue(Element element) {
        NamedNodeMap attrs = element.getAttributes();
        for (int k = 0; k < attrs.getLength(); ++k) {
            Attr attr = (Attr)attrs.item(k);
            if (!attr.isId()) continue;
            return attr.getValue();
        }
        return null;
    }

    private static boolean isStoreBootDriverEqualsDriver(String storeBoot, XhiveDriverIf driver) {
        return driver.equals((Object)FederationSupport.getInstance().getDriver(storeBoot));
    }
}

