/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  com.emc.documentum.xml.xproc.plugin.xdb.impl.XDBSessionManager
 *  com.emc.documentum.xml.xproc.plugin.xdb.impl.XDBTemporaryDOMImplementationLS
 *  com.xhive.core.interfaces.XhiveSessionIf
 */
package com.emc.documentum.xml.dds.xproc.internal;

import com.emc.documentum.xml.dds.application.Application;
import com.emc.documentum.xml.dds.application.StoreManager;
import com.emc.documentum.xml.dds.persistence.Session;
import com.emc.documentum.xml.dds.persistence.SessionStoreUserStrategy;
import com.emc.documentum.xml.dds.persistence.Store;
import com.emc.documentum.xml.dds.persistence.StoreType;
import com.emc.documentum.xml.dds.persistence.StoreUser;
import com.emc.documentum.xml.dds.persistence.xdb.internal.XDBSession;
import com.emc.documentum.xml.dds.user.User;
import com.emc.documentum.xml.dds.util.SessionPool;
import com.emc.documentum.xml.xproc.plugin.xdb.impl.XDBSessionManager;
import com.emc.documentum.xml.xproc.plugin.xdb.impl.XDBTemporaryDOMImplementationLS;
import com.xhive.core.interfaces.XhiveSessionIf;
import java.util.Collection;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSParser;
import org.w3c.dom.ls.LSSerializer;

public class DDSDOMImplementation
implements DOMImplementation,
DOMImplementationLS {
    private final Application application;
    private final User user;
    private final DOMImplementation defaultDOMImplementation;
    private final SessionPool sessionPool;
    private final boolean readOnly;

    public DDSDOMImplementation(Application application, User user, DOMImplementation defaultDOMImplementation, SessionPool sessionPool, boolean readOnly) {
        this.application = application;
        this.user = user;
        this.defaultDOMImplementation = defaultDOMImplementation;
        this.sessionPool = sessionPool;
        this.readOnly = readOnly;
    }

    @Override
    public Document createDocument(String namespaceURI, String qualifiedName, DocumentType doctype) {
        return this.selectTemporaryDOMImplementation().createDocument(namespaceURI, qualifiedName, doctype);
    }

    @Override
    public DocumentType createDocumentType(String qualifiedName, String publicId, String systemId) {
        return this.selectTemporaryDOMImplementation().createDocumentType(qualifiedName, publicId, systemId);
    }

    @Override
    public Object getFeature(String feature, String version) {
        return this.selectTemporaryDOMImplementation().getFeature(feature, version);
    }

    @Override
    public boolean hasFeature(String feature, String version) {
        return this.selectTemporaryDOMImplementation().hasFeature(feature, version);
    }

    @Override
    public LSInput createLSInput() {
        return ((DOMImplementationLS)((Object)this.selectTemporaryDOMImplementation())).createLSInput();
    }

    @Override
    public LSOutput createLSOutput() {
        return ((DOMImplementationLS)((Object)this.selectTemporaryDOMImplementation())).createLSOutput();
    }

    @Override
    public LSParser createLSParser(short mode, String schemaType) {
        return ((DOMImplementationLS)((Object)this.selectTemporaryDOMImplementation())).createLSParser(mode, schemaType);
    }

    @Override
    public LSSerializer createLSSerializer() {
        return ((DOMImplementationLS)((Object)this.selectTemporaryDOMImplementation())).createLSSerializer();
    }

    private DOMImplementation selectTemporaryDOMImplementation() {
        Collection<Store> stores;
        Collection<Session> pooledSessions = this.sessionPool.getReadOnlySessions();
        if (pooledSessions != null) {
            for (Session session : pooledSessions) {
                if (!session.isOpen() || session.getStoreType() != StoreType.XDB) continue;
                XhiveSessionIf xhiveSession = ((XDBSession)session).getSession();
                XDBSessionManager sessionManager = new XDBSessionManager(xhiveSession);
                return new XDBTemporaryDOMImplementationLS(sessionManager);
            }
        }
        if (!this.readOnly && (pooledSessions = this.sessionPool.getReadWriteSessions()) != null) {
            for (Session session : pooledSessions) {
                if (!session.isOpen() || session.getStoreType() != StoreType.XDB) continue;
                XhiveSessionIf xhiveSession = ((XDBSession)session).getSession();
                XDBSessionManager sessionManager = new XDBSessionManager(xhiveSession);
                return new XDBTemporaryDOMImplementationLS(sessionManager);
            }
        }
        if ((stores = this.application.getStoreManager().getStores()) != null) {
            Exception exc = null;
            for (Store store : stores) {
                StoreUser storeUser;
                SessionStoreUserStrategy strategy;
                if (store.getType() != StoreType.XDB || (storeUser = (strategy = this.application.getSessionStoreUserStrategy()).getStoreUser(this.application, this.user, store.getAlias())) == null) continue;
                try {
                    XDBSession session = (XDBSession)this.sessionPool.getSession(store, storeUser, true, this.readOnly);
                    this.sessionPool.put(store, storeUser, session);
                    session.begin();
                    XhiveSessionIf xhiveSession = session.getSession();
                    if (!xhiveSession.isOpen()) continue;
                    XDBSessionManager sessionManager = new XDBSessionManager(xhiveSession);
                    return new XDBTemporaryDOMImplementationLS(sessionManager);
                }
                catch (Exception e) {
                    exc = e;
                    continue;
                }
            }
            if (exc != null) {
                throw new RuntimeException(exc);
            }
        }
        return this.defaultDOMImplementation;
    }
}

