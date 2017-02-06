/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  com.xhive.core.interfaces.XhiveDatabaseIf
 *  com.xhive.core.interfaces.XhiveSessionIf
 *  com.xhive.dom.interfaces.XhiveLibraryChildIf
 *  com.xhive.dom.interfaces.XhiveLibraryIf
 *  com.xhive.dom.interfaces.XhiveMetadataEntryIf
 *  com.xhive.dom.interfaces.XhiveMetadataMapIf
 *  com.xhive.dom.interfaces.XhiveNodeIf
 *  com.xhive.error.XhiveException
 *  com.xhive.index.interfaces.XhiveIndexIf
 *  com.xhive.index.interfaces.XhiveIndexListIf
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package com.emc.documentum.xml.dds.importer;

import com.emc.documentum.xml.dds.util.internal.FilenameUtils;
import com.emc.documentum.xml.dds.util.internal.LibraryChildUtils;
import com.xhive.core.interfaces.XhiveDatabaseIf;
import com.xhive.core.interfaces.XhiveSessionIf;
import com.xhive.dom.interfaces.XhiveLibraryChildIf;
import com.xhive.dom.interfaces.XhiveLibraryIf;
import com.xhive.dom.interfaces.XhiveMetadataEntryIf;
import com.xhive.dom.interfaces.XhiveMetadataMapIf;
import com.xhive.dom.interfaces.XhiveNodeIf;
import com.xhive.error.XhiveException;
import com.xhive.index.interfaces.XhiveIndexIf;
import com.xhive.index.interfaces.XhiveIndexListIf;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Subscription {
    private static Log log = LogFactory.getLog(Subscription.class);
    public static final String DATA_LIBRARY = "/DATA";
    private static final String STATE_INITIAL = "ICE-INITIAL";
    private static final String STATE_ANY = "ICE-ANY";
    private static final String KEY_STATE = "state";
    private static final String KEY_COLLECTION = "syndicator_address";
    private static final String KEY_COLLECTION_METADATA = "collection metadata path";
    public static final String SUBSCRIPTION_ELEMENT_INDEX = "(DDS Internal Index) Subscription-element";
    public static final String SUBSCRIPTION_LOCALES_INDEX = "(DDS Internal Index) Locale values";
    private final XhiveLibraryIf subscriptionLibrary;
    private final String subscriptionPath;
    private final String collectionPath;
    private final String metadataPath;
    private final XhiveMetadataMapIf metadata;
    private XhiveIndexIf index;
    private XhiveIndexIf metadataIndex;

    public Subscription(String name, XhiveSessionIf session) {
        XhiveLibraryIf root = session.getDatabase().getRoot();
        this.subscriptionPath = FilenameUtils.concat("/DATA", name);
        this.subscriptionLibrary = (XhiveLibraryIf)root.getByPath(this.subscriptionPath);
        this.metadata = this.subscriptionLibrary.getMetadata();
        this.collectionPath = (String)this.metadata.get((Object)"syndicator_address");
        this.metadataPath = (String)this.metadata.get((Object)"collection metadata path");
        XhiveIndexListIf idxList = root.getByPath(this.collectionPath).getIndexList();
        this.index = idxList.getIndex("(DDS Internal Index) Subscription-element");
        if (this.index == null) {
            Subscription.createCollectionIndexes(idxList);
            this.index = idxList.getIndex("(DDS Internal Index) Subscription-element");
        }
        idxList = root.getByPath(this.metadataPath).getIndexList();
        this.metadataIndex = idxList.getIndex("(DDS Internal Index) Subscription-element");
        if (this.metadataIndex == null) {
            Subscription.createCollectionIndexes(idxList);
            this.metadataIndex = idxList.getIndex("(DDS Internal Index) Subscription-element");
        }
    }

    public void setState(String state) {
        if (state == null || "".equals(state)) {
            throw new RuntimeException("State cannot be set to null, or an empty String value");
        }
        this.metadata.put("state", state);
    }

    public String getState() {
        return (String)this.metadata.get((Object)"state");
    }

    @Deprecated
    public boolean stateIsConsistent(String state) {
        if (this.getState().equals(state) || "ICE-ANY".equals(state)) {
            return true;
        }
        log.warn((Object)state);
        return false;
    }

    public String getCollectionRoot() {
        return this.collectionPath;
    }

    public String getCollectionMetadataRoot() {
        return this.metadataPath;
    }

    public String getSubscriptionRoot() {
        return this.subscriptionPath;
    }

    public static void createSubscription(String subscriptionName, String collectionPath, String collectionMetadataPath, XhiveSessionIf session, int libraryOptions) {
        String collectionPath1 = collectionPath;
        String collectionMetadataPath1 = collectionMetadataPath;
        if (subscriptionName == null) {
            throw new RuntimeException("The subscription must have a name");
        }
        String subscriptionName1 = FilenameUtils.concat("/DATA", subscriptionName);
        if (collectionPath == null) {
            collectionPath1 = FilenameUtils.concat(subscriptionName1, "Collection");
        }
        if (collectionMetadataPath == null) {
            collectionMetadataPath1 = FilenameUtils.concat(subscriptionName1, "CollectionMetadata");
        }
        collectionMetadataPath1 = FilenameUtils.concat("/", collectionMetadataPath1);
        collectionPath1 = FilenameUtils.concat("/", collectionPath1);
        Subscription.checkCollectionPathConsistency(collectionPath1, collectionMetadataPath1);
        XhiveLibraryIf root = session.getDatabase().getRoot();
        if (Subscription.subscriptionExists(subscriptionName1, root)) {
            throw new XhiveException(106, new String[]{"There is a subscription with this name already"});
        }
        XhiveLibraryIf syndLib = LibraryChildUtils.createLibraries(subscriptionName1, root, libraryOptions);
        XhiveMetadataMapIf metadata = syndLib.getMetadata();
        metadata.put("state", "ICE-INITIAL");
        metadata.put("syndicator_address", collectionPath1);
        metadata.put("collection metadata path", collectionMetadataPath1);
        Subscription.createCollectionLibrary(root, collectionPath1, libraryOptions);
        Subscription.createCollectionMetadataLibrary(root, collectionMetadataPath1, subscriptionName1, libraryOptions);
    }

    public static void createSubscription(String subscriptionName, String collectionPath, String collectionMetadataPath, XhiveSessionIf session) {
        Subscription.createSubscription(subscriptionName, collectionPath, collectionMetadataPath, session, 128);
    }

    private static void checkCollectionPathConsistency(String collectionPath2, String collectionMetadataPath) {
        String collectionMetadata = FilenameUtils.normalizeNoEndSeparator(collectionMetadataPath);
        String collection = FilenameUtils.normalizeNoEndSeparator(collectionPath2);
        if (collection.equals(collectionMetadata)) {
            throw new RuntimeException("Collection and CollectionMetadata paths are the same");
        }
        if (collectionMetadata.startsWith(collection + "/")) {
            throw new RuntimeException("CollectionMetadata cannot be sublibrary of Collection");
        }
        if (collection.startsWith(collectionMetadata + "/")) {
            throw new RuntimeException("Collection cannot be sublibrary of CollectionMetadata");
        }
    }

    private static void createCollectionMetadataLibrary(XhiveLibraryIf root, String collectionMetadata, String subscriptionName, int libraryOptions) {
        LibraryChildUtils.createLibraries(collectionMetadata, root, libraryOptions);
        XhiveLibraryChildIf mLib = root.getByPath(collectionMetadata);
        Subscription.addCollectionMetadataIndexes(mLib, root, subscriptionName);
    }

    private static void addCollectionMetadataIndexes(XhiveLibraryChildIf collectionMetadataLib, XhiveLibraryIf root, String subscriptionName) {
        XhiveIndexListIf idxList = collectionMetadataLib.getIndexList();
        Subscription.createCollectionIndexes(idxList);
    }

    private static void createCollectionLibrary(XhiveLibraryIf root, String collection, int libraryOptions) {
        LibraryChildUtils.createLibraries(collection, root, libraryOptions);
        XhiveLibraryChildIf lib = root.getByPath(collection);
        XhiveIndexListIf idxList = lib.getIndexList();
        Subscription.createCollectionIndexes(idxList);
    }

    private static void createCollectionIndexes(XhiveIndexListIf idxList) {
        if (idxList.getIndex("(DDS Internal Index) Subscription-element") == null) {
            idxList.addMetadataValueIndex("(DDS Internal Index) Subscription-element", "dds:subscription-element", 16);
        }
        if (idxList.getIndex("(DDS Internal Index) Locale values") == null) {
            idxList.addMetadataValueIndex("(DDS Internal Index) Locale values", "dds:locale", 16);
        }
    }

    public static boolean subscriptionExists(String syndicatorName, XhiveLibraryIf root) {
        String subscriptionPath = FilenameUtils.concat("/DATA", syndicatorName);
        XhiveLibraryChildIf sLib = root.getByPath(subscriptionPath);
        return Subscription.subscriptionExists((XhiveNodeIf)sLib);
    }

    public static boolean subscriptionExists(XhiveNodeIf sLib0) {
        if (sLib0 == null) {
            return false;
        }
        if (!(sLib0 instanceof XhiveLibraryIf)) {
            return false;
        }
        XhiveLibraryIf sLib = (XhiveLibraryIf)sLib0;
        XhiveMetadataMapIf metadata0 = sLib.getMetadata();
        XhiveMetadataEntryIf entry = metadata0.getEntry("syndicator_address");
        if (entry == null) {
            return false;
        }
        if (sLib.getByPath((String)entry.getValue()) == null) {
            return false;
        }
        entry = metadata0.getEntry("collection metadata path");
        if (entry == null) {
            return false;
        }
        if (sLib.getByPath((String)entry.getValue()) == null) {
            return false;
        }
        return true;
    }

    public static Subscription getOrCreate(String subscriptionName, XhiveSessionIf session) {
        if (!Subscription.subscriptionExists(subscriptionName, session.getDatabase().getRoot())) {
            Subscription.createSubscription(subscriptionName, null, null, session);
        }
        return new Subscription(subscriptionName, session);
    }

    public XhiveLibraryChildIf getBySubscriptionElement(String subscriptionElement) {
        return (XhiveLibraryChildIf)this.index.getNodeByKey((Object)subscriptionElement);
    }

    public XhiveLibraryChildIf getMetadataEntryBySubscriptionElement(String subscriptionElement) {
        return (XhiveLibraryChildIf)this.metadataIndex.getNodeByKey((Object)subscriptionElement);
    }

    public String getItemMetadataPath(String filename, String locale) {
        if (locale == null) {
            return FilenameUtils.concat(this.getCollectionMetadataRoot(), filename);
        }
        String base = FilenameUtils.concat(this.getCollectionMetadataRoot(), locale);
        return FilenameUtils.concat(base, filename);
    }
}

