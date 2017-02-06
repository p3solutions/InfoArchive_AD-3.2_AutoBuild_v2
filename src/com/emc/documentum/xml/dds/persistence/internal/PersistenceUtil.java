/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.apache.xerces.parsers.DOMParser
 */
package com.emc.documentum.xml.dds.persistence.internal;

import com.emc.documentum.xml.dds.exception.DDSException;
import com.emc.documentum.xml.dds.logging.LogCenter;
import com.emc.documentum.xml.dds.persistence.Container;
import com.emc.documentum.xml.dds.persistence.ContentDescriptor;
import com.emc.documentum.xml.dds.persistence.Data;
import com.emc.documentum.xml.dds.persistence.Location;
import com.emc.documentum.xml.dds.persistence.LocationOptions;
import com.emc.documentum.xml.dds.persistence.Metadata;
import com.emc.documentum.xml.dds.persistence.Session;
import com.emc.documentum.xml.dds.persistence.StoreChild;
import com.emc.documentum.xml.dds.persistence.XMLNode;
import com.emc.documentum.xml.dds.persistence.content.BinaryContentDescriptor;
import com.emc.documentum.xml.dds.persistence.content.XMLContentDescriptor;
import com.emc.documentum.xml.dds.persistence.data.InputStreamData;
import com.emc.documentum.xml.dds.persistence.exception.AmbiguousXPointerException;
import com.emc.documentum.xml.dds.persistence.exception.ContainerAlreadyExistsException;
import com.emc.documentum.xml.dds.persistence.exception.ContainerNotFoundException;
import com.emc.documentum.xml.dds.persistence.exception.DeadlockException;
import com.emc.documentum.xml.dds.persistence.exception.IllegalActionException;
import com.emc.documentum.xml.dds.persistence.exception.InvalidContentException;
import com.emc.documentum.xml.dds.persistence.exception.LocationAlreadyExistsException;
import com.emc.documentum.xml.dds.persistence.exception.LocationNotFoundException;
import com.emc.documentum.xml.dds.persistence.exception.NodeNotFoundException;
import com.emc.documentum.xml.dds.persistence.exception.SchemeNotSupportedException;
import com.emc.documentum.xml.dds.persistence.exception.StoreChildNotFoundException;
import com.emc.documentum.xml.dds.persistence.exception.StoreSpecificException;
import com.emc.documentum.xml.dds.persistence.exception.TypeConflictException;
import com.emc.documentum.xml.dds.persistence.metadata.DocumentumMetadata;
import com.emc.documentum.xml.dds.serialization.exception.SerializationException;
import com.emc.documentum.xml.dds.structure.DDSDataSet;
import com.emc.documentum.xml.dds.structure.DDSLocale;
import com.emc.documentum.xml.dds.structure.Structure;
import com.emc.documentum.xml.dds.structure.StructureStrategy;
import com.emc.documentum.xml.dds.structure.StructureStrategyType;
import com.emc.documentum.xml.dds.structure.strategy.DDSStructureStrategyType;
import com.emc.documentum.xml.dds.util.internal.DistributedByteArray;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import javax.xml.namespace.QName;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.apache.xerces.parsers.DOMParser;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public final class PersistenceUtil {
    private static XPathFactory xpathFactory;

    private PersistenceUtil() {
    }

    public static void copy(Session sourceSession, Location source, Session targetSession, Location target, boolean replace) throws LocationNotFoundException, LocationAlreadyExistsException, TypeConflictException, IllegalActionException, StoreSpecificException, DeadlockException {
        PersistenceUtil.ensureTopLevelLocationExistence(sourceSession, targetSession, source, target, replace);
        PersistenceUtil.copyDescendants(sourceSession, source, targetSession, target);
    }

    public static void copy(Session sourceSession, Container source, Session targetSession, Container target, boolean replace) throws LocationNotFoundException, ContainerNotFoundException, ContainerAlreadyExistsException, StoreSpecificException, TypeConflictException, DeadlockException {
        block6 : {
            if (source.getLocation().getChildLocation(source.getName()).exists(sourceSession)) {
                throw new TypeConflictException("Copy failed : Source maps to a Location : " + source.getPath());
            }
            try {
                InputStream is;
                Data result = source.retrieve(sourceSession, null);
                target.persist(targetSession, null, result, replace);
                if (!(result instanceof InputStreamData) || (is = (InputStream)result.content()) == null) break block6;
                try {
                    is.close();
                }
                catch (IOException e) {
                    throw new StoreSpecificException("Internal error.");
                }
            }
            catch (SerializationException se) {
                throw new StoreSpecificException("Internal error.");
            }
            catch (InvalidContentException ie) {
                throw new StoreSpecificException("Internal error.");
            }
        }
    }

    public static void move(Session sourceSession, Location source, Session targetSession, Location target, boolean replace) throws LocationNotFoundException, LocationAlreadyExistsException, TypeConflictException, IllegalActionException, StoreSpecificException, DeadlockException {
        PersistenceUtil.ensureTopLevelLocationExistence(sourceSession, targetSession, source, target, replace);
        PersistenceUtil.copyDescendants(sourceSession, source, targetSession, target);
        source.delete(sourceSession);
    }

    public static void move(Session sourceSession, Container source, Session targetSession, Container target, boolean replace) throws LocationNotFoundException, ContainerNotFoundException, ContainerAlreadyExistsException, StoreSpecificException, TypeConflictException, IllegalActionException, DeadlockException {
        if (source.getLocation().getChildLocation(source.getName()).exists(sourceSession)) {
            throw new TypeConflictException("Move failed : Source maps to a Location : " + source.getPath());
        }
        try {
            target.persist(targetSession, null, source.retrieve(sourceSession, null), replace);
            source.delete(sourceSession);
        }
        catch (SerializationException se) {
            throw new StoreSpecificException("Internal error.");
        }
        catch (InvalidContentException ie) {
            throw new StoreSpecificException("Internal error.");
        }
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private static void ensureTopLevelLocationExistence(Session sourceSession, Session targetSession, Location source, Location target, boolean replace) throws LocationNotFoundException, StoreSpecificException, IllegalActionException, TypeConflictException, DeadlockException, LocationAlreadyExistsException {
        Location parent = target.getParent();
        if (target.exists(targetSession)) {
            if (!replace) throw new LocationAlreadyExistsException("Target Location for operation already exists.");
            target.delete(targetSession);
            target.create(targetSession, source.getOptions(sourceSession), false);
        } else {
            if (!parent.exists(targetSession)) {
                throw new LocationNotFoundException("Parent Location does not exist for Location operation.");
            }
            target.create(targetSession, source.getOptions(sourceSession), false);
        }
        if (source.exists(sourceSession) || !source.getParent().getChildContainer(source.getName()).exists(sourceSession)) return;
        throw new TypeConflictException("Action failed : Source maps to a Container : " + source.getPath());
    }

    private static void copyDescendants(Session sourceSession, Location source, Session targetSession, Location target) throws LocationNotFoundException, LocationAlreadyExistsException, TypeConflictException, DeadlockException, StoreSpecificException {
        Collection<StoreChild> childContainers = source.listChildren(sourceSession, false, true, false);
        for (StoreChild child : childContainers) {
            try {
                Data data = ((Container)child).retrieve(sourceSession, new BinaryContentDescriptor());
                target.getChildContainer(child.getName()).persist(targetSession, new BinaryContentDescriptor(), data, false);
                continue;
            }
            catch (ContainerNotFoundException cnfe) {
                throw new StoreSpecificException("Internal error.");
            }
            catch (ContainerAlreadyExistsException caee) {
                throw new StoreSpecificException("Internal error.");
            }
            catch (SerializationException se) {
                throw new StoreSpecificException("Internal error.");
            }
            catch (InvalidContentException ie) {
                throw new StoreSpecificException("Internal error.");
            }
        }
        Collection<StoreChild> childLocations = source.listChildren(sourceSession, true, false, false);
        for (StoreChild child2 : childLocations) {
            Location newLocation = target.getChildLocation(child2.getName());
            newLocation.create(targetSession, null, false);
            PersistenceUtil.copyDescendants(sourceSession, (Location)child2, targetSession, newLocation);
        }
    }

    public static boolean existsXMLNode(InputSource input, XMLNode xmlNode) throws AmbiguousXPointerException, StoreSpecificException {
        Document document = null;
        try {
            DOMParser parser = new DOMParser();
            parser.parse(input);
            document = parser.getDocument();
        }
        catch (FileNotFoundException fnfe) {
            return false;
        }
        catch (IOException ioe) {
            throw new StoreSpecificException("A problem occurred accessing the Container :", ioe);
        }
        catch (SAXException se) {
            return false;
        }
        try {
            String xpointer = xmlNode.getXPointer();
            String xpathExpression = xpointer.substring(xpointer.indexOf(40) + 1, xpointer.lastIndexOf(41));
            XPath xpath = xpathFactory.newXPath();
            NodeList nodeList = (NodeList)xpath.evaluate(xpathExpression, document, XPathConstants.NODESET);
            if (nodeList.getLength() == 0) {
                return false;
            }
            if (nodeList.getLength() == 1) {
                return true;
            }
            throw new AmbiguousXPointerException("Exists failed : XPointer points to multiple Nodes.");
        }
        catch (XPathExpressionException xpee) {
            return false;
        }
    }

    public static Node retrieveXMLNode(InputSource input, XMLNode xmlNode) throws NodeNotFoundException, AmbiguousXPointerException, StoreSpecificException {
        Document document = null;
        try {
            DOMParser parser = new DOMParser();
            parser.parse(input);
            document = parser.getDocument();
        }
        catch (FileNotFoundException fnfe) {
            throw new NodeNotFoundException("Container could not be found :", fnfe);
        }
        catch (IOException ioe) {
            throw new StoreSpecificException("A problem occurred accessing the Container :", ioe);
        }
        catch (SAXException se) {
            throw new DOMException((short) 12, "Syntax error while parsing.");
        }
        if (document == null) {
            throw new NodeNotFoundException("File does not contain XML.");
        }
        if (xmlNode.getXPointer() == null) {
            return document.getFirstChild().getOwnerDocument();
        }
        try {
            String xpointer = xmlNode.getXPointer();
            String xpathExpression = xpointer.substring(xpointer.indexOf(40) + 1, xpointer.lastIndexOf(41));
            XPath xpath = xpathFactory.newXPath();
            NodeList nodeList = (NodeList)xpath.evaluate(xpathExpression, document, XPathConstants.NODESET);
            if (nodeList.getLength() == 0) {
                throw new NodeNotFoundException("Node does not exist : " + xmlNode.getPath());
            }
            if (nodeList.getLength() == 1) {
                return nodeList.item(0);
            }
            throw new AmbiguousXPointerException("Could not retrieve XML Node failed : XPointer points to multiple Nodes : " + xmlNode.getPath());
        }
        catch (XPathExpressionException xpee) {
            throw new NodeNotFoundException("XPath Expression invalid.", xpee);
        }
    }

    public static Metadata getDocumentumMetadata(Session session, Container container) throws SchemeNotSupportedException, ContainerNotFoundException, StoreSpecificException, DeadlockException {
        Structure context = container.getContext();
        if (context instanceof DDSDataSet || context instanceof DDSLocale) {
            Container metadataContainer = null;
            if (context instanceof DDSDataSet) {
                if (((DDSDataSet)context).getStructureStrategy().getType() != DDSStructureStrategyType.DOCUMENTUM) {
                    throw new SchemeNotSupportedException("The Documentum Metadata Scheme is only supported for documents in DDSDataSets or DDSLocales");
                }
                metadataContainer = ((DDSDataSet)context).getMetadataContainer(container);
            } else {
                if (((DDSLocale)context).getDataSet().getStructureStrategy().getType() != DDSStructureStrategyType.DOCUMENTUM) {
                    throw new SchemeNotSupportedException("The Documentum Metadata Scheme is only supported for documents in DDSDataSets or DDSLocales");
                }
                metadataContainer = ((DDSLocale)context).getDataSet().getMetadataContainer(container);
            }
            try {
                return new DocumentumMetadata(metadataContainer.retrieve(session, new XMLContentDescriptor()));
            }
            catch (StoreChildNotFoundException lnfe) {
                throw new ContainerNotFoundException(lnfe);
            }
            catch (InvalidContentException ie) {
                throw new StoreSpecificException("Internal error.");
            }
            catch (TypeConflictException tce) {
                throw new StoreSpecificException("Internal error.");
            }
        }
        throw new SchemeNotSupportedException("The Documentum Metadata Scheme is only supported for documents in DDSDataSets or DDSLocales");
    }

    public static void setDocumentumMetadata(Session session, Container container, DocumentumMetadata metadata) throws SchemeNotSupportedException, ContainerNotFoundException, StoreSpecificException, DeadlockException {
        Structure context = container.getContext();
        if (context instanceof DDSDataSet || context instanceof DDSLocale) {
            Container metadataContainer = null;
            if (context instanceof DDSDataSet) {
                if (((DDSDataSet)context).getStructureStrategy().getType() != DDSStructureStrategyType.DOCUMENTUM) {
                    throw new SchemeNotSupportedException("The Documentum Metadata Scheme is only supported for documents in DDSDataSets or DDSLocales");
                }
                metadataContainer = ((DDSDataSet)context).getMetadataContainer(container);
            } else {
                if (((DDSLocale)context).getDataSet().getStructureStrategy().getType() != DDSStructureStrategyType.DOCUMENTUM) {
                    throw new SchemeNotSupportedException("The Documentum Metadata Scheme is only supported for documents in DDSDataSets or DDSLocales");
                }
                metadataContainer = ((DDSLocale)context).getDataSet().getMetadataContainer(container);
            }
            try {
                if (!metadataContainer.getLocation().exists(session)) {
                    metadataContainer.getLocation().create(session, null, true);
                }
                metadataContainer.persist(session, new XMLContentDescriptor(), metadata.getData(), true);
            }
            catch (DDSException de) {
                throw new StoreSpecificException(de);
            }
        } else {
            throw new SchemeNotSupportedException("The Documentum Metadata Scheme is only supported for documents in DDSDataSets or DDSLocales");
        }
    }

    public static InputStream getInputStreamCached(InputStream is) throws IOException {
        if (is == null) {
            return null;
        }
        DistributedByteArray dba = new DistributedByteArray();
        int len = 0;
        byte[] buffer = new byte[16384];
        while (len != -1) {
            len = is.read(buffer, 0, buffer.length);
            if (len <= 1) continue;
            dba.getOutputStream().write(buffer, 0, len);
        }
        is.close();
        return dba.getInputStream();
    }

    static {
        try {
            xpathFactory = XPathFactory.newInstance("http://java.sun.com/jaxp/xpath/dom");
        }
        catch (Exception e) {
            LogCenter.exception("PersistenceUtil : Problem instantiating XPathFactory", (Throwable)e);
        }
    }
}

