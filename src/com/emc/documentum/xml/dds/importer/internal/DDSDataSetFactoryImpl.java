/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  com.xhive.core.interfaces.XhiveDatabaseIf
 *  com.xhive.core.interfaces.XhiveSessionIf
 *  com.xhive.dom.interfaces.XhiveDocumentIf
 *  com.xhive.dom.interfaces.XhiveElementIf
 *  com.xhive.dom.interfaces.XhiveLibraryChildIf
 *  com.xhive.dom.interfaces.XhiveLibraryIf
 *  com.xhive.dom.interfaces.XhiveLibraryIf$LibraryState
 *  com.xhive.dom.interfaces.XhiveNodeIf
 *  com.xhive.error.XhiveException
 *  com.xhive.util.interfaces.IterableIterator
 */
package com.emc.documentum.xml.dds.importer.internal;

import com.emc.documentum.xml.dds.importer.Subscription;
import com.emc.documentum.xml.dds.importer.internal.DDSDataImporter;
import com.emc.documentum.xml.dds.importer.internal.DDSDataImporterImpl;
import com.emc.documentum.xml.dds.importer.internal.DDSDataSetFactory;
import com.xhive.core.interfaces.XhiveDatabaseIf;
import com.xhive.core.interfaces.XhiveSessionIf;
import com.xhive.dom.interfaces.XhiveDocumentIf;
import com.xhive.dom.interfaces.XhiveElementIf;
import com.xhive.dom.interfaces.XhiveLibraryChildIf;
import com.xhive.dom.interfaces.XhiveLibraryIf;
import com.xhive.dom.interfaces.XhiveNodeIf;
import com.xhive.error.XhiveException;
import com.xhive.util.interfaces.IterableIterator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

public class DDSDataSetFactoryImpl
implements DDSDataSetFactory {
    private static final String CLASS = "class";
    private static final String ID = "id";
    private static final String DATASET = "dataset";
    private static final String LOCALE_AWARE = "localeAware";
    private static final String STRUCTURE_STRATEGY = "structureStrategy";
    private static final String DEFAULT_STRATEGY = "DocumentumStructureStrategy";
    private final XhiveSessionIf session;

    public DDSDataSetFactoryImpl(XhiveSessionIf session) {
        this.session = session;
    }

    @Override
    public DDSDataImporter getDataImporter(String dataSet) {
        return new DDSDataImporterImpl(this.session, dataSet);
    }

    @Override
    public void createDataSet(String name, boolean localeAware) {
        Subscription.createSubscription(name, null, null, this.session);
        this.storeConfiguration(name, localeAware);
    }

    @Override
    public void createDataSet(String name, int libraryOptions, boolean localeAware) {
        Subscription.createSubscription(name, null, null, this.session, libraryOptions);
        this.storeConfiguration(name, localeAware);
    }

    private void append(XhiveDocumentIf doc, Element parent, String elemName, String value) {
        XhiveElementIf child = doc.createElementNS(null, elemName);
        parent.appendChild((Node)child);
        child.appendChild(doc.createTextNode(value));
    }

    private void append(XhiveDocumentIf doc, Element parent, String elemName, String attrName, String attrValue) {
        XhiveElementIf child = doc.createElementNS(null, elemName);
        parent.appendChild((Node)child);
        child.setAttributeNS(null, attrName, attrValue);
    }

    private void storeConfiguration(String dataSet, boolean localeAware) {
        String docName = dataSet + ".xml";
        XhiveLibraryIf dataLib = (XhiveLibraryIf)this.session.getDatabase().getRoot().getByPath("/DATA");
        XhiveDocumentIf doc = (XhiveDocumentIf)dataLib.get(docName);
        if (doc != null) {
            dataLib.removeChild((Node)doc);
        }
        doc = dataLib.createDocument(null, "dataset", null);
        XhiveElementIf docElem = doc.getDocumentElement();
        this.append(doc, (Element)docElem, "structureStrategy", "class", "DocumentumStructureStrategy");
        this.append(doc, (Element)docElem, "localeAware", Boolean.toString(localeAware));
        this.append(doc, (Element)docElem, "id", dataSet);
        doc.setName(docName);
        dataLib.appendChild((Node)doc);
    }

    @Override
    public void deleteDataSet(String name) {
        XhiveLibraryIf root = this.session.getDatabase().getRoot();
        if (!this.isDataSet(name)) {
            throw new XhiveException(2005, new String[0]);
        }
        XhiveLibraryIf lib = (XhiveLibraryIf)root.getByPath("/DATA");
        XhiveLibraryChildIf node = lib.get(name);
        lib.removeChild((Node)node);
    }

    @Override
    public void deleteDataSet(String name, boolean localeAware) {
        XhiveLibraryIf root = this.session.getDatabase().getRoot();
        if (!this.isDataSet(name)) {
            throw new XhiveException(2005, new String[0]);
        }
        XhiveLibraryIf lib = (XhiveLibraryIf)root.getByPath("/DATA");
        XhiveLibraryIf node = (XhiveLibraryIf)lib.get(name);
        XhiveLibraryIf dataNode = (XhiveLibraryIf)node.get("Collection");
        XhiveLibraryIf metadataNode = (XhiveLibraryIf)node.get("CollectionMetadata");
        if (dataNode != null) {
            this.detachChildren(dataNode, localeAware);
        }
        if (metadataNode != null) {
            this.detachChildren(metadataNode, localeAware);
        }
        lib.removeChild((Node)node);
    }

    private void detachChildren(XhiveLibraryIf lib, boolean localeAware) {
        if (localeAware) {
            for (XhiveLibraryChildIf libChild = lib.getFirstChild(); libChild != null; libChild = libChild.getNextSibling()) {
                if (!(libChild instanceof XhiveLibraryIf)) continue;
                this.detachLibraryChildren((XhiveLibraryIf)libChild);
            }
        } else {
            this.detachLibraryChildren(lib);
        }
    }

    private void detachLibraryChildren(XhiveLibraryIf library) {
        XhiveLibraryChildIf libChild = library.getFirstChild();
        while (libChild != null) {
            boolean detached = false;
            if (libChild instanceof XhiveLibraryIf && ((XhiveLibraryIf)libChild).isDetachable()) {
                XhiveLibraryChildIf nextLibChild = libChild.getNextSibling();
                ((XhiveLibraryIf)libChild).setState(XhiveLibraryIf.LibraryState.READ_ONLY);
                ((XhiveLibraryIf)libChild).detach();
                libChild = nextLibChild;
                detached = true;
            }
            if (detached) continue;
            libChild = libChild.getNextSibling();
        }
    }

    @Override
    public List<String> getDataSetNames() {
        ArrayList<String> sList = new ArrayList<String>();
        XhiveLibraryIf root = this.session.getDatabase().getRoot();
        XhiveLibraryIf lib = (XhiveLibraryIf)root.getByPath("/DATA");
        if (lib == null) {
            return sList;
        }
        for (XhiveNodeIf node : lib.getChildren()) {
            if (!Subscription.subscriptionExists(node)) continue;
            String name = ((XhiveLibraryIf)node).getName();
            sList.add(name);
        }
        return sList;
    }

    private boolean isDataSet(String name) {
        return Subscription.subscriptionExists(name, this.session.getDatabase().getRoot());
    }
}

