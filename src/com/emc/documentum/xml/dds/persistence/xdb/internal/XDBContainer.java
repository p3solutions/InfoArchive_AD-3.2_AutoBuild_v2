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
 *  com.xhive.dom.interfaces.XhiveMetadataMapIf
 *  com.xhive.dom.interfaces.XhiveNodeIf
 *  com.xhive.error.XhiveDeadlockException
 *  com.xhive.error.XhiveException
 */
package com.emc.documentum.xml.dds.persistence.xdb.internal;

import com.emc.documentum.xml.dds.persistence.Container;
import com.emc.documentum.xml.dds.persistence.ContentDescriptor;
import com.emc.documentum.xml.dds.persistence.Data;
import com.emc.documentum.xml.dds.persistence.Location;
import com.emc.documentum.xml.dds.persistence.Metadata;
import com.emc.documentum.xml.dds.persistence.MetadataScheme;
import com.emc.documentum.xml.dds.persistence.Session;
import com.emc.documentum.xml.dds.persistence.StoreType;
import com.emc.documentum.xml.dds.persistence.XMLNode;
import com.emc.documentum.xml.dds.persistence.content.ObjectContentDescriptor;
import com.emc.documentum.xml.dds.persistence.content.XMLContentDescriptor;
import com.emc.documentum.xml.dds.persistence.data.AbstractData;
import com.emc.documentum.xml.dds.persistence.data.InputStreamData;
import com.emc.documentum.xml.dds.persistence.data.ObjectData;
import com.emc.documentum.xml.dds.persistence.data.StringData;
import com.emc.documentum.xml.dds.persistence.exception.AmbiguousXPointerException;
import com.emc.documentum.xml.dds.persistence.exception.ContainerAlreadyExistsException;
import com.emc.documentum.xml.dds.persistence.exception.ContainerNotFoundException;
import com.emc.documentum.xml.dds.persistence.exception.DeadlockException;
import com.emc.documentum.xml.dds.persistence.exception.IllegalActionException;
import com.emc.documentum.xml.dds.persistence.exception.InvalidContentException;
import com.emc.documentum.xml.dds.persistence.exception.LocationNotFoundException;
import com.emc.documentum.xml.dds.persistence.exception.NodeNotFoundException;
import com.emc.documentum.xml.dds.persistence.exception.SchemeNotSupportedException;
import com.emc.documentum.xml.dds.persistence.exception.StoreSpecificException;
import com.emc.documentum.xml.dds.persistence.exception.TypeConflictException;
import com.emc.documentum.xml.dds.persistence.internal.AbstractContainer;
import com.emc.documentum.xml.dds.persistence.internal.PersistenceUtil;
import com.emc.documentum.xml.dds.persistence.metadata.DocumentumMetadata;
import com.emc.documentum.xml.dds.persistence.metadata.XDBMetadata;
import com.emc.documentum.xml.dds.persistence.xdb.internal.XDBContainerUtil;
import com.emc.documentum.xml.dds.persistence.xdb.internal.XDBLocation;
import com.emc.documentum.xml.dds.persistence.xdb.internal.XDBXMLNode;
import com.emc.documentum.xml.dds.persistence.xdb.internal.XDBXMLUtil;
import com.emc.documentum.xml.dds.serialization.Serializer;
import com.emc.documentum.xml.dds.serialization.exception.DeserializationException;
import com.emc.documentum.xml.dds.serialization.exception.SerializationException;
import com.emc.documentum.xml.dds.util.internal.DistributedByteArray;
import com.xhive.core.interfaces.XhiveDatabaseIf;
import com.xhive.core.interfaces.XhiveSessionIf;
import com.xhive.dom.interfaces.XhiveBlobNodeIf;
import com.xhive.dom.interfaces.XhiveDocumentIf;
import com.xhive.dom.interfaces.XhiveLibraryChildIf;
import com.xhive.dom.interfaces.XhiveLibraryIf;
import com.xhive.dom.interfaces.XhiveMetadataMapIf;
import com.xhive.dom.interfaces.XhiveNodeIf;
import com.xhive.error.XhiveDeadlockException;
import com.xhive.error.XhiveException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.Node;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;

public class XDBContainer
extends AbstractContainer {
    protected XDBContainer(XDBLocation location, String name) {
        super(location, name);
    }

    @Override
    public StoreType getStoreType() {
        return StoreType.XDB;
    }

    @Override
    public XMLNode getXMLNode(String xpointer) {
        return new XDBXMLNode(this, xpointer);
    }

    @Override
    public XMLNode asXMLNode() {
        return new XDBXMLNode(this);
    }

    @Override
    public boolean exists(Session session) throws StoreSpecificException, DeadlockException {
        try {
            XhiveLibraryChildIf libraryChild = ((XhiveSessionIf)session.getSession()).getDatabase().getRoot().getByPath(this.getPath());
            return libraryChild != null && (libraryChild instanceof XhiveDocumentIf || libraryChild instanceof XhiveBlobNodeIf);
        }
        catch (XhiveDeadlockException xde) {
            throw new DeadlockException((Throwable)xde);
        }
        catch (XhiveException xe) {
            throw new StoreSpecificException((Throwable)xe);
        }
    }

    @Override
    public void delete(Session session) throws ContainerNotFoundException, StoreSpecificException, DeadlockException, IllegalActionException {
        block5 : {
            try {
                XhiveLibraryIf root = ((XhiveSessionIf)session.getSession()).getDatabase().getRoot();
                XhiveLibraryChildIf libraryChild = root.getByPath(this.getPath());
                if (libraryChild == null) {
                    throw new ContainerNotFoundException("Delete failed : Container does not exist : " + this.getPath());
                }
                if (libraryChild instanceof XhiveDocumentIf || libraryChild instanceof XhiveBlobNodeIf) {
                    XhiveLibraryIf parent = libraryChild.getOwnerLibrary();
                    parent.removeChild((Node)libraryChild);
                    break block5;
                }
                throw new ContainerNotFoundException("Delete failed : Container does not exist : " + this.getPath());
            }
            catch (XhiveDeadlockException xde) {
                throw new DeadlockException((Throwable)xde);
            }
            catch (XhiveException xe) {
                throw new StoreSpecificException((Throwable)xe);
            }
        }
    }

    @Override
    public void persist(Session session, ContentDescriptor contentDescriptor, Data<?> data, boolean replace) throws LocationNotFoundException, ContainerAlreadyExistsException, TypeConflictException, SerializationException, StoreSpecificException, DeadlockException {
        XhiveSessionIf xSession = (XhiveSessionIf)session.getSession();
        if (contentDescriptor != null && contentDescriptor.isXML()) {
            if (contentDescriptor instanceof ObjectContentDescriptor) {
                StringData transformedData = new StringData(((ObjectContentDescriptor)contentDescriptor).getSerializer().serialize(((ObjectData)data).content()));
                XDBContainerUtil.storeXML(xSession, this, new XMLContentDescriptor(), transformedData, replace, null, null);
            } else {
                XDBContainerUtil.storeXML(xSession, this, contentDescriptor, data, replace, null, null);
            }
        } else {
            XDBContainerUtil.storeNonXML(xSession, this, contentDescriptor, data, replace);
        }
    }

    @Override
    public Data<?> retrieve(Session session, ContentDescriptor contentDescriptor) throws ContainerNotFoundException, InvalidContentException, TypeConflictException, StoreSpecificException, DeadlockException {
        XhiveSessionIf xSession = (XhiveSessionIf)session.getSession();
        try {
            XhiveLibraryChildIf libraryChild = XDBContainerUtil.retrieveLibraryChild(xSession, this, contentDescriptor);
            if (contentDescriptor == null) {
                if (libraryChild instanceof XhiveBlobNodeIf) {
                    return this.getTypedData(new InputStreamData(this.getInputStreamCached(((XhiveBlobNodeIf)libraryChild).getContents())), "application/octet-stream");
                }
                if (libraryChild instanceof XhiveDocumentIf) {
                    return this.getTypedData(new InputStreamData(this.nodeToInputStream((XhiveNodeIf)libraryChild, false)), "application/xml");
                }
                return null;
            }
            if (contentDescriptor instanceof XMLContentDescriptor) {
                return this.getTypedData(new InputStreamData(this.nodeToInputStream((XhiveNodeIf)libraryChild, false)), "application/xml");
            }
            if (contentDescriptor instanceof ObjectContentDescriptor) {
                return this.getTypedData(new ObjectData(((ObjectContentDescriptor)contentDescriptor).getSerializer().deserialize(this.nodeToInputStream((XhiveNodeIf)libraryChild, true))), "application/java-serialized-object");
            }
            if (libraryChild.getNodeType() == 203) {
                return this.getTypedData(new InputStreamData(this.getInputStreamCached(((XhiveBlobNodeIf)libraryChild).getContents())), "application/octet-stream");
            }
            return this.getTypedData(new InputStreamData(this.nodeToInputStream((XhiveNodeIf)libraryChild, false)), "application/xml");
        }
        catch (DeserializationException de) {
            throw new InvalidContentException("The content could not be deserialized properly :", de);
        }
        catch (LocationNotFoundException lnfe) {
            throw new ContainerNotFoundException(lnfe);
        }
        catch (IOException ioe) {
            throw new StoreSpecificException(ioe);
        }
    }

    private Data<?> getTypedData(AbstractData<?> data, String mimeType) {
        data.setMimeType(mimeType);
        return data;
    }

    private InputStream getInputStreamCached(InputStream is) throws IOException {
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

    private InputStream nodeToInputStream(XhiveNodeIf node, boolean omitXMLDeclaration) {
        XhiveLibraryIf dbRoot = node.getSession().getDatabase().getRoot();
        DistributedByteArray dba = new DistributedByteArray();
        LSOutput output = dbRoot.createLSOutput();
        output.setByteStream(dba.getOutputStream());
        output.setEncoding("UTF-8");
        LSSerializer serializer = dbRoot.createLSSerializer();
        DOMConfiguration domConfig = serializer.getDomConfig();
        domConfig.setParameter("discard-default-content", Boolean.FALSE);
        serializer.write((Node)node, output);
        return dba.getInputStream();
    }

    @Override
    public Metadata getMetadata(Session session, MetadataScheme scheme) throws SchemeNotSupportedException, ContainerNotFoundException, StoreSpecificException, DeadlockException {
        switch (scheme) {
            case XDB: {
                XhiveSessionIf xSession = (XhiveSessionIf)session.getSession();
                try {
                    return new XDBMetadata((Map<String, String>)XDBContainerUtil.retrieveLibraryChild(xSession, this, null).getMetadata());
                }
                catch (LocationNotFoundException cnfe) {
                    throw new ContainerNotFoundException(cnfe);
                }
                catch (TypeConflictException tce) {
                    throw new ContainerNotFoundException(tce);
                }
                catch (InvalidContentException ice) {
                    throw new StoreSpecificException("Internal error.");
                }
            }
            case DOCUMENTUM: {
                return PersistenceUtil.getDocumentumMetadata(session, this);
            }
        }
        return null;
    }

    @Override
    public void setMetadata(Session session, Metadata metadata) throws SchemeNotSupportedException, ContainerNotFoundException, TypeConflictException, StoreSpecificException, DeadlockException {
        switch (metadata.getScheme()) {
            case XDB: {
                XhiveSessionIf xSession = (XhiveSessionIf)session.getSession();
                try {
                    XhiveMetadataMapIf map = XDBContainerUtil.retrieveLibraryChild(xSession, this, null).getMetadata();
                    map.clear();
                    map.putAll((Map)((XDBMetadata)metadata));
                    return;
                }
                catch (LocationNotFoundException cnfe) {
                    throw new ContainerNotFoundException(cnfe);
                }
                catch (InvalidContentException ice) {
                    throw new StoreSpecificException("Internal error.");
                }
            }
            case DOCUMENTUM: {
                PersistenceUtil.setDocumentumMetadata(session, this, (DocumentumMetadata)metadata);
                return;
            }
        }
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    public void move(Session session, Container target, boolean replace) throws LocationNotFoundException, ContainerNotFoundException, ContainerAlreadyExistsException, TypeConflictException, IllegalActionException, StoreSpecificException, DeadlockException {
        XhiveLibraryIf parentNode;
        XhiveLibraryChildIf sourceNode;
        Container targetContainer = target;
        Location parentLocation = targetContainer.getLocation();
        if (target.exists(session)) {
            if (!replace) throw new ContainerAlreadyExistsException("Target Container for move already exists.");
            target.delete(session);
        } else if (!parentLocation.exists(session)) {
            throw new LocationNotFoundException("Parent Location does not exist for Container move.");
        }
        try {
            parentNode = (XhiveLibraryIf)XDBXMLUtil.retrieveNode((XhiveSessionIf)session.getSession(), new XDBXMLNode(parentLocation));
            sourceNode = (XhiveLibraryChildIf)XDBXMLUtil.retrieveNode((XhiveSessionIf)session.getSession(), this.asXMLNode());
            if (!(sourceNode instanceof XhiveDocumentIf) && !(sourceNode instanceof XhiveBlobNodeIf)) {
                throw new TypeConflictException("Move failed : source maps to a Location.");
            }
        }
        catch (NodeNotFoundException nnfe) {
            if (!this.getLocation().getChildLocation(this.getName()).exists(session)) throw new ContainerNotFoundException(nnfe);
            throw new TypeConflictException("Move failed : source maps to a Location.");
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
    public void copy(Session session, Container target, boolean replace) throws LocationNotFoundException, ContainerNotFoundException, ContainerAlreadyExistsException, TypeConflictException, IllegalActionException, StoreSpecificException, DeadlockException {
        XhiveLibraryIf parentNode;
        XhiveLibraryChildIf sourceNode;
        Container targetContainer = target;
        Location parentLocation = targetContainer.getLocation();
        if (target.exists(session)) {
            if (!replace) throw new ContainerAlreadyExistsException("Target Container for copy already exists.");
            target.delete(session);
        } else if (!parentLocation.exists(session)) {
            throw new LocationNotFoundException("Parent Location does not exist for Container copy.");
        }
        try {
            parentNode = (XhiveLibraryIf)XDBXMLUtil.retrieveNode((XhiveSessionIf)session.getSession(), new XDBXMLNode(parentLocation));
            sourceNode = (XhiveLibraryChildIf)XDBXMLUtil.retrieveNode((XhiveSessionIf)session.getSession(), this.asXMLNode());
            if (!(sourceNode instanceof XhiveDocumentIf) && !(sourceNode instanceof XhiveBlobNodeIf)) {
                throw new TypeConflictException("Copy failed : source maps to a Location.");
            }
        }
        catch (NodeNotFoundException nnfe) {
            if (!this.getLocation().getChildLocation(this.getName()).exists(session)) throw new ContainerNotFoundException(nnfe);
            throw new TypeConflictException("Copy failed : source maps to a Location.");
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

