/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.configuration.baseline;

import com.emc.documentum.xml.dds.configuration.Configuration;
import com.emc.documentum.xml.dds.configuration.baseline.security.JCESettings;
import com.emc.documentum.xml.dds.configuration.persistence.StoreConfiguration;
import com.emc.documentum.xml.dds.uri.URIResolver;

public class Bootstrap
implements Configuration {
    public static final String STORECONFIG = "Store";
    public static final String SERVICECONFIG = "Service";
    public static final String STRUCTURECONFIG = "Structure";
    public static final String XBASECONFIG = "XBase";
    private String name;
    private StoreConfiguration mainStore;
    private URIResolver defaultURIResolver;
    private String publicKeyPath;
    private String privateKeyPath;
    private JCESettings jceSettings;
    private boolean forceCreate;

    public String getName() {
        return this.name == null ? null : this.name.trim();
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPublicKeyPath() {
        return this.publicKeyPath == null ? null : this.publicKeyPath.trim();
    }

    public void setPublicKeyPath(String publicKeyPath) {
        this.publicKeyPath = publicKeyPath;
    }

    public String getPrivateKeyPath() {
        return this.privateKeyPath == null ? null : this.privateKeyPath.trim();
    }

    public void setPrivateKeyPath(String privateKeyPath) {
        this.privateKeyPath = privateKeyPath;
    }

    public JCESettings getJCESettings() {
        if (this.jceSettings == null) {
            this.jceSettings = new JCESettings();
        }
        return this.jceSettings;
    }

    public void setJCESettings(JCESettings settings) {
        this.jceSettings = settings;
    }

    public StoreConfiguration getMainStore() {
        return this.mainStore;
    }

    public void setMainStore(StoreConfiguration mainStore) {
        this.mainStore = mainStore;
    }

    public URIResolver getDefaultURIResolver() {
        return this.defaultURIResolver;
    }

    public void setDefaultURIResolver(URIResolver resolver) {
        this.defaultURIResolver = resolver;
    }

    public boolean getForceCreate() {
        return this.forceCreate;
    }

    public void setForceCreate(boolean forceCreate) {
        this.forceCreate = forceCreate;
    }
}

