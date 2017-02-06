/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  com.xhive.core.interfaces.XhiveDatabaseIf
 *  com.xhive.core.interfaces.XhiveSessionIf
 *  com.xhive.dom.interfaces.XhiveLibraryChildIf
 *  com.xhive.dom.interfaces.XhiveLibraryIf
 *  com.xhive.dom.interfaces.XhiveMetadataMapIf
 *  com.xhive.dom.interfaces.XhiveNodeIf
 *  com.xhive.error.XhiveException
 *  com.xhive.util.interfaces.IterableIterator
 *  org.apache.commons.io.IOUtils
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package com.emc.documentum.xml.dds.importer;

import com.emc.documentum.xml.dds.importer.Subscription;
import com.emc.documentum.xml.dds.importer.SubscriptionIf;
import com.emc.documentum.xml.dds.importer.internal.DDSDataImporterImpl;
import com.emc.documentum.xml.dds.util.internal.FilenameUtils;
import com.emc.documentum.xml.dds.util.internal.ImportSettings;
import com.emc.documentum.xml.dds.util.internal.LSParserConfiguration;
import com.emc.documentum.xml.dds.util.internal.LibraryChildUtils;
import com.xhive.core.interfaces.XhiveDatabaseIf;
import com.xhive.core.interfaces.XhiveSessionIf;
import com.xhive.dom.interfaces.XhiveLibraryChildIf;
import com.xhive.dom.interfaces.XhiveLibraryIf;
import com.xhive.dom.interfaces.XhiveMetadataMapIf;
import com.xhive.dom.interfaces.XhiveNodeIf;
import com.xhive.error.XhiveException;
import com.xhive.util.interfaces.IterableIterator;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.Map;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.ls.DOMImplementationLS;

public class SCSSubscriptionImpl
extends DDSDataImporterImpl
implements SubscriptionIf {
    private final XhiveSessionIf session;
    private static Log log = LogFactory.getLog(SCSSubscriptionImpl.class);

    public SCSSubscriptionImpl(XhiveSessionIf session, String name) {
        super(session, name);
        this.session = session;
    }

    @Override
    public void addNamedItem(String sourceFilename, String collectionPath, String subscriptionElement, String locale, short type, boolean validate) throws IOException {
        this.addNamedItem(sourceFilename, collectionPath, subscriptionElement, type, this.getImportSettings(locale, validate));
    }

    public void addNamedItem(String sourceFilename, String contentPath, String subscriptionElement, short type, ImportSettings importSettings) throws IOException {
        FileInputStream entryIS = null;
        if (type != 201) {
            entryIS = new FileInputStream(sourceFilename);
        }
        String url = null;
        if (type == 9) {
            File fileToUpload = new File(sourceFilename);
            url = fileToUpload.toURI().toURL().toString();
        }
        this.addItem(entryIS, url, contentPath, subscriptionElement, type, importSettings);
    }

    @Override
    public long addItemXMLMetadata(String xmlContent, String collectionPath, String subscriptionElement, boolean isDir, String locale, boolean validate) throws IOException {
        return this.addItemXMLMetadata(xmlContent, collectionPath, subscriptionElement, isDir, this.getImportSettings(locale, validate));
    }

    public long addItemXMLMetadata(String xmlmetadataString, String collectionPath, String subscriptionElement, boolean isDir, ImportSettings importSettings) {
        return this.addMetadataItem(IOUtils.toInputStream((String)xmlmetadataString), collectionPath, subscriptionElement, isDir, importSettings);
    }

    @Override
    public void checkpoint() {
        if (this.session.isOpen()) {
            this.session.checkpoint();
        }
    }

    @Override
    public void commit() {
        if (this.session.isOpen()) {
            this.session.commit();
        }
    }

    @Override
    public void rollback() {
        if (this.session.isOpen()) {
            this.session.rollback();
        }
    }

    @Override
    public void terminate() {
        if (this.session.isOpen()) {
            this.session.rollback();
        }
        if (this.session.isConnected()) {
            this.session.disconnect();
        }
        if (!this.session.isTerminated()) {
            this.session.terminate();
        }
    }

    private ImportSettings getImportSettings(String locale, boolean validate) {
        ImportSettings importSettings = LibraryChildUtils.createImportSettings((DOMImplementationLS)this.session.getDatabase().getRoot());
        importSettings.setLocale(locale);
        importSettings.getDOMConfiguration().setParameter("validate", validate);
        return importSettings;
    }

    @Override
    public void addSpecialItems(String xmlContent, String collectionPath, boolean validate) {
        this.addSpecialItems(xmlContent, collectionPath, this.getImportSettings(null, validate));
    }

    public void addSpecialItems(String xmlContent, String collectionPath, ImportSettings importSettings) {
        String libName = FilenameUtils.concat(this.getSubscription().getSubscriptionRoot(), collectionPath);
        InputStream iStr = IOUtils.toInputStream((String)xmlContent);
        int libraryOptions = importSettings == null ? 128 : importSettings.getLibraryOptions();
        String parentPath = FilenameUtils.getFullPathNoEndSeparator(libName);
        String name = FilenameUtils.getName(libName);
        XhiveLibraryIf parentLib = LibraryChildUtils.createLibraries(parentPath, this.getRoot(), libraryOptions);
        LibraryChildUtils.insertDocument(name, parentLib, iStr, importSettings, null);
    }

    @Override
    public String getCollectionItemXhiveMetadata(String libraryChildPath, String key) {
        Map map = this.getCollectionItemXhiveMetadata(libraryChildPath);
        if (map == null) {
            throw new XhiveException(2005, new String[0]);
        }
        return (String)map.get(key);
    }

    @Override
    public Map getCollectionItemXhiveMetadata(String libraryChildPath) {
        XhiveLibraryChildIf lib = this.getSubscription().getBySubscriptionElement(libraryChildPath);
        if (lib == null) {
            return null;
        }
        return lib.getMetadata();
    }

    @Override
    public Map getMetadataItemXhiveMetadata(String libraryChildPath) {
        XhiveLibraryChildIf lib = this.getSubscription().getMetadataEntryBySubscriptionElement(libraryChildPath);
        if (lib == null) {
            return null;
        }
        return lib.getMetadata();
    }

    @Override
    public void setCollectionItemXhiveMetadata(String subscriptionElement, String key, String value) {
        XhiveLibraryChildIf lib = this.getSubscription().getBySubscriptionElement(subscriptionElement);
        if (lib == null) {
            throw new XhiveException(2005, new String[0]);
        }
        lib.getMetadata().put(key, value);
    }

    @Override
    public void truncateCollection() {
        String libpath = this.getSubscription().getCollectionRoot();
        this.truncate(libpath);
    }

    private void truncate(String path) {
        XhiveLibraryIf lib = (XhiveLibraryIf)this.getRoot().getByPath(path);
        IterableIterator<? extends XhiveLibraryChildIf> iter = lib.getChildren();
        for (XhiveNodeIf child : iter) {
            iter.remove();
        }
    }

    @Override
    public void truncateCollectionMetadata() {
        this.truncate(this.getSubscription().getCollectionMetadataRoot());
    }

    @Override
    public void removeNamedItem(String subscriptionElement) {
        this.removeCompleteItem(subscriptionElement);
    }

    @Override
    public void removeCollectionItem(String subscriptionElement) {
        this.removeItem(subscriptionElement);
    }
}

