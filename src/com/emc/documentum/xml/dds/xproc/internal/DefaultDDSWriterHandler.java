/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  com.emc.documentum.xml.xproc.io.Target
 *  com.emc.documentum.xml.xproc.io.XMLTarget
 *  com.emc.documentum.xml.xproc.pipeline.model.ExtensionContext
 *  com.xhive.core.interfaces.XhiveDatabaseIf
 *  com.xhive.core.interfaces.XhiveSessionIf
 *  com.xhive.dom.interfaces.XhiveBlobNodeIf
 *  com.xhive.dom.interfaces.XhiveDocumentIf
 *  com.xhive.dom.interfaces.XhiveLSParserIf
 *  com.xhive.dom.interfaces.XhiveLibraryChildIf
 *  com.xhive.dom.interfaces.XhiveLibraryIf
 *  com.xhive.dom.interfaces.XhiveNodeIf
 */
package com.emc.documentum.xml.dds.xproc.internal;

import com.emc.documentum.xml.dds.application.Application;
import com.emc.documentum.xml.dds.exception.DDSException;
import com.emc.documentum.xml.dds.persistence.Container;
import com.emc.documentum.xml.dds.persistence.Location;
import com.emc.documentum.xml.dds.persistence.Session;
import com.emc.documentum.xml.dds.persistence.SessionStoreUserStrategy;
import com.emc.documentum.xml.dds.persistence.Store;
import com.emc.documentum.xml.dds.persistence.StoreChild;
import com.emc.documentum.xml.dds.persistence.StoreType;
import com.emc.documentum.xml.dds.persistence.StoreUser;
import com.emc.documentum.xml.dds.persistence.xdb.internal.XDBSession;
import com.emc.documentum.xml.dds.uri.DDSURI;
import com.emc.documentum.xml.dds.uri.URIResolver;
import com.emc.documentum.xml.dds.uri.URITarget;
import com.emc.documentum.xml.dds.user.User;
import com.emc.documentum.xml.dds.util.SessionPool;
import com.emc.documentum.xml.dds.util.internal.DistributedByteArray;
import com.emc.documentum.xml.dds.util.internal.StringUtils;
import com.emc.documentum.xml.dds.xproc.AbstractDDSWriterHandler;
import com.emc.documentum.xml.xproc.io.Target;
import com.emc.documentum.xml.xproc.io.XMLTarget;
import com.emc.documentum.xml.xproc.pipeline.model.ExtensionContext;
import com.xhive.core.interfaces.XhiveDatabaseIf;
import com.xhive.core.interfaces.XhiveSessionIf;
import com.xhive.dom.interfaces.XhiveBlobNodeIf;
import com.xhive.dom.interfaces.XhiveDocumentIf;
import com.xhive.dom.interfaces.XhiveLSParserIf;
import com.xhive.dom.interfaces.XhiveLibraryChildIf;
import com.xhive.dom.interfaces.XhiveLibraryIf;
import com.xhive.dom.interfaces.XhiveNodeIf;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Node;
import org.w3c.dom.ls.LSInput;

public class DefaultDDSWriterHandler
extends AbstractDDSWriterHandler {
    private final boolean overwrite;
    private final boolean readOnly;

    public DefaultDDSWriterHandler(boolean readOnly, boolean overwrite) {
        this.readOnly = readOnly;
        this.overwrite = overwrite;
    }

    public Target getTarget(String publicID, String systemID, ExtensionContext extensionContext) throws IOException, DDSException {
        return this.getTarget(publicID, systemID, false, extensionContext);
    }

    public XMLTarget getXMLTarget(String publicID, String systemID, ExtensionContext extensionContext) throws IOException, DDSException {
        return (XMLTarget)this.getTarget(publicID, systemID, true, extensionContext);
    }

    private Target getTarget(String publicID, String systemID, boolean isXMLTarget, ExtensionContext extensionContext) throws IOException, DDSException {
        SessionStoreUserStrategy strategy;
        Store store;
        StoreUser storeUser;
        URITarget target;
        if (StringUtils.isEmpty(systemID) || !systemID.startsWith("dds:")) {
            return null;
        }
        Application application = this.getApplication();
        User user = this.getUser();
        SessionPool sessionPool = this.getSessionPool();
        Session session = sessionPool.getSession(store = (target = application.getDefaultURIResolver().resolveURI(DDSURI.parseURI(systemID), user)).getStore(), storeUser = (strategy = application.getSessionStoreUserStrategy()).getStoreUser(application, user, store.getAlias()), true, this.readOnly);
        if (!session.isOpen()) {
            session.begin();
        }
        if (store.getType() == StoreType.XDB) {
            return this.getXDBTarget(session, publicID, systemID, target, isXMLTarget, extensionContext);
        }
        throw new DDSException("Non-XDB targets not supported at the moment");
    }

    private Target getXDBTarget(Session session, String publicID, String systemID, URITarget target, boolean isXMLTarget, ExtensionContext extensionContext) throws IOException {
        XhiveSessionIf xhiveSession = ((XDBSession)session).getSession();
        XhiveLibraryIf rootLibrary = xhiveSession.getDatabase().getRoot();
        StoreChild storeChild = target.getStoreChild();
        if (!storeChild.isContainer()) {
            throw new IOException("System ID: " + systemID + ", public ID: " + publicID + " resolved to a Library or XMLNode - target not supported");
        }
        Container container = (Container)storeChild;
        String ownerLibraryPath = DefaultDDSWriterHandler.getXhiveLibraryPath(container.getLocation());
        XhiveLibraryChildIf libChild = rootLibrary.getByPath(ownerLibraryPath);
        if (!(libChild instanceof XhiveLibraryIf)) {
            throw new IOException("Target system ID: " + systemID + ", public ID: " + publicID + " - cannot find owner library: " + ownerLibraryPath);
        }
        XhiveLibraryIf library = (XhiveLibraryIf)libChild;
        if (isXMLTarget) {
            return new XDBXMLTarget(library, publicID, systemID, container.getName(), this.overwrite);
        }
        return new XDBTarget(library, publicID, systemID, container.getName(), this.overwrite);
    }

    private static String getXhiveLibraryPath(Location location) {
        StringBuffer buffer = new StringBuffer();
        List<String> pathComponents = location.getPathComponents();
        for (String component : pathComponents) {
            buffer.append(component).append('/');
        }
        return buffer.toString();
    }

    private static class XDBXMLTarget
    extends XDBTarget
    implements XMLTarget {
        public XDBXMLTarget(XhiveLibraryIf library, String publicID, String systemID, String name, boolean overwrite) {
            super(library, publicID, systemID, name, overwrite);
        }

        @Override
        protected void storeContent() {
            XhiveLibraryIf library = this.getLibrary();
            XhiveDocumentIf doc = library.createDocument(null, null, null);
            doc.setName(this.getName());
            library.appendChild((Node)doc);
            LSInput lsInput = library.createLSInput();
            lsInput.setPublicId(this.getPublicID());
            lsInput.setSystemId(this.getSystemID());
            lsInput.setByteStream(this.getDba().getInputStream());
            XhiveLSParserIf lsParser = library.createLSParser();
            lsParser.parseWithContext(lsInput, (Node)doc, (short) 5);
        }
    }

    private static class XDBTarget
    implements Target {
        private XhiveLibraryIf library;
        private final String name;
        private final String publicID;
        private final String systemID;
        private DistributedByteArray dba = new DistributedByteArray();
        private final boolean overwrite;

        public XDBTarget(XhiveLibraryIf library, String publicID, String systemID, String name, boolean overwrite) {
            this.publicID = publicID;
            this.systemID = systemID;
            this.library = library;
            this.name = name;
            this.overwrite = overwrite;
        }

        public OutputStream getOutputStream() {
            return this.dba == null ? null : this.dba.getOutputStream();
        }

        public void close() throws IOException {
            if (this.library != null) {
                this.checkRemoveExisting();
                this.storeContent();
            }
            this.freeLibrary();
            this.freeDistributedByteArray();
        }

        protected void storeContent() throws IOException {
            XhiveBlobNodeIf blob = this.library.createBlob();
            blob.setContents(this.dba.getInputStream());
            blob.setName(this.name);
            this.library.appendChild((Node)blob);
        }

        protected void checkRemoveExisting() throws IOException {
            XhiveLibraryChildIf existing = this.library.get(this.name);
            if (existing != null) {
                if (this.overwrite) {
                    this.library.removeChild((Node)existing);
                } else {
                    throw new IOException("Library already contains a child with name: " + this.name);
                }
            }
        }

        public String getPublicID() {
            return this.publicID;
        }

        public String getSystemID() {
            return this.systemID;
        }

        protected XhiveLibraryIf getLibrary() {
            return this.library;
        }

        protected void freeLibrary() {
            this.library = null;
        }

        protected String getName() {
            return this.name;
        }

        protected boolean isOverwrite() {
            return this.overwrite;
        }

        protected void freeDistributedByteArray() {
            this.dba = null;
        }

        DistributedByteArray getDba() {
            return this.dba;
        }
    }

}

