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
 *  com.xhive.error.XhiveException
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.w3c.dom.as.ASModel
 */
package com.emc.documentum.xml.dds.importer.internal;

import com.emc.documentum.xml.dds.importer.Subscription;
import com.emc.documentum.xml.dds.importer.internal.DDSDataImporter;
import com.emc.documentum.xml.dds.util.internal.FilenameUtils;
import com.emc.documentum.xml.dds.util.internal.ImportSettings;
import com.emc.documentum.xml.dds.util.internal.LibraryChildUtils;
import com.xhive.core.interfaces.XhiveDatabaseIf;
import com.xhive.core.interfaces.XhiveSessionIf;
import com.xhive.dom.interfaces.XhiveBlobNodeIf;
import com.xhive.dom.interfaces.XhiveDocumentIf;
import com.xhive.dom.interfaces.XhiveLibraryChildIf;
import com.xhive.dom.interfaces.XhiveLibraryIf;
import com.xhive.dom.interfaces.XhiveMetadataMapIf;
import com.xhive.dom.interfaces.XhiveNodeIf;
import com.xhive.error.XhiveException;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.as.ASModel;
import org.w3c.dom.ls.DOMImplementationLS;

public class DDSDataImporterImpl
implements DDSDataImporter {
    private final XhiveLibraryIf root;
    private final Subscription subscription;
    private static Log log = LogFactory.getLog(DDSDataImporterImpl.class);

    public DDSDataImporterImpl(XhiveSessionIf session, String name) {
        this.root = session.getDatabase().getRoot();
        this.subscription = Subscription.getOrCreate(name, session);
    }

    @Override
    public void addItem(InputStream input, String url, String path, String id, short type, ImportSettings importSettings) throws IOException {
        this.checkType(type);
        ImportSettings settings = importSettings;
        if (settings == null) {
            settings = LibraryChildUtils.createImportSettings((DOMImplementationLS)this.root);
        }
        String locale = settings.getLocale();
        String localFilename = this.getDatabasePath(path, locale);
        XhiveLibraryIf parentLib = LibraryChildUtils.createLibraries(FilenameUtils.getFullPathNoEndSeparator(localFilename), this.root, settings.getLibraryOptions());
        String baseName = FilenameUtils.getName(localFilename);
        String schemaId = null;
        if (type == 201) {
            LibraryChildUtils.createLibrary(parentLib, baseName, settings.getLibraryOptions());
        } else if (type == 9) {
            LibraryChildUtils.insertDocument(baseName, parentLib, input, settings, url);
            XhiveDocumentIf doc = (XhiveDocumentIf)this.root.getByPath(localFilename);
            if (doc.getActiveASModel() != null) {
                schemaId = doc.getActiveASModel().getHint();
            }
        } else if (type == 203) {
            LibraryChildUtils.insertBlob(baseName, parentLib, input);
        }
        XhiveMetadataMapIf metadata = this.root.getByPath(localFilename).getMetadata();
        this.addDDSMetadata(metadata, id, locale, path, schemaId);
    }

    protected void addDDSMetadata(XhiveMetadataMapIf metadata, String id, String locale, String contentPath, String schemaId) {
        metadata.put("dds:subscription-element", id);
        metadata.put("dds:locale", locale);
        if (contentPath != null) {
            metadata.put("dds:content-filename", contentPath);
        }
        if (schemaId != null) {
            metadata.put("dds:schema-id", schemaId);
        }
    }

    protected String getDatabasePath(String collectionPath, String locale, boolean metadata) {
        String base;
        if (collectionPath == null) {
            return null;
        }
        String collectionPathLocal = "./" + collectionPath;
        String string = base = metadata ? this.subscription.getCollectionMetadataRoot() : this.subscription.getCollectionRoot();
        if (locale != null && !"".equals(locale)) {
            base = FilenameUtils.concat(base, locale);
        }
        return FilenameUtils.concat(base, collectionPathLocal);
    }

    protected String getDatabasePath(String collectionPath, String locale) {
        return this.getDatabasePath(collectionPath, locale, false);
    }

    private void checkType(short type) {
        switch (type) {
            case 9: 
            case 201: 
            case 203: {
                return;
            }
        }
        throw new XhiveException(100, new String[]{"Valid parameters are XhiveNodeIf.DOCUMENT_NODE, LIBRARY_NODE, and BLOB_NODE"});
    }

    @Override
    public long addMetadataItem(InputStream inputStream, String path, String id, boolean isDir, ImportSettings importSettings) {
        long newId;
        XhiveLibraryChildIf doc;
        if (inputStream == null) {
            return 0;
        }
        if (path == null) {
            throw new XhiveException(2005, new String[]{"collectionPath cannot be null"});
        }
        ImportSettings settings = importSettings;
        if (settings == null) {
            settings = LibraryChildUtils.createImportSettings((DOMImplementationLS)this.root);
        }
        String locale = settings.getLocale();
        String metadataFilename = this.subscription.getItemMetadataPath(path, locale);
        if (isDir) {
            long targetID = 0;
            boolean update = false;
            XhiveLibraryChildIf collectionElementMetadata = this.subscription.getMetadataEntryBySubscriptionElement(id);
            if (collectionElementMetadata != null) {
                targetID = collectionElementMetadata.getId();
                update = true;
            }
            newId = this.addNamelessDocument(inputStream, metadataFilename, update, targetID, settings);
            doc = ((XhiveLibraryIf)this.root.getByPath(metadataFilename)).get(newId);
        } else {
            int libraryOptions = settings.getLibraryOptions();
            String parentPath = FilenameUtils.getFullPathNoEndSeparator(metadataFilename);
            String name = FilenameUtils.getName(metadataFilename);
            XhiveLibraryIf parentLib = LibraryChildUtils.createLibraries(parentPath, this.root, libraryOptions);
            newId = LibraryChildUtils.insertDocument(name, parentLib, inputStream, settings, null);
            doc = this.root.getByPath(metadataFilename);
        }
        XhiveMetadataMapIf metadata = doc.getMetadata();
        this.addDDSMetadata(metadata, id, locale, null, null);
        return newId;
    }

    private long addNamelessDocument(InputStream byteStream, String ownerPath, boolean update, long id, ImportSettings importSettings) {
        XhiveDocumentIf doc;
        if (update) {
            XhiveLibraryIf parentLib = (XhiveLibraryIf)this.root.getByPath(ownerPath);
            doc = (XhiveDocumentIf)parentLib.get(id);
        } else {
            doc = LibraryChildUtils.createDocumentLibrary(ownerPath, null, this.root, importSettings);
        }
        LibraryChildUtils.updateDocument(byteStream, doc, importSettings);
        return doc.getId();
    }

    @Override
    public void removeCompleteItem(String id) {
        this.removeItem(id);
        this.removeMetadataItem(id);
    }

    private void removeAndPrune(XhiveLibraryChildIf lib, XhiveLibraryIf collection) {
        if (lib == null) {
            return;
        }
        XhiveLibraryIf owner = lib.getOwnerLibrary();
        owner.removeChild((Node)lib);
        if (owner.hasChildNodes()) {
            return;
        }
        boolean isRootOfCollection = owner.getFullPath().equals(collection.getFullPath());
        if (isRootOfCollection) {
            return;
        }
        XhiveMetadataMapIf metadata = owner.getMetadata();
        boolean isACollectionItem = metadata.containsKey((Object)"dds:subscription-element");
        if (isACollectionItem) {
            return;
        }
        this.removeAndPrune((XhiveLibraryChildIf)owner, collection);
    }

    @Override
    public void removeItem(String id) {
        XhiveLibraryIf collection = (XhiveLibraryIf)this.root.getByPath(this.subscription.getCollectionRoot());
        XhiveLibraryChildIf lib = this.subscription.getBySubscriptionElement(id);
        this.removeAndPrune(lib, collection);
    }

    @Override
    public void removeMetadataItem(String id) {
        XhiveLibraryChildIf lib = this.subscription.getMetadataEntryBySubscriptionElement(id);
        XhiveLibraryIf collectionMetadata = (XhiveLibraryIf)this.root.getByPath(this.subscription.getCollectionMetadataRoot());
        this.removeAndPrune(lib, collectionMetadata);
    }

    @Override
    public boolean itemExists(String id) {
        return this.subscription.getBySubscriptionElement(id) != null;
    }

    @Override
    public boolean metadataItemExists(String id) {
        return this.subscription.getMetadataEntryBySubscriptionElement(id) != null;
    }

    public boolean checkContentExists(String subscriptionElement) {
        return this.subscription.getBySubscriptionElement(subscriptionElement) != null;
    }

    public boolean checkMetadataExists(String subscriptionElement) {
        return this.subscription.getMetadataEntryBySubscriptionElement(subscriptionElement) != null;
    }

    private String getSubscriptionId(String locale, String filename) {
        StringBuffer sb = new StringBuffer();
        sb.append(locale);
        if (!filename.startsWith("/")) {
            sb.append('/');
        }
        sb.append(filename);
        return sb.toString();
    }

    public void copyFallbackContent(String subscriptionElement, String fallbackLocale) {
        XhiveLibraryChildIf result = this.subscription.getBySubscriptionElement(subscriptionElement);
        if (result == null) {
            throw new XhiveException(2005, new String[0]);
        }
        String fileName = (String)result.getMetadata().get((Object)"dds:content-filename");
        String locale = (String)result.getMetadata().get((Object)"dds:locale");
        String fallbackSubscriptionElement = this.getSubscriptionId(fallbackLocale, fileName);
        XhiveLibraryChildIf current = this.subscription.getBySubscriptionElement(fallbackSubscriptionElement);
        String localFilename = this.getDatabasePath(fileName, fallbackLocale);
        XhiveLibraryIf parentLib = LibraryChildUtils.createLibraries(FilenameUtils.getFullPathNoEndSeparator(localFilename), this.root, 128);
        XhiveDocumentIf copyLibraryChild = null;
        if (result instanceof XhiveDocumentIf) {
            copyLibraryChild = (XhiveDocumentIf)LibraryChildUtils.createCopyDocument((DOMImplementation)parentLib, (Document)((XhiveDocumentIf)result));
        } else if (result instanceof XhiveBlobNodeIf) {
            try {
                copyLibraryChild = (XhiveDocumentIf) LibraryChildUtils.createCopyBlobNode(parentLib, (XhiveBlobNodeIf)result);
            }
            catch (IOException e) {
                throw new XhiveException(2, (Throwable)e, new String[0]);
            }
        } else if (result instanceof XhiveLibraryIf) {
            copyLibraryChild = (XhiveDocumentIf) parentLib.createLibrary(128);
        }
        if (copyLibraryChild != null) {
            if (current != null) {
                parentLib.replaceChild((Node)copyLibraryChild, (Node)current);
            } else {
                parentLib.appendChild((Node)copyLibraryChild);
            }
            copyLibraryChild.setName(result.getName());
            this.addDDSMetadata(copyLibraryChild.getMetadata(), fallbackSubscriptionElement, fallbackLocale, fileName, (String)result.getMetadata().get((Object)"dds:schema-id"));
            copyLibraryChild.getMetadata().put("dds:content-locale", locale);
        }
    }

    public long copyFallbackMetadata(String subscriptionElement, String fallbackLocale) {
        XhiveDocumentIf metadata = (XhiveDocumentIf)this.subscription.getMetadataEntryBySubscriptionElement(subscriptionElement);
        if (metadata != null) {
            String locale = (String)metadata.getMetadata().get((Object)"dds:locale");
            String fileName = subscriptionElement.substring(locale.length());
            String fallbackSubscriptionElement = fallbackLocale + fileName;
            XhiveLibraryChildIf current = this.subscription.getMetadataEntryBySubscriptionElement(fallbackSubscriptionElement);
            String metadataFilename = this.getDatabasePath(fileName, fallbackLocale, true);
            XhiveLibraryIf parentLib = LibraryChildUtils.createLibraries(FilenameUtils.getFullPathNoEndSeparator(metadataFilename), this.root, 128);
            XhiveLibraryChildIf copyDocument = (XhiveLibraryChildIf)LibraryChildUtils.createCopyDocument((DOMImplementation)parentLib, (Document)metadata);
            if (current != null) {
                parentLib.replaceChild((Node)copyDocument, (Node)current);
            } else {
                parentLib.appendChild((Node)copyDocument);
            }
            copyDocument.setName(metadata.getName());
            this.addDDSMetadata(copyDocument.getMetadata(), fallbackSubscriptionElement, fallbackLocale, null, null);
            return copyDocument.getId();
        }
        return -1;
    }

    protected Subscription getSubscription() {
        return this.subscription;
    }

    protected XhiveLibraryIf getRoot() {
        return this.root;
    }
}

