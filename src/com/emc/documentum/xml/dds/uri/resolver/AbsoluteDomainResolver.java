/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.uri.resolver;

import com.emc.documentum.xml.dds.application.Application;
import com.emc.documentum.xml.dds.persistence.Store;
import com.emc.documentum.xml.dds.persistence.XMLNode;
import com.emc.documentum.xml.dds.structure.DDSDataSet;
import com.emc.documentum.xml.dds.structure.DDSLocale;
import com.emc.documentum.xml.dds.structure.Structure;
import com.emc.documentum.xml.dds.structure.internal.RootStructure;
import com.emc.documentum.xml.dds.uri.DDSURI;
import com.emc.documentum.xml.dds.uri.exception.DDSURIException;
import com.emc.documentum.xml.dds.uri.resolver.DDSURIResolver;
import com.emc.documentum.xml.dds.user.User;
import com.emc.documentum.xml.dds.util.internal.StringUtils;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AbsoluteDomainResolver
extends DDSURIResolver {
    public static final String DOMAIN_ABSOLUTE = "absolute";

    public AbsoluteDomainResolver(Application application) {
        super(application);
    }

    @Override
    protected DDSURI generateURI(List<String> locationPathComponents, String containerName, Store locationStore) throws DDSURIException {
        String domainSpecificPart = this.createDomainSpecificPartString(locationPathComponents, 0, containerName);
        Store store = this.checkStore(locationStore);
        return this.createAbsoluteDDSURI(store.getAlias(), domainSpecificPart);
    }

    @Override
    protected DDSURI generateURIFromNode(XMLNode xmlNode) throws DDSURIException {
        String nodePath = xmlNode.getCanonicalPath();
        if (nodePath == null) {
            return null;
        }
        Store store = this.checkStore(xmlNode.getStore());
        return this.createAbsoluteDDSURI(store.getAlias(), nodePath);
    }

    private DDSURI createAbsoluteDDSURI(String storeAlias, String domainSpecificPart) throws DDSURIException {
        HashMap<String, String> domainAttributes = new HashMap<String, String>();
        domainAttributes.put("DOMAIN", "absolute");
        domainAttributes.put("STORE", storeAlias);
        return new DDSURI(domainAttributes, domainSpecificPart);
    }

    @Override
    protected DDSURIResolver.TargetDomain getTargetDomain(DDSURI uri) throws DDSURIException {
        String domain = uri.getAttribute("DOMAIN");
        if (domain == null) {
            domain = "absolute";
        }
        if (!domain.equals("absolute")) {
            return super.getTargetDomain(uri);
        }
        String storeAlias = uri.getAttribute("STORE");
        String dataSetAlias = uri.getAttribute("DATASET");
        String localeName = uri.getAttribute("LOCALE");
        DDSDataSet dataSet = null;
        DDSLocale locale = null;
        Store store = null;
        store = StringUtils.isEmpty(storeAlias) ? this.getApplication().getMainStore() : this.getApplication().getStore(storeAlias);
        if (store == null) {
            throw new DDSURIException("Unable to determine store, alias: " + storeAlias + ", application: " + this.getApplication().getName());
        }
        if (!StringUtils.isEmpty(dataSetAlias)) {
            throw new DDSURIException("Absolute domain uri does not support dataSet Alias: " + dataSetAlias);
        }
        if (!StringUtils.isEmpty(localeName)) {
            throw new DDSURIException("Absolute domain uri does not support locale: " + localeName);
        }
        return new DDSURIResolver.TargetDomain(domain, store, dataSet, locale);
    }

    @Override
    protected Structure getContext(DDSURIResolver.TargetDomain targetDomain, User user) throws DDSURIException {
        return targetDomain.getStore().getRootStructure();
    }
}

