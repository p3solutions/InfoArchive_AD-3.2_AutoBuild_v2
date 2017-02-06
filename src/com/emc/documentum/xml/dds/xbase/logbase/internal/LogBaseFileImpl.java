/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  com.xhive.core.interfaces.XhiveDatabaseIf
 *  com.xhive.core.interfaces.XhiveSessionIf
 *  com.xhive.dom.interfaces.XhiveDocumentIf
 *  com.xhive.dom.interfaces.XhiveLSParserIf
 *  com.xhive.dom.interfaces.XhiveLibraryChildIf
 *  com.xhive.dom.interfaces.XhiveLibraryIf
 *  com.xhive.error.XhiveDeadlockException
 */
package com.emc.documentum.xml.dds.xbase.logbase.internal;

import com.emc.documentum.xml.dds.DDS;
import com.emc.documentum.xml.dds.application.Application;
import com.emc.documentum.xml.dds.logging.LogCenter;
import com.emc.documentum.xml.dds.operation.exception.OperationException;
import com.emc.documentum.xml.dds.operation.framework.Operation;
import com.emc.documentum.xml.dds.operation.library.persistence.CreateLocationOperation;
import com.emc.documentum.xml.dds.operation.library.persistence.ExistsStoreChildOperation;
import com.emc.documentum.xml.dds.operation.library.persistence.PersistOperation;
import com.emc.documentum.xml.dds.persistence.Container;
import com.emc.documentum.xml.dds.persistence.ContentDescriptor;
import com.emc.documentum.xml.dds.persistence.Data;
import com.emc.documentum.xml.dds.persistence.Location;
import com.emc.documentum.xml.dds.persistence.LocationOptions;
import com.emc.documentum.xml.dds.persistence.Session;
import com.emc.documentum.xml.dds.persistence.StoreChild;
import com.emc.documentum.xml.dds.persistence.content.XMLContentDescriptor;
import com.emc.documentum.xml.dds.persistence.data.StringData;
import com.emc.documentum.xml.dds.persistence.exception.DeadlockException;
import com.emc.documentum.xml.dds.persistence.exception.StoreSpecificException;
import com.emc.documentum.xml.dds.persistence.xdb.internal.XDBSession;
import com.emc.documentum.xml.dds.persistence.xdb.internal.XDBStore;
import com.emc.documentum.xml.dds.persistence.xdb.internal.XDBStoreUser;
import com.emc.documentum.xml.dds.user.User;
import com.emc.documentum.xml.dds.xbase.XBaseEntry;
import com.emc.documentum.xml.dds.xbase.XBaseFile;
import com.emc.documentum.xml.dds.xbase.logbase.LogBaseFile;
import com.xhive.core.interfaces.XhiveDatabaseIf;
import com.xhive.core.interfaces.XhiveSessionIf;
import com.xhive.dom.interfaces.XhiveDocumentIf;
import com.xhive.dom.interfaces.XhiveLSParserIf;
import com.xhive.dom.interfaces.XhiveLibraryChildIf;
import com.xhive.dom.interfaces.XhiveLibraryIf;
import com.xhive.error.XhiveDeadlockException;
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.ls.LSInput;

public final class LogBaseFileImpl
implements LogBaseFile {
    private Element currentElement;
    private Element currentRootElement;
    private final String documentName;
    private final String documentPath;
    private final String documentFullPath;
    private Container documentContainer;
    private final XDBStore xdb;

    public LogBaseFileImpl(XDBStore store, XDBStoreUser user, String documentPath, String documentName) {
        this.documentName = documentName;
        this.documentPath = documentPath;
        this.documentFullPath = documentPath + "/" + documentName;
        this.xdb = store;
    }

    @Override
    public void create(boolean replace) {
        Application app = DDS.getApplication();
        User appUsr = app.getApplicationUser();
        this.documentContainer = this.xdb.getContainer(this.documentPath, this.documentName);
        try {
            if (((Boolean)app.execute(appUsr, new ExistsStoreChildOperation(this.documentContainer))).booleanValue() && !replace) {
                return;
            }
            if (!((Boolean)app.execute(appUsr, new ExistsStoreChildOperation(this.documentContainer.getLocation()))).booleanValue()) {
                app.execute(appUsr, new CreateLocationOperation(this.documentContainer.getLocation(), null, true));
            }
            app.execute(appUsr, new PersistOperation(this.documentContainer, new XMLContentDescriptor(), new StringData("<db/>"), true));
        }
        catch (OperationException oe) {
            LogCenter.exception(this, "Could not create file for DB", oe);
        }
    }

    @Override
    public void close() {
    }

    @Override
    public void clear() {
        this.create(true);
    }

    @Override
    public void store(Session session, XBaseEntry entry) throws StoreSpecificException, DeadlockException {
        try {
            entry.write(session, this);
        }
        catch (XhiveDeadlockException xde) {
            throw new DeadlockException("Deadlock occurred in XBase", (Throwable)xde);
        }
        catch (Exception e) {
            throw new StoreSpecificException("An error occurred while trying to store an entry :", e);
        }
    }

    @Override
    public void createEntry(Session session) {
        XhiveLibraryIf rootLib = ((XDBSession)session).getSession().getDatabase().getRoot();
        this.currentRootElement = ((Document)rootLib.getByPath(this.documentFullPath)).getDocumentElement();
        this.currentElement = this.currentRootElement.getOwnerDocument().createElement("entry");
    }

    @Override
    public void closeEntry(Session session) {
        this.currentRootElement.appendChild(this.currentElement);
        this.currentElement = null;
        this.currentRootElement = null;
    }

    @Override
    public void addAsNode(Session session, Node node) {
        Element rootElement = ((Document)((XDBSession)session).getSession().getDatabase().getRoot().getByPath(this.documentFullPath)).getDocumentElement();
        Node newChild = rootElement.getOwnerDocument().importNode(node, true);
        rootElement.appendChild(newChild);
    }

    @Override
    public void addAsString(Session session, String xmlFragment) {
        Element rootElement = ((Document)((XDBSession)session).getSession().getDatabase().getRoot().getByPath(this.documentFullPath)).getDocumentElement();
        XhiveLibraryIf ownerLibrary = ((XhiveDocumentIf)rootElement.getOwnerDocument()).getOwnerLibrary();
        XhiveLSParserIf parser = ownerLibrary.createLSParser();
        parser.getDomConfig().setParameter("namespaces", Boolean.TRUE);
        LSInput lsInput = ownerLibrary.createLSInput();
        lsInput.setStringData(xmlFragment);
        lsInput.setSystemId("");
        parser.parseWithContext(lsInput, rootElement, (short) 1);
    }

    @Override
    public void addPair(Session session, String name, String value) {
        Element kvElement = this.currentRootElement.getOwnerDocument().createElement(name);
        kvElement.setTextContent(value);
        this.currentElement.appendChild(kvElement);
    }

    @Override
    public void addAttribute(Session session, String name, String value) {
        this.currentElement.setAttribute(name, value);
    }

    @Override
    public void addElement(Session session, String name) {
        this.currentElement.appendChild(this.currentElement.getOwnerDocument().createElement(name));
    }

    @Override
    public void closeElement(Session session) {
        this.currentElement = (Element)this.currentElement.getParentNode();
    }

    @Override
    public void addContent(Session session, String content) {
        this.currentElement.setTextContent(content);
    }
}

