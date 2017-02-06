/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  com.emc.documentum.xml.xproc.io.Source
 *  com.emc.documentum.xml.xproc.pipeline.model.ExtensionContext
 *  com.xhive.core.interfaces.XhiveSessionIf
 *  com.xhive.dom.interfaces.XhiveBlobNodeIf
 *  com.xhive.dom.interfaces.XhiveNodeIf
 */
package com.emc.documentum.xml.dds.xproc.internal;

import com.emc.documentum.xml.dds.application.Application;
import com.emc.documentum.xml.dds.exception.DDSException;
import com.emc.documentum.xml.dds.persistence.Session;
import com.emc.documentum.xml.dds.persistence.SessionStoreUserStrategy;
import com.emc.documentum.xml.dds.persistence.Store;
import com.emc.documentum.xml.dds.persistence.StoreChild;
import com.emc.documentum.xml.dds.persistence.StoreType;
import com.emc.documentum.xml.dds.persistence.StoreUser;
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
import com.emc.documentum.xml.dds.persistence.filesystem.internal.FileSystemContainer;
import com.emc.documentum.xml.dds.persistence.filesystem.internal.FileSystemContainerUtil;
import com.emc.documentum.xml.dds.persistence.filesystem.internal.FileSystemSession;
import com.emc.documentum.xml.dds.persistence.filesystem.internal.FileSystemXMLUtil;
import com.emc.documentum.xml.dds.persistence.xdb.internal.XDBContainer;
import com.emc.documentum.xml.dds.persistence.xdb.internal.XDBContainerUtil;
import com.emc.documentum.xml.dds.persistence.xdb.internal.XDBLocation;
import com.emc.documentum.xml.dds.persistence.xdb.internal.XDBLocationUtil;
import com.emc.documentum.xml.dds.persistence.xdb.internal.XDBSession;
import com.emc.documentum.xml.dds.persistence.xdb.internal.XDBXMLUtil;
import com.emc.documentum.xml.dds.uri.DDSURI;
import com.emc.documentum.xml.dds.uri.URIResolver;
import com.emc.documentum.xml.dds.uri.URITarget;
import com.emc.documentum.xml.dds.user.User;
import com.emc.documentum.xml.dds.util.SessionPool;
import com.emc.documentum.xml.dds.util.internal.StringUtils;
import com.emc.documentum.xml.dds.xproc.AbstractDDSResolverHandler;
import com.emc.documentum.xml.xproc.io.Source;
import com.emc.documentum.xml.xproc.pipeline.model.ExtensionContext;
import com.xhive.core.interfaces.XhiveSessionIf;
import com.xhive.dom.interfaces.XhiveBlobNodeIf;
import com.xhive.dom.interfaces.XhiveNodeIf;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.w3c.dom.Node;

public class DefaultDDSResolverHandler
extends AbstractDDSResolverHandler {
    private final boolean readOnly;

    public DefaultDDSResolverHandler(boolean readOnly) {
        this.readOnly = readOnly;
    }

    public Source getSource(String publicID, String systemID, ExtensionContext extensionContext) throws Exception {
        return this.getSource(publicID, systemID, false, extensionContext);
    }

    private Source getSource(String publicID, String systemID, boolean isXMLSource, ExtensionContext extensionContext) throws Exception {
        if (StringUtils.isEmpty(systemID) || !systemID.startsWith("dds:")) {
            return null;
        }
        Application application = this.getApplication();
        User user = this.getUser();
        if (user != null) {
            SessionStoreUserStrategy strategy;
            Store store;
            StoreType storeType;
            StoreUser storeUser;
            URITarget target;
            SessionPool sessionPool = this.getSessionPool();
            Session session = sessionPool.getSession(store = (target = application.getDefaultURIResolver().resolveURI(DDSURI.parseURI(systemID), user)).getStore(), storeUser = (strategy = application.getSessionStoreUserStrategy()).getStoreUser(application, user, store.getAlias()), true, this.readOnly);
            if (!session.isOpen()) {
                session.begin();
            }
            if ((storeType = store.getType()) == StoreType.XDB) {
                return this.getXDBSource((XDBSession)session, publicID, systemID, target, isXMLSource);
            }
            if (store.getType() == StoreType.FILESYSTEM) {
                return this.getFileSystemSource((FileSystemSession)session, publicID, systemID, target, isXMLSource);
            }
            throw new DDSException("Unsupported store type: " + (Object)((Object)storeType));
        }
        throw new DDSException("Session timed out");
    }

    private Source getXDBSource(XDBSession session, String publicID, String systemID, URITarget target, boolean isXMLSource) throws IOException, LocationNotFoundException, LocationAlreadyExistsException, TypeConflictException, StoreSpecificException, DeadlockException, ContainerNotFoundException, InvalidContentException, NodeNotFoundException, AmbiguousXPointerException {
        StoreChild storeChild = target.getStoreChild();
        XhiveNodeIf node = storeChild.isLocation() ? XDBLocationUtil.retrieveLibrary(session.getSession(), (XDBLocation)storeChild, null, false, false, false) : (storeChild.isContainer() ? XDBContainerUtil.retrieveLibraryChild(session.getSession(), (XDBContainer)storeChild, null) : XDBXMLUtil.retrieveNode(session.getSession(), (XMLNode)storeChild));
        if (!(node instanceof XhiveBlobNodeIf)) {
            return new Source((Node)node, publicID, systemID);
        }
        if (isXMLSource) {
            throw new IOException("System ID: " + systemID + ", public ID: " + publicID + " resolved to a DOM node of type: " + node.getLocationType() + ". A document or a library expected");
        }
        XhiveBlobNodeIf blob = (XhiveBlobNodeIf)node;
        return new Source(blob.getContents(), publicID, systemID);
    }

    private Source getFileSystemSource(FileSystemSession session, String publicID, String systemID, URITarget target, boolean isXMLSource) throws IOException, DDSException {
        File file;
        Node node;
        StoreChild storeChild = target.getStoreChild();
        if (storeChild.isLocation()) {
            throw new IOException("System ID: " + systemID + ", public ID: " + publicID + " resolved to a file system directory. Not supported.");
        }
        if (storeChild.isContainer()) {
            file = FileSystemContainerUtil.retrieveFile((FileSystemContainer)storeChild, false, false, false);
            node = null;
        } else {
            file = null;
            node = FileSystemXMLUtil.retrieveNode((XMLNode)storeChild);
        }
        if (isXMLSource) {
            if (node != null) {
                return new Source(node, publicID, systemID);
            }
            if (file != null) {
                return new Source((InputStream)new FileInputStream(file), publicID, systemID);
            }
        } else {
            if (node != null) {
                throw new IOException("System ID: " + systemID + ", public ID: " + publicID + " resolved to a DOM node in 'binary mode'. Not supported.");
            }
            if (file != null) {
                return new Source((InputStream)new FileInputStream(file), publicID, systemID);
            }
        }
        throw new IOException("System ID: " + systemID + ", public ID: " + publicID + " cannot be resolved.");
    }
}

