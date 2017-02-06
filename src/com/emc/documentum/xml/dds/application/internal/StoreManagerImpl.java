/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.application.internal;

import com.emc.documentum.xml.dds.application.Application;
import com.emc.documentum.xml.dds.application.StoreManager;
import com.emc.documentum.xml.dds.application.internal.ApplicationImpl;
import com.emc.documentum.xml.dds.configuration.Configuration;
import com.emc.documentum.xml.dds.configuration.baseline.StoresConfiguration;
import com.emc.documentum.xml.dds.configuration.persistence.StoreConfiguration;
import com.emc.documentum.xml.dds.configuration.persistence.StoreUserConfiguration;
import com.emc.documentum.xml.dds.configuration.persistence.esu.ESUStoreConfiguration;
import com.emc.documentum.xml.dds.configuration.persistence.xam.XAMStoreConfiguration;
import com.emc.documentum.xml.dds.logging.LogCenter;
import com.emc.documentum.xml.dds.operation.exception.OperationException;
import com.emc.documentum.xml.dds.operation.framework.Operation;
import com.emc.documentum.xml.dds.operation.library.persistence.ExistsStoreChildOperation;
import com.emc.documentum.xml.dds.operation.library.persistence.ListChildrenOperation;
import com.emc.documentum.xml.dds.operation.library.persistence.PersistOperation;
import com.emc.documentum.xml.dds.persistence.Container;
import com.emc.documentum.xml.dds.persistence.ContentDescriptor;
import com.emc.documentum.xml.dds.persistence.Data;
import com.emc.documentum.xml.dds.persistence.Location;
import com.emc.documentum.xml.dds.persistence.Store;
import com.emc.documentum.xml.dds.persistence.StoreChild;
import com.emc.documentum.xml.dds.persistence.StoreFactory;
import com.emc.documentum.xml.dds.persistence.StoreUser;
import com.emc.documentum.xml.dds.persistence.StoreUserFactory;
import com.emc.documentum.xml.dds.persistence.content.ObjectContentDescriptor;
import com.emc.documentum.xml.dds.persistence.data.ObjectData;
import com.emc.documentum.xml.dds.persistence.xdb.internal.ReplicatedXDBStore;
import com.emc.documentum.xml.dds.persistence.xdb.internal.XDBStore;
import com.emc.documentum.xml.dds.structure.internal.ApplicationStructure;
import com.emc.documentum.xml.dds.structure.internal.RootStructure;
import com.emc.documentum.xml.dds.structure.internal.UserStructure;
import com.emc.documentum.xml.dds.user.User;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class StoreManagerImpl
implements StoreManager {
    private final Application application;
    private Store mainStore;
    private StoresConfiguration configuration;
    private final Map<String, Store> storeMapping = new HashMap<String, Store>();

    public StoreManagerImpl(Application application) {
        this.application = application;
    }

    @Override
    public Store getStore(String alias) {
        return this.storeMapping.get(alias);
    }

    @Override
    public Collection<Store> getStores() {
        return this.storeMapping.values();
    }

    @Override
    public Store addStore(StoreConfiguration storeConfiguration) {
        return this.addStore(storeConfiguration, true);
    }

    public Store addStore(StoreConfiguration storeConfiguration, boolean persist) {
        ReplicatedXDBStore replStore;
        if (storeConfiguration == null) {
            return null;
        }
        Store store = StoreFactory.newStore(storeConfiguration);
        this.storeMapping.put(storeConfiguration.getAlias(), store);
        User applicationUser = this.application.getApplicationUser();
        if (applicationUser.getStoreUser(storeConfiguration.getAlias()) == null) {
            applicationUser.addStoreUser(storeConfiguration.getAlias(), store.getDefaultStoreUser());
            if (store instanceof ReplicatedXDBStore) {
                replStore = (ReplicatedXDBStore)store;
                applicationUser.addStoreUser(replStore.getMaster().getAlias(), store.getDefaultStoreUser());
                for (XDBStore slave : replStore.getSlaves()) {
                    applicationUser.addStoreUser(slave.getAlias(), store.getDefaultStoreUser());
                }
            }
        }
        if (store instanceof ReplicatedXDBStore) {
            replStore = (ReplicatedXDBStore)store;
            this.storeMapping.put(replStore.getMaster().getAlias(), replStore.getMaster());
            for (XDBStore slave : replStore.getSlaves()) {
                this.storeMapping.put(slave.getAlias(), slave);
            }
        }
        this.initializeStoreRoot(store);
        if (persist) {
            this.configuration.add(storeConfiguration);
            this.persistConfiguration();
        }
        return store;
    }

    @Override
    public void removeStore(String storeAlias) {
        if (this.mainStore.getAlias().equals(storeAlias)) {
            return;
        }
        Store store = this.storeMapping.remove(storeAlias);
        if (store != null) {
            if (store instanceof ReplicatedXDBStore) {
                ReplicatedXDBStore replStore = (ReplicatedXDBStore)store;
                this.storeMapping.remove(replStore.getMaster().getAlias());
                for (XDBStore slave : replStore.getSlaves()) {
                    this.storeMapping.remove(slave.getAlias());
                }
            }
            User applicationUser = this.application.getApplicationUser();
            applicationUser.removeStoreUser(storeAlias);
            List storeConfigs = this.configuration.getList();
            StoreConfiguration storeConfig = null;
            Iterator iterator = storeConfigs.iterator();
            while (storeConfig == null && iterator.hasNext()) {
                StoreConfiguration next = (StoreConfiguration)iterator.next();
                if (!next.getAlias().equals(storeAlias)) continue;
                storeConfig = next;
            }
            if (storeConfig != null) {
                this.configuration.remove(storeConfig);
                this.persistConfiguration();
            }
        }
    }

    @Override
    public void setDefaultStoreUser(String storeAlias, StoreUserConfiguration defaultStoreUserConfiguration) {
        if (!this.application.getMainStore().getAlias().equals(storeAlias)) {
            List storeConfigs = this.configuration.getList();
            StoreConfiguration storeConfig = null;
            Iterator iterator = storeConfigs.iterator();
            while (storeConfig == null && iterator.hasNext()) {
                StoreConfiguration next = (StoreConfiguration)iterator.next();
                if (!next.getAlias().equals(storeAlias)) continue;
                storeConfig = next;
            }
            if (storeConfig != null) {
                storeConfig.setDefaultStoreUser(defaultStoreUserConfiguration);
                this.persistConfiguration();
            }
        }
        StoreUser defaultStoreUser = StoreUserFactory.newStoreUser(defaultStoreUserConfiguration);
        this.getStore(storeAlias).setDefaultStoreUser(defaultStoreUser);
        User applicationUser = this.application.getApplicationUser();
        applicationUser.addStoreUser(storeAlias, defaultStoreUser);
        if (this.getStore(storeAlias) instanceof ReplicatedXDBStore) {
            ReplicatedXDBStore replStore = (ReplicatedXDBStore)this.getStore(storeAlias);
            applicationUser.addStoreUser(replStore.getMaster().getAlias(), defaultStoreUser);
            for (XDBStore slave : replStore.getSlaves()) {
                applicationUser.addStoreUser(slave.getAlias(), defaultStoreUser);
            }
        }
    }

    protected Store setMainStore(StoreConfiguration storeConfiguration) {
        if (storeConfiguration == null) {
            return null;
        }
        this.mainStore = StoreFactory.newStore(storeConfiguration);
        this.storeMapping.put(storeConfiguration.getAlias(), this.mainStore);
        return this.mainStore;
    }

    @Override
    public StoresConfiguration getConfiguration() {
        return this.configuration;
    }

    @Override
    public void setConfiguration(Configuration configuration) {
        this.configuration = (StoresConfiguration)configuration;
    }

    @Override
    public boolean activateConfiguration() {
        LogCenter.debug(this, "Activating Configuration...");
        this.initializeStoreRoot(this.mainStore);
        ArrayList<StoreConfiguration> xamStoreList = new ArrayList<StoreConfiguration>();
        ArrayList<StoreConfiguration> esuStoreList = new ArrayList<StoreConfiguration>();
        if (this.configuration != null) {
            for (StoreConfiguration storeConfiguration : this.configuration.getList()) {
                if (storeConfiguration instanceof XAMStoreConfiguration) {
                    xamStoreList.add(storeConfiguration);
                    continue;
                }
                if (storeConfiguration instanceof ESUStoreConfiguration) {
                    esuStoreList.add(storeConfiguration);
                    continue;
                }
                LogCenter.debug(this, "Adding Store : " + storeConfiguration.getAlias());
                this.addStore(storeConfiguration, false);
            }
        }
        for (StoreConfiguration xamStoreConfiguration : xamStoreList) {
            this.addStore(xamStoreConfiguration, false);
        }
        for (StoreConfiguration esuStoreConfiguration : esuStoreList) {
            this.addStore(esuStoreConfiguration, false);
        }
        LogCenter.debug(this, "Configuration Activated.");
        return true;
    }

    @Override
    public boolean configure(Configuration config) {
        this.setConfiguration(config);
        return this.activateConfiguration();
    }

    private void initializeStoreRoot(Store store) {
        RootStructure storeRoot = store.getRootStructure();
        try {
            Location appRootLocation = storeRoot.getRootLocation().getChildLocation("APPLICATIONS");
            if (((Boolean)this.application.execute(this.application.getApplicationUser(), new ExistsStoreChildOperation(appRootLocation))).booleanValue()) {
                ApplicationStructure applicationStructure;
                Collection<StoreChild> applicationLocations = this.application.execute(this.application.getApplicationUser(), new ListChildrenOperation(appRootLocation, true, false, false));
                for (StoreChild applicationLocation : applicationLocations) {
                    storeRoot.addApplicationStructure(new ApplicationStructure(applicationLocation.getName(), storeRoot));
                    LogCenter.debug(this, "Added Application Structure : " + applicationLocation.getName());
                }
                if (store.getAlias().equals(this.application.getMainStore().getAlias()) && (applicationStructure = storeRoot.getApplicationStructure(this.application.getName())) != null) {
                    Location userRootLocation = applicationStructure.getRootLocation().getChildLocation("users");
                    if (((Boolean)this.application.execute(this.application.getApplicationUser(), new ExistsStoreChildOperation(userRootLocation))).booleanValue()) {
                        Collection<StoreChild> userLocations = this.application.execute(this.application.getApplicationUser(), new ListChildrenOperation(userRootLocation, true, false, false));
                        for (StoreChild userLocation : userLocations) {
                            applicationStructure.addUserStructure(new UserStructure(userLocation.getName(), applicationStructure));
                        }
                    }
                }
            }
        }
        catch (OperationException oe) {
            // empty catch block
        }
    }

    private void persistConfiguration() {
        Container container = ((ApplicationImpl)this.application).getConfigurationLocation().getChildContainer("Stores.xml");
        try {
            this.application.execute(this.application.getApplicationUser(), new PersistOperation(container, new ObjectContentDescriptor(), new ObjectData(this.configuration), true));
        }
        catch (OperationException oe) {
            LogCenter.exception(this, "Failed to persist StoresConfiguration", oe);
        }
    }
}

