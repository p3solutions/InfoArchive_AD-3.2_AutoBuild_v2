/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.application.internal;

import com.emc.documentum.xml.dds.application.Application;
import com.emc.documentum.xml.dds.application.ServiceManager;
import com.emc.documentum.xml.dds.application.StoreManager;
import com.emc.documentum.xml.dds.application.StructureManager;
import com.emc.documentum.xml.dds.application.XBaseManager;
import com.emc.documentum.xml.dds.application.exception.InitializationException;
import com.emc.documentum.xml.dds.application.internal.ServiceManagerImpl;
import com.emc.documentum.xml.dds.application.internal.StoreManagerImpl;
import com.emc.documentum.xml.dds.application.internal.StructureManagerImpl;
import com.emc.documentum.xml.dds.application.internal.XBaseManagerImpl;
import com.emc.documentum.xml.dds.configuration.Configurable;
import com.emc.documentum.xml.dds.configuration.Configuration;
import com.emc.documentum.xml.dds.configuration.baseline.Bootstrap;
import com.emc.documentum.xml.dds.configuration.baseline.ServicesConfiguration;
import com.emc.documentum.xml.dds.configuration.baseline.StoresConfiguration;
import com.emc.documentum.xml.dds.configuration.baseline.StructuresConfiguration;
import com.emc.documentum.xml.dds.configuration.baseline.XBasesConfiguration;
import com.emc.documentum.xml.dds.configuration.baseline.security.JCESettings;
import com.emc.documentum.xml.dds.configuration.persistence.StoreConfiguration;
import com.emc.documentum.xml.dds.configuration.user.UserConfiguration;
import com.emc.documentum.xml.dds.logging.LogCenter;
import com.emc.documentum.xml.dds.operation.exception.OperationException;
import com.emc.documentum.xml.dds.operation.exception.OperationFailedException;
import com.emc.documentum.xml.dds.operation.framework.Operation;
import com.emc.documentum.xml.dds.operation.framework.OperationManager;
import com.emc.documentum.xml.dds.operation.framework.internal.OperationManagerImpl;
import com.emc.documentum.xml.dds.operation.library.persistence.CreateLocationOperation;
import com.emc.documentum.xml.dds.operation.library.persistence.ExistsStoreChildOperation;
import com.emc.documentum.xml.dds.operation.library.persistence.PersistOperation;
import com.emc.documentum.xml.dds.operation.library.persistence.RetrieveOperation;
import com.emc.documentum.xml.dds.persistence.Container;
import com.emc.documentum.xml.dds.persistence.ContentDescriptor;
import com.emc.documentum.xml.dds.persistence.Data;
import com.emc.documentum.xml.dds.persistence.Location;
import com.emc.documentum.xml.dds.persistence.LocationOptions;
import com.emc.documentum.xml.dds.persistence.SessionStoreUserStrategy;
import com.emc.documentum.xml.dds.persistence.Store;
import com.emc.documentum.xml.dds.persistence.StoreChild;
import com.emc.documentum.xml.dds.persistence.StoreUser;
import com.emc.documentum.xml.dds.persistence.content.ObjectContentDescriptor;
import com.emc.documentum.xml.dds.persistence.data.ObjectData;
import com.emc.documentum.xml.dds.persistence.strategy.StoreDefaultStrategy;
import com.emc.documentum.xml.dds.persistence.strategy.UserDefaultStrategy;
import com.emc.documentum.xml.dds.persistence.xam.internal.XAMSession;
import com.emc.documentum.xml.dds.persistence.xdb.internal.XDBStore;
import com.emc.documentum.xml.dds.serialization.DefaultDDSSerializer;
import com.emc.documentum.xml.dds.serialization.exception.DeserializationException;
import com.emc.documentum.xml.dds.service.DDSServiceType;
import com.emc.documentum.xml.dds.service.Service;
import com.emc.documentum.xml.dds.service.ServiceType;
import com.emc.documentum.xml.dds.service.State;
import com.emc.documentum.xml.dds.service.impl.ServiceImpl;
import com.emc.documentum.xml.dds.structure.DDSDataSet;
import com.emc.documentum.xml.dds.structure.DDSLocale;
import com.emc.documentum.xml.dds.uri.URIResolver;
import com.emc.documentum.xml.dds.user.User;
import com.emc.documentum.xml.dds.user.internal.UserImpl;
import com.emc.documentum.xml.dds.util.internal.Cryptographer;
import com.emc.documentum.xml.dds.xbase.XBase;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ApplicationImpl
extends ServiceImpl
implements Application {
    private Store mainStore;
    private URIResolver defaultURIResolver;
    private OperationManager operationManager;
    private StoreManager storeManager;
    private ServiceManager serviceManager;
    private final StructureManager structureManager;
    private final XBaseManager xBaseManager;
    private User applicationUser;
    private Location configurationLocation;
    private boolean forceCreate;

    public ApplicationImpl() {
        this.storeManager = new StoreManagerImpl(this);
        this.serviceManager = new ServiceManagerImpl(this);
        this.structureManager = new StructureManagerImpl(this);
        this.xBaseManager = new XBaseManagerImpl(this);
    }

    public ApplicationImpl(Bootstrap bootstrap) {
        this();
        this.setConfiguration(bootstrap);
        this.setName(bootstrap.getName());
    }

    public ApplicationImpl(String bootstrapXML) throws InitializationException {
        this();
        try {
            Bootstrap bootstrap = (Bootstrap)new DefaultDDSSerializer().deserialize(bootstrapXML);
            this.setConfiguration(bootstrap);
            this.setName(bootstrap.getName());
        }
        catch (DeserializationException de) {
            throw new InitializationException("Initialization failed : Bootstrap XML could not be deserialized.", de);
        }
    }

    @Override
    public Store getMainStore() {
        return this.mainStore;
    }

    @Override
    public URIResolver getDefaultURIResolver() {
        return this.defaultURIResolver;
    }

    @Override
    public void setDefaultURIResolver(URIResolver resolver) {
        this.defaultURIResolver = resolver;
    }

    @Override
    public User getApplicationUser() {
        return this.applicationUser;
    }

    @Override
    public SessionStoreUserStrategy getSessionStoreUserStrategy() {
        if (this.operationManager == null) {
            return null;
        }
        return this.operationManager.getSessionStoreUserStrategy();
    }

    @Override
    public OperationManager getOperationManager() {
        return this.operationManager;
    }

    @Override
    public ServiceManager getServiceManager() {
        return this.serviceManager;
    }

    @Override
    public StoreManager getStoreManager() {
        return this.storeManager;
    }

    @Override
    public StructureManager getStructureManager() {
        return this.structureManager;
    }

    @Override
    public XBaseManager getXBaseManager() {
        return this.xBaseManager;
    }

    @Override
    public <T> T execute(User user, Operation<T> operation) throws OperationFailedException {
        return this.operationManager.execute(user, operation);
    }

    @Override
    public Service getService(String name) {
        return this.serviceManager.getService(name);
    }

    @Override
    public Store getStore(String alias) {
        return this.storeManager.getStore(alias);
    }

    @Override
    public DDSDataSet getDataSet(String alias) {
        return this.structureManager.getDataSet(alias);
    }

    @Override
    public DDSDataSet getDefaultDataSet() {
        return this.structureManager.getDefaultDataSet();
    }

    @Override
    public DDSLocale getLocale(String dataSetAlias, String localeName) {
        return this.structureManager.getLocale(this.getDataSet(dataSetAlias), localeName);
    }

    @Override
    public DDSLocale getDefaultLocale(String dataSetAlias) {
        DDSDataSet dataSet = this.getDataSet(dataSetAlias);
        if (dataSet == null || !dataSet.isLocaleAware()) {
            return null;
        }
        DDSLocale locale = dataSet.getLocale(dataSet.getDefaultLocale());
        if (locale != null) {
            return locale;
        }
        List<DDSLocale> dataSetLocales = this.structureManager.getLocales(dataSet);
        if (dataSetLocales != null && dataSetLocales.size() == 1) {
            return dataSetLocales.get(0);
        }
        return null;
    }

    @Override
    public XBase getXBase(String id) {
        return this.xBaseManager.getXBase(id);
    }

    public Location getConfigurationLocation() {
        return this.configurationLocation;
    }

    @Override
    public ServiceType getType() {
        return DDSServiceType.APPLICATION;
    }

    @Override
    protected boolean checkDependencies() {
        return this.getConfiguration() != null || this.mainStore != null;
    }

    @Override
    protected boolean executeInitialization() {
        LogCenter.debug(this, "Application.executeInitialization() called");
        if (!this.activateConfiguration()) {
            LogCenter.error(this, "Could not process configuration, please check the Bootstrap file.");
            return false;
        }
        this.applicationUser = new UserImpl(new UserConfiguration(null, null));
        this.applicationUser.addStoreUser(this.mainStore.getAlias(), this.mainStore.getDefaultStoreUser());
        UserDefaultStrategy strategy = new UserDefaultStrategy();
        strategy.setFallBackStrategy(new StoreDefaultStrategy());
        this.operationManager = new OperationManagerImpl(this, strategy);
        ArrayList<String> pathComponents = new ArrayList<String>();
        pathComponents.add("APPLICATIONS");
        pathComponents.add(this.getName());
        pathComponents.add("configuration");
        this.configurationLocation = this.getMainStore().getLocation(pathComponents);
        try {
            if (!(((Boolean)this.execute(this.getApplicationUser(), new ExistsStoreChildOperation(this.configurationLocation))).booleanValue() || "admin".equals(this.getName()) || this.forceCreate)) {
                LogCenter.error(this, "Could not initialize application " + this.getName() + ": " + this.configurationLocation.getPath() + " not found in database");
                return false;
            }
            if (!((Boolean)this.execute(this.getApplicationUser(), new ExistsStoreChildOperation(this.configurationLocation))).booleanValue()) {
                this.execute(this.getApplicationUser(), new CreateLocationOperation(this.configurationLocation, null, true));
            }
        }
        catch (OperationException oe) {
            LogCenter.exception(this, "Could not initialize Configuration location in main Store. Please check whether the Main Store XDB is running.", oe);
            return false;
        }
        return this.configureStoreManager() && this.configureServiceManager() && this.configureStructureManager() && this.configureXBaseManager() && this.serviceManager.initializeServices();
    }

    @Override
    protected boolean executeStartup() {
        LogCenter.debug(this, "Application.executeStartup() called");
        return this.serviceManager.startServices();
    }

    @Override
    protected boolean executePause() {
        return this.serviceManager.pauseServices();
    }

    @Override
    protected boolean executeResume() {
        return this.serviceManager.resumeServices();
    }

    @Override
    protected boolean executeShutdown() {
        boolean result = this.serviceManager.stopServices();
        if (result) {
            this.mainStore = null;
            XAMSession.disconnectSessions();
            this.serviceManager = new ServiceManagerImpl(this);
            for (Store store : this.storeManager.getStores()) {
                if (!(store instanceof XDBStore)) continue;
                ((XDBStore)store).disconnect();
            }
            this.storeManager = new StoreManagerImpl(this);
        }
        return result;
    }

    @Override
    public boolean activateConfiguration() {
        if (this.getConfiguration() == null || this.getState() == State.RUNNING) {
            return false;
        }
        Bootstrap bootstrap = (Bootstrap)this.getConfiguration();
        this.setName(bootstrap.getName());
        LogCenter.debug(this, "Application Name = " + this.getName());
        if (!Cryptographer.initialize(bootstrap.getJCESettings(), bootstrap.getPrivateKeyPath(), bootstrap.getPublicKeyPath())) {
            LogCenter.error(this, "Cryptographer was not initialized properly.");
            return false;
        }
        this.mainStore = ((StoreManagerImpl)this.storeManager).setMainStore(bootstrap.getMainStore());
        if (this.mainStore == null) {
            LogCenter.error(this, "Main Store not specified in bootstrap.");
            return false;
        }
        this.setDefaultURIResolver(bootstrap.getDefaultURIResolver());
        if (this.defaultURIResolver == null) {
            LogCenter.error(this, "Default URI Resolver not specified in bootstrap.");
            return false;
        }
        this.defaultURIResolver.setApplication(this);
        this.forceCreate = bootstrap.getForceCreate();
        return true;
    }

    private boolean configureStoreManager() {
        return this.configureManager(this.storeManager, "Stores.xml", new StoresConfiguration());
    }

    private boolean configureServiceManager() {
        return this.configureManager(this.serviceManager, "Services.xml", new ServicesConfiguration());
    }

    private boolean configureStructureManager() {
        return this.configureManager(this.structureManager, "Structures.xml", new StructuresConfiguration());
    }

    private boolean configureXBaseManager() {
        return this.configureManager(this.xBaseManager, "XBases.xml", new XBasesConfiguration());
    }

    private boolean configureManager(Configurable manager, String configurationName, Configuration blankConfiguration) {
        Configuration managerConfiguration = this.resolve(configurationName, blankConfiguration);
        return manager.configure(managerConfiguration);
    }

    private Configuration resolve(String configurationName, Configuration blankConfiguration) {
        try {
            Container container = this.configurationLocation.getChildContainer(configurationName);
            if (!((Boolean)this.execute(this.getApplicationUser(), new ExistsStoreChildOperation(container))).booleanValue()) {
                this.execute(this.getApplicationUser(), new PersistOperation(container, new ObjectContentDescriptor(), new ObjectData(blankConfiguration), true));
                return blankConfiguration;
            }
            return (Configuration)((ObjectData)this.operationManager.execute(this.applicationUser, new RetrieveOperation(container, new ObjectContentDescriptor()))).content();
        }
        catch (OperationException oe) {
            LogCenter.exception(this, "Could not retrieve configuration document : " + configurationName, oe);
            return null;
        }
    }
}

