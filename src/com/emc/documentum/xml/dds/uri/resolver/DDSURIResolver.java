/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.uri.resolver;

import com.emc.documentum.xml.dds.application.Application;
import com.emc.documentum.xml.dds.application.StoreManager;
import com.emc.documentum.xml.dds.application.StructureManager;
import com.emc.documentum.xml.dds.persistence.Container;
import com.emc.documentum.xml.dds.persistence.Location;
import com.emc.documentum.xml.dds.persistence.Store;
import com.emc.documentum.xml.dds.persistence.StoreChild;
import com.emc.documentum.xml.dds.persistence.XMLNode;
import com.emc.documentum.xml.dds.structure.DDSDataSet;
import com.emc.documentum.xml.dds.structure.DDSLocale;
import com.emc.documentum.xml.dds.structure.Structure;
import com.emc.documentum.xml.dds.structure.StructureStrategy;
import com.emc.documentum.xml.dds.structure.StructureStrategyType;
import com.emc.documentum.xml.dds.structure.internal.ApplicationStructure;
import com.emc.documentum.xml.dds.structure.internal.MetadataStructure;
import com.emc.documentum.xml.dds.structure.internal.ResourceStructure;
import com.emc.documentum.xml.dds.structure.internal.RootStructure;
import com.emc.documentum.xml.dds.structure.internal.UserStructure;
import com.emc.documentum.xml.dds.structure.strategy.DDSStructureStrategyType;
import com.emc.documentum.xml.dds.structure.strategy.DocumentumStructureStrategy;
import com.emc.documentum.xml.dds.uri.DDSURI;
import com.emc.documentum.xml.dds.uri.URIResolver;
import com.emc.documentum.xml.dds.uri.URITarget;
import com.emc.documentum.xml.dds.uri.exception.DDSURIException;
import com.emc.documentum.xml.dds.user.User;
import com.emc.documentum.xml.dds.util.internal.StringUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

public class DDSURIResolver
implements URIResolver {
    public static final String DOMAIN_DATA = "data";
    public static final String DOMAIN_RESOURCE = "resource";
    public static final String DOMAIN_USER = "user";
    public static final String DOMAIN_DOCUMENTUM_METADATA = "documentum-metadata";
    public static final String ATTRIBUTE_DOMAIN = "DOMAIN";
    public static final String ATTRIBUTE_STORE = "STORE";
    public static final String ATTRIBUTE_DATASET = "DATASET";
    public static final String ATTRIBUTE_LOCALE = "LOCALE";
    private static final char FORWARD_SLASH_CHAR = '/';
    private static final String FORWARD_SLASH_STR = String.valueOf('/');
    private static final char HASH_CHAR = '#';
    private Application application;

    @Override
    public void setApplication(Application application) {
        this.application = application;
    }

    public Application getApplication() {
        return this.application;
    }

    public DDSURIResolver(Application application) {
        Objects.requireNonNull(application, "<null> application");
        this.application = application;
    }

    @Override
    public URITarget resolveURI(String uri, User user) throws DDSURIException {
        Objects.requireNonNull(uri, "<null> uri");
        return this.resolveURI(DDSURI.parseURI(uri), user);
    }

    @Override
    public URITarget resolveURI(DDSURI uri, User user) throws DDSURIException {
        Objects.requireNonNull(uri, "<null> uri");
        return this.resolveURIInternal(uri, user);
    }

    @Override
    public DDSURI generateURI(StoreChild storeChild) throws DDSURIException {
        Objects.requireNonNull(storeChild, "<null> storeChild");
        if (storeChild.isLocation()) {
            return this.generateURI(((Location)storeChild).getPathComponents(), null, storeChild.getStore());
        }
        if (storeChild.isContainer()) {
            return this.generateURI(((Container)storeChild).getLocation(), (Container)storeChild);
        }
        if (((XMLNode)storeChild).representsLocation()) {
            return this.generateURI(((XMLNode)storeChild).asLocation().getPathComponents(), null, storeChild.getStore());
        }
        if (((XMLNode)storeChild).representsContainer()) {
            return this.generateURI(((XMLNode)storeChild).asContainer().getLocation(), ((XMLNode)storeChild).asContainer());
        }
        return this.generateURIFromNode((XMLNode)storeChild);
    }

    @Override
    public DDSURI generateURI(Location location, Container container) throws DDSURIException {
        Objects.requireNonNull(location, "<null> location");
        Objects.requireNonNull(container, "<null> container");
        return this.generateURI(location.getPathComponents(), container.getName(), location.getStore());
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    protected DDSURI generateURI(List<String> locationPathComponents, String containerName, Store locationStore) throws DDSURIException {
        int size = locationPathComponents == null ? 0 : locationPathComponents.size();
        String domain = null;
        Store store = this.checkStore(locationStore);
        DDSDataSet dataSet = null;
        Structure locale = null;
        int pathIndex = 0;
        boolean found = false;
        String appLibraryName = DDSURIResolver.getLibraryNameSafe("APPLICATIONS");
        String dataLibraryName = DDSURIResolver.getLibraryNameSafe("DATA");
        if (size > 1) {
            if (appLibraryName.equals(locationPathComponents.get(0)) && this.application.getName().equals(locationPathComponents.get(1))) {
                if (size > 2) {
                    if ("resources".equals(locationPathComponents.get(2))) {
                        domain = "resource";
                        pathIndex = 3;
                        found = true;
                    } else if (size > 3 && "users".equals(locationPathComponents.get(2)) && locationPathComponents.get(3) != null) {
                        domain = "user";
                        pathIndex = 4;
                        found = true;
                    }
                }
            } else if (dataLibraryName.equals(locationPathComponents.get(0))) {
                int offset;
                String dataSetName = locationPathComponents.get(1);
                dataSet = this.findDataSet(dataSetName);
                if (dataSet == null) {
                    throw new DDSURIException("Unknown data set name: " + dataSetName);
                }
                StructureStrategy structureStrategy = dataSet.getStructureStrategy();
                int n = offset = structureStrategy.getType() == DDSStructureStrategyType.DOCUMENTUM ? 2 : 1;
                if (size > offset) {
                    boolean localeAware;
                    if (structureStrategy.getType() == DDSStructureStrategyType.DOCUMENTUM) {
                        String locationName = locationPathComponents.get(offset);
                        if ("Collection".equals(locationName)) {
                            domain = "data";
                        } else {
                            if (!"CollectionMetadata".equals(locationName)) throw new DDSURIException("Failed to generate a URI based on the Documentum structure strategy (data set name must be followed by 'Collection' or 'CollectionMetadata', got: " + locationPathComponents.get(offset) + ")");
                            domain = "documentum-metadata";
                        }
                    } else {
                        domain = "data";
                    }
                    if (localeAware = dataSet.isLocaleAware()) {
                        if (size > offset + 1) {
                            String localeName = locationPathComponents.get(offset + 1);
                            locale = this.application.getStructureManager().getLocale(dataSet, localeName);
                            if (locale == null) {
                                throw new DDSURIException("Unknown locale: " + localeName);
                            }
                            pathIndex = offset + 2;
                        }
                    } else {
                        pathIndex = offset + 1;
                    }
                    found = true;
                }
            }
        }
        if (!found) {
            throw new DDSURIException("Cannot generate URI, location: " + locationPathComponents + ", container: " + containerName + ", store: " + locationStore.getAlias());
        }
        String domainSpecificPart = this.createDomainSpecificPartString(locationPathComponents, pathIndex, containerName);
        HashMap<String, String> domainAttributes = new HashMap<String, String>();
        domainAttributes.put("DOMAIN", domain);
        if (!"resource".equals(domain) && !"user".equals(domain)) {
            domainAttributes.put("STORE", store == null ? null : store.getAlias());
        }
        domainAttributes.put("LOCALE", locale == null ? null : locale.getId());
        domainAttributes.put("DATASET", dataSet == null ? null : dataSet.getAlias());
        return new DDSURI(domainAttributes, domainSpecificPart);
    }

    private DDSDataSet findDataSet(String dataSetName) {
        List<DDSDataSet> dataSets = this.application.getStructureManager().getDataSets();
        if (dataSets != null) {
            for (DDSDataSet dataSet : dataSets) {
                if (!dataSetName.equals(dataSet.getId())) continue;
                return dataSet;
            }
        }
        return null;
    }

    protected Store checkStore(Store locationStore) throws DDSURIException {
        Collection<Store> stores = this.application.getStoreManager().getStores();
        if (stores == null || stores.isEmpty()) {
            throw new DDSURIException("Application contains no store");
        }
        if (locationStore != null) {
            boolean found = false;
            for (Store store : stores) {
                if (store != locationStore) continue;
                found = true;
                break;
            }
            if (found) {
                return locationStore;
            }
            throw new DDSURIException("Cannot generate URI for location store: " + locationStore.getAlias() + ". The store is not registered with the application.");
        }
        if (stores.size() == 1) {
            return stores.iterator().next();
        }
        throw new DDSURIException("Cannot determine store for path");
    }

    protected DDSURI generateURIFromNode(XMLNode xmlNode) throws DDSURIException {
        String[] pathComponentsArray;
        int lastSeparatorIndex;
        String nodePath = xmlNode.getCanonicalPath();
        if (nodePath == null) {
            return null;
        }
        Store store = xmlNode.getStore();
        if (store == null) {
            throw new DDSURIException("XMLNode does not specify store: " + nodePath);
        }
        String path = nodePath;
        String containerPart = null;
        String fragment = null;
        int fragmentIndex = path.indexOf(35);
        if (fragmentIndex != -1) {
            fragment = path.substring(fragmentIndex);
            path = path.substring(0, fragmentIndex);
        }
        if (!path.endsWith(FORWARD_SLASH_STR) && (lastSeparatorIndex = path.lastIndexOf(FORWARD_SLASH_STR)) != -1) {
            containerPart = path.substring(lastSeparatorIndex + FORWARD_SLASH_STR.length());
            path = path.substring(0, lastSeparatorIndex);
        }
        if (fragment != null) {
            containerPart = containerPart == null ? fragment : containerPart + fragment;
        }
        ArrayList<String> pathComponents = new ArrayList<String>();
        for (String value : pathComponentsArray = path.split(Pattern.quote(FORWARD_SLASH_STR))) {
            if (value == null || StringUtils.isEmpty(value)) continue;
            pathComponents.add(value);
        }
        return this.generateURI(pathComponents, containerPart, store);
    }

    protected String createDomainSpecificPartString(List<String> pathComponents, int pathIndex, String containerName) {
        StringBuilder path = new StringBuilder();
        path.append('/');
        if (pathIndex != -1 && pathComponents != null) {
            for (int currentPathIndex = pathIndex; currentPathIndex < pathComponents.size(); ++currentPathIndex) {
                path.append(pathComponents.get(currentPathIndex)).append('/');
            }
        }
        if (containerName != null) {
            path.append(containerName);
        }
        return path.toString();
    }

    private URITarget resolveURIInternal(DDSURI uri, User user) throws DDSURIException {
        TargetDomain targetDomain = this.getTargetDomain(uri);
        return this.resolveTarget(targetDomain, uri.getDomainSpecificPart(), user);
    }

    private URITarget resolveTarget(TargetDomain targetDomain, String path, User user) throws DDSURIException {
        Structure context = this.getContext(targetDomain, user);
        String locationPath = this.getTranslatedLocationPath(path);
        String containerName = this.getContainerName(path);
        String fragment = this.getFragment(path);
        Location location = context.getLocation(locationPath);
        if (containerName == null) {
            return new URITargetImpl(targetDomain.getStore(), targetDomain.getDataSet(), targetDomain.getLocale(), location);
        }
        Container container = context.getContainer(locationPath, containerName);
        if (StringUtils.isEmpty(fragment)) {
            return new URITargetImpl(targetDomain.getStore(), targetDomain.getDataSet(), targetDomain.getLocale(), container);
        }
        XMLNode xmlNode = container.getXMLNode(fragment);
        return new URITargetImpl(targetDomain.getStore(), targetDomain.getDataSet(), targetDomain.getLocale(), xmlNode);
    }

    protected TargetDomain getTargetDomain(DDSURI uri) throws DDSURIException {
        String domain = uri.getAttribute("DOMAIN");
        if (domain == null) {
            domain = "data";
        }
        String storeAlias = uri.getAttribute("STORE");
        String dataSetAlias = uri.getAttribute("DATASET");
        String localeName = uri.getAttribute("LOCALE");
        DDSDataSet dataSet = null;
        Structure locale = null;
        if (!DDSURIResolver.isDomainSupported(domain)) {
            throw new DDSURIException("Domain not supported: " + domain);
        }
        if (storeAlias != null && ("resource".equals(domain) || "user".equals(domain))) {
            throw new DDSURIException("The 'STORE' attribute not allowed for domain: '" + domain + "', uri: " + uri);
        }
        if ("data".equals(domain) || "documentum-metadata".equals(domain)) {
            dataSet = StringUtils.isEmpty(dataSetAlias) ? this.application.getDefaultDataSet() : this.application.getDataSet(dataSetAlias);
            if (dataSet == null) {
                throw new DDSURIException("Unknown data set alias: " + dataSetAlias);
            }
            if (dataSet.isLocaleAware()) {
                locale = StringUtils.isEmpty(localeName) ? this.application.getDefaultLocale(dataSet.getAlias()) : this.application.getLocale(dataSet.getAlias(), localeName);
                if (locale == null) {
                    throw new DDSURIException("Unable to determine locale: " + uri);
                }
            }
        }
        Store store = null;
        store = locale != null ? locale.getRootStructure().getStore() : (dataSet != null ? dataSet.getRootStructure().getStore() : (StringUtils.isEmpty(storeAlias) ? this.application.getMainStore() : this.application.getStore(storeAlias)));
        if (store == null) {
            throw new DDSURIException("Unable to determine store, alias : " + storeAlias + ", application : " + this.application.getName());
        }
        return new TargetDomain(domain, store, dataSet, (DDSLocale)locale);
    }

    private static boolean isDomainSupported(String domain) throws DDSURIException {
        return "data".equals(domain) || "resource".equals(domain) || "user".equals(domain) || "documentum-metadata".equals(domain);
    }

    protected Structure getContext(TargetDomain targetDomain, User user) throws DDSURIException {
        String domain = targetDomain.getDomain();
        if ("data".equals(domain)) {
            DDSDataSet dataSet = targetDomain.getDataSet();
            DDSLocale locale = targetDomain.getLocale();
            if (locale == null && dataSet.isLocaleAware() && dataSet.getDefaultLocale() != null) {
                locale = dataSet.getLocale(dataSet.getDefaultLocale());
            }
            return locale != null ? locale : dataSet;
        }
        if ("documentum-metadata".equals(domain)) {
            Container foo;
            Structure metadataStructure;
            Container metadataFoo;
            Structure context;
            DDSDataSet dataSet = targetDomain.getDataSet();
            DDSLocale locale = targetDomain.getLocale();
            StructureStrategy structureStrategy = dataSet.getStructureStrategy();
            if (!(structureStrategy instanceof DocumentumStructureStrategy)) {
                throw new DDSURIException("Domain 'documentum-'metadata' supported only with DocumentumStructureStrategy");
            }
            if (locale == null && dataSet.isLocaleAware() && dataSet.getDefaultLocale() != null) {
                locale = dataSet.getLocale(dataSet.getDefaultLocale());
            }
            if (!((metadataStructure = (metadataFoo = dataSet.getMetadataContainer(foo = (context = locale != null ? locale : dataSet).getContainer(null, "foo"))).getContext()) instanceof MetadataStructure)) {
                throw new DDSURIException("MetadataStructure expected, got: " + metadataStructure);
            }
            if (locale != null && (metadataStructure = ((MetadataStructure)metadataStructure).getLocale(locale.getId())) == null) {
                throw new DDSURIException("No metadata structure found for locale: " + locale.getId());
            }
            return metadataStructure;
        }
        if ("resource".equals(domain)) {
            return this.application.getStructureManager().getApplicationStructure().getResourceStructure();
        }
        if ("user".equals(domain)) {
            if (user == null) {
                throw new DDSURIException("Domain 'user' is not addressable in user-less context");
            }
            UserStructure result = this.application.getStructureManager().getApplicationStructure().getUserStructure(user.getId());
            if (result == null) {
                result = new UserStructure(user.getId(), this.application.getStructureManager().getApplicationStructure());
            }
            return result;
        }
        throw new DDSURIException("Unsupported domain: " + domain);
    }

    private static String getLibraryNameSafe(String libraryName) {
        return libraryName.indexOf(47) == 0 ? libraryName.substring(1) : libraryName;
    }

    private String getTranslatedLocationPath(String uriPath) {
        if (!StringUtils.isEmpty(uriPath)) {
            int lastSlashIndex;
            String result = uriPath;
            int hashIndex = result.indexOf(35);
            if (hashIndex != -1) {
                result = result.substring(0, hashIndex);
            }
            if ((lastSlashIndex = result.lastIndexOf(47)) >= 0) {
                result = result.substring(0, lastSlashIndex);
            }
            while (result.indexOf(47) == 0) {
                result = result.substring(1);
            }
            while (result.length() > 0 && result.lastIndexOf(47) == result.length() - 1) {
                result = result.substring(0, result.length() - 1);
            }
            return result;
        }
        return "";
    }

    private String getContainerName(String path) {
        if (StringUtils.isEmpty(path) || path.endsWith(FORWARD_SLASH_STR)) {
            return null;
        }
        int hashIndex = path.indexOf(35);
        if (hashIndex >= 0) {
            int lastSlashIndex = path.lastIndexOf(47, hashIndex);
            return path.substring(lastSlashIndex + 1, hashIndex);
        }
        int lastSlashIndex = path.lastIndexOf(47);
        return path.substring(lastSlashIndex + 1);
    }

    private String getFragment(String path) {
        if (StringUtils.isEmpty(path) || path.indexOf(35) == -1) {
            return null;
        }
        int hashIndex = path.indexOf(35);
        return path.substring(hashIndex + 1);
    }

    private static final class URITargetImpl
    implements URITarget {
        private final Store store;
        private final DDSDataSet dataSet;
        private final DDSLocale locale;
        private final StoreChild storeChild;

        public URITargetImpl(Store store, DDSDataSet dataSet, DDSLocale locale, StoreChild storeChild) {
            this.store = store;
            this.dataSet = dataSet;
            this.locale = locale;
            this.storeChild = storeChild;
        }

        @Override
        public DDSDataSet getDataSet() {
            return this.dataSet;
        }

        @Override
        public DDSLocale getLocale() {
            return this.locale;
        }

        @Override
        public Store getStore() {
            return this.store;
        }

        @Override
        public StoreChild getStoreChild() {
            return this.storeChild;
        }
    }

    protected static final class TargetDomain {
        private final String domain;
        private final Store store;
        private final DDSDataSet dataSet;
        private final DDSLocale locale;

        public TargetDomain(String domain, Store store, DDSDataSet dataSet, DDSLocale locale) {
            this.domain = domain;
            this.store = store;
            this.dataSet = dataSet;
            this.locale = locale;
        }

        public String getDomain() {
            return this.domain;
        }

        public Store getStore() {
            return this.store;
        }

        public DDSDataSet getDataSet() {
            return this.dataSet;
        }

        public DDSLocale getLocale() {
            return this.locale;
        }
    }

}

