/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.application.internal;

import com.emc.documentum.xml.dds.application.Application;
import com.emc.documentum.xml.dds.application.XBaseManager;
import com.emc.documentum.xml.dds.configuration.Configuration;
import com.emc.documentum.xml.dds.configuration.baseline.XBaseConfiguration;
import com.emc.documentum.xml.dds.configuration.baseline.XBasesConfiguration;
import com.emc.documentum.xml.dds.logging.LogCenter;
import com.emc.documentum.xml.dds.persistence.Location;
import com.emc.documentum.xml.dds.persistence.Store;
import com.emc.documentum.xml.dds.persistence.xdb.internal.XDBStore;
import com.emc.documentum.xml.dds.persistence.xdb.internal.XDBStoreUser;
import com.emc.documentum.xml.dds.xbase.StorageStrategy;
import com.emc.documentum.xml.dds.xbase.XBase;
import com.emc.documentum.xml.dds.xbase.logbase.LogBaseConfiguration;
import com.emc.documentum.xml.dds.xbase.logbase.internal.LogBaseImpl;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XBaseManagerImpl
implements XBaseManager {
    private final Application application;
    private XBasesConfiguration configuration;
    private final Map<String, XBase> xBases = new HashMap<String, XBase>();
    private final Object mutex = new Object();

    public XBaseManagerImpl(Application application) {
        this.application = application;
    }

    @Override
    public XBasesConfiguration getConfiguration() {
        return this.configuration;
    }

    @Override
    public void setConfiguration(Configuration config) {
        this.configuration = (XBasesConfiguration)config;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean activateConfiguration() {
        LogCenter.debug(this, "Activating Configuration...");
        if (this.configuration != null) {
            Object object = this.mutex;
            synchronized (object) {
                for (XBaseConfiguration xBaseConfiguration : this.configuration.getList()) {
                    this.addXBase(xBaseConfiguration);
                }
                LogCenter.debug(this, "Configuration Activated.");
                return true;
            }
        }
        LogCenter.debug(this, "No configuration found.");
        return true;
    }

    @Override
    public boolean configure(Configuration config) {
        this.configuration = (XBasesConfiguration)config;
        return this.activateConfiguration();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XBase addXBase(XBaseConfiguration xBaseConfiguration) {
        Object object = this.mutex;
        synchronized (object) {
            if (xBaseConfiguration instanceof LogBaseConfiguration) {
                LogBaseConfiguration logBaseConfig = (LogBaseConfiguration)xBaseConfiguration;
                XDBStore store = (XDBStore)this.application.getStore(logBaseConfig.getStoreAlias());
                XDBStoreUser storeUser = store.getDefaultStoreUser();
                Location location = store.getLocation(logBaseConfig.getLocation());
                LogBaseImpl xBase = new LogBaseImpl(logBaseConfig.getXBaseId(), store, storeUser, location, logBaseConfig.getBaseName(), logBaseConfig.getStrategy());
                this.xBases.put(xBase.getId(), xBase);
                LogCenter.debug(this, "Added XBase " + xBase.getId());
                return xBase;
            }
            return null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XBase getXBase(String xBaseId) {
        Object object = this.mutex;
        synchronized (object) {
            return this.xBases.get(xBaseId);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<XBase> getXBases() {
        Object object = this.mutex;
        synchronized (object) {
            ArrayList<XBase> result = new ArrayList<XBase>();
            result.addAll(this.xBases.values());
            return result;
        }
    }
}

