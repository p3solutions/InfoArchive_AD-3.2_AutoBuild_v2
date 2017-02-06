/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.application.internal;

import com.emc.documentum.xml.dds.application.Application;
import com.emc.documentum.xml.dds.application.StructureManager;
import com.emc.documentum.xml.dds.application.internal.ApplicationImpl;
import com.emc.documentum.xml.dds.configuration.Configuration;
import com.emc.documentum.xml.dds.configuration.baseline.DataSetConfiguration;
import com.emc.documentum.xml.dds.configuration.baseline.StructuresConfiguration;
import com.emc.documentum.xml.dds.logging.LogCenter;
import com.emc.documentum.xml.dds.operation.exception.OperationException;
import com.emc.documentum.xml.dds.operation.framework.Operation;
import com.emc.documentum.xml.dds.operation.library.persistence.CreateLocationOperation;
import com.emc.documentum.xml.dds.operation.library.persistence.ExistsStoreChildOperation;
import com.emc.documentum.xml.dds.operation.library.persistence.ListChildrenOperation;
import com.emc.documentum.xml.dds.operation.library.persistence.PersistOperation;
import com.emc.documentum.xml.dds.operation.library.persistence.RetrieveOperation;
import com.emc.documentum.xml.dds.persistence.Container;
import com.emc.documentum.xml.dds.persistence.ContentDescriptor;
import com.emc.documentum.xml.dds.persistence.Data;
import com.emc.documentum.xml.dds.persistence.Location;
import com.emc.documentum.xml.dds.persistence.LocationOptions;
import com.emc.documentum.xml.dds.persistence.Store;
import com.emc.documentum.xml.dds.persistence.StoreChild;
import com.emc.documentum.xml.dds.persistence.content.ObjectContentDescriptor;
import com.emc.documentum.xml.dds.persistence.data.ObjectData;
import com.emc.documentum.xml.dds.structure.DDSDataSet;
import com.emc.documentum.xml.dds.structure.DDSLocale;
import com.emc.documentum.xml.dds.structure.Structure;
import com.emc.documentum.xml.dds.structure.StructureStrategy;
import com.emc.documentum.xml.dds.structure.StructureStrategyType;
import com.emc.documentum.xml.dds.structure.exception.DataSetAlreadyExistsException;
import com.emc.documentum.xml.dds.structure.exception.DataSetNotFoundException;
import com.emc.documentum.xml.dds.structure.exception.LocaleAlreadyExistsException;
import com.emc.documentum.xml.dds.structure.exception.LocaleNotFoundException;
import com.emc.documentum.xml.dds.structure.exception.StructureException;
import com.emc.documentum.xml.dds.structure.internal.ApplicationStructure;
import com.emc.documentum.xml.dds.structure.internal.DDSDataSetImpl;
import com.emc.documentum.xml.dds.structure.internal.DDSLocaleImpl;
import com.emc.documentum.xml.dds.structure.internal.DataStructure;
import com.emc.documentum.xml.dds.structure.internal.RootStructure;
import com.emc.documentum.xml.dds.structure.strategy.DDSStructureStrategy;
import com.emc.documentum.xml.dds.structure.strategy.DDSStructureStrategyType;
import com.emc.documentum.xml.dds.structure.strategy.DocumentumStructureStrategy;
import com.emc.documentum.xml.dds.user.User;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class StructureManagerImpl
implements StructureManager {
    private final Application application;
    private StructuresConfiguration configuration;
    private final Map<String, DDSDataSet> dataSetMap;
    private String defaultDataSet;
    private final Object structureMutex = new Object();

    public StructureManagerImpl(Application application) {
        this.application = application;
        this.dataSetMap = new HashMap<String, DDSDataSet>();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public DDSDataSet getDataSet(String alias) {
        Object object = this.structureMutex;
        synchronized (object) {
            return this.dataSetMap.get(alias);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<DDSDataSet> getDataSets() {
        Object object = this.structureMutex;
        synchronized (object) {
            return new ArrayList<DDSDataSet>(this.dataSetMap.values());
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public DDSDataSet getDefaultDataSet() {
        Object object = this.structureMutex;
        synchronized (object) {
            if (this.defaultDataSet == null && this.dataSetMap.size() == 1) {
                return this.getDataSets().get(0);
            }
            return this.getDataSet(this.defaultDataSet);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setDefaultDataSet(DDSDataSet dataSet) {
        Object object = this.structureMutex;
        synchronized (object) {
            this.defaultDataSet = dataSet == null ? null : dataSet.getAlias();
        }
    }

    @Override
    public DDSDataSet addDataSet(String alias, String id, Store store) throws DataSetNotFoundException {
        return this.addDataSet(alias, id, store, true);
    }

    private DDSDataSet addDataSet(String alias, String id, Store store, boolean persist) throws DataSetNotFoundException {
        Object object = this.structureMutex;
        synchronized (object) {
            try {
            	DDSDataSet dataSet;
                User user = this.application.getApplicationUser();
                LogCenter.debug(this, "Checking DataSet Config existence at : " + store.getRootStructure().getDataSetConfigurationContainer(id).getPath());
                if (((Boolean)this.application.execute(user, new ExistsStoreChildOperation(store.getRootStructure().getDataSetConfigurationContainer(id)))).booleanValue()) {
                    LogCenter.debug(this, "Retrieving DataSet Config.");
                    dataSet = (DDSDataSet)((ObjectData)this.application.execute(user, new RetrieveOperation(store.getRootStructure().getDataSetConfigurationContainer(id), new ObjectContentDescriptor()))).content();
                    dataSet.setAlias(alias);
                    dataSet.setRootStructure(store.getRootStructure());
                    ((DDSDataSetImpl)dataSet).setParentStructure(store.getRootStructure());
                    this.dataSetMap.put(alias, (DDSDataSet)dataSet);
                } else {
                    dataSet = new DDSDataSetImpl(alias, id, true, (StructureStrategy)new DocumentumStructureStrategy(), store.getRootStructure());
                    LogCenter.debug(this, "Checking DataSet existence.");
                    if (((Boolean)this.application.execute(user, new ExistsStoreChildOperation(dataSet.getRootLocation()))).booleanValue()) {
                        if (((Boolean)this.application.execute(user, new ExistsStoreChildOperation(dataSet.getRootLocation().getChildLocation("Collection")))).booleanValue()) {
                            dataSet.setStructureStrategy(new DocumentumStructureStrategy());
                        } else {
                            dataSet.setStructureStrategy(new DDSStructureStrategy());
                        }
                        LogCenter.debug(this, "Persisting DataSet Config.");
                        this.application.execute(user, new PersistOperation(store.getRootStructure().getDataSetConfigurationContainer(id), new ObjectContentDescriptor(), new ObjectData(dataSet), false));
                        this.dataSetMap.put(alias, (DDSDataSet)dataSet);
                    } else {
                        throw new DataSetNotFoundException("DataSet Location not found : " + dataSet.getRootLocation().getPath());
                    }
                }
                if (dataSet.isLocaleAware()) {
                    Location localeLocation = dataSet.getStructureStrategy() instanceof DocumentumStructureStrategy ? dataSet.getRootLocation().getChildLocation("Collection") : dataSet.getRootLocation();
                    Collection<StoreChild> localeLocations = this.application.execute(this.application.getApplicationUser(), new ListChildrenOperation(localeLocation, true, false, false));
                    for (StoreChild location : localeLocations) {
                        LogCenter.debug(this, "Adding Locale " + location.getName());
                        this.addLocale((DDSDataSet)dataSet, ((Location)location).getName());
                    }
                }
                if (persist) {
                    this.configuration.add(new DataSetConfiguration(dataSet.getAlias(), store.getAlias(), dataSet.getId()));
                    this.persistConfiguration();
                }
                return dataSet;
            }
            catch (OperationException oe) {
                throw new DataSetNotFoundException(oe);
            }
            catch (Exception e) {
                throw new DataSetNotFoundException(e);
            }
        }
    }

    @Override
    public void removeDataSet(String alias) throws DataSetNotFoundException {
        if (this.dataSetMap.remove(alias) == null) {
            throw new DataSetNotFoundException("Remove DataSet failed : DataSet " + alias + " could not be found.");
        }
        DataSetConfiguration dsConfiguration = null;
        Iterator<DataSetConfiguration> iterator = this.configuration.getDataSetConfigurations().iterator();
        while (dsConfiguration == null && iterator.hasNext()) {
            DataSetConfiguration next = iterator.next();
            if (!next.getAlias().equals(alias)) continue;
            dsConfiguration = next;
        }
        this.configuration.remove(dsConfiguration);
        this.persistConfiguration();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public DDSDataSet createDataSet(String alias, String id, Store store, boolean localeAware, StructureStrategy strategy) throws DataSetAlreadyExistsException, OperationException {
        Object object = this.structureMutex;
        synchronized (object) {
            DDSDataSetImpl dataSet = new DDSDataSetImpl(alias, id, localeAware, strategy, store.getRootStructure());
            if (((Boolean)this.application.execute(this.application.getApplicationUser(), new ExistsStoreChildOperation(dataSet.getRootLocation()))).booleanValue()) {
                throw new DataSetAlreadyExistsException();
            }
            this.createLocations(dataSet);
            this.application.execute(this.application.getApplicationUser(), new PersistOperation(store.getRootStructure().getDataSetConfigurationContainer(id), new ObjectContentDescriptor(), new ObjectData(dataSet), true));
            this.dataSetMap.put(alias, dataSet);
            this.configuration.add(new DataSetConfiguration(dataSet.getAlias(), store.getAlias(), dataSet.getId()));
            this.persistConfiguration();
            return dataSet;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public DDSDataSet createDataSet(String alias, String id, Store store, String defaultLocale, StructureStrategy strategy) throws DataSetAlreadyExistsException, OperationException {
        Object object = this.structureMutex;
        synchronized (object) {
            DDSDataSetImpl dataSet = new DDSDataSetImpl(alias, id, defaultLocale, strategy, store.getRootStructure());
            if (((Boolean)this.application.execute(this.application.getApplicationUser(), new ExistsStoreChildOperation(dataSet.getRootLocation()))).booleanValue()) {
                throw new DataSetAlreadyExistsException();
            }
            this.createLocations(dataSet);
            this.application.execute(this.application.getApplicationUser(), new PersistOperation(store.getRootStructure().getDataSetConfigurationContainer(id), new ObjectContentDescriptor(), new ObjectData(dataSet), true));
            this.dataSetMap.put(alias, dataSet);
            this.configuration.add(new DataSetConfiguration(dataSet.getAlias(), store.getAlias(), dataSet.getId()));
            this.persistConfiguration();
            try {
                this.createLocale((DDSDataSet)dataSet, this.localeForFullString(defaultLocale));
            }
            catch (LocaleAlreadyExistsException laee) {
                LogCenter.exception(this, "Could not create default Locale :", laee);
            }
            return dataSet;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public DDSLocale getLocale(DDSDataSet dataSet, String name) {
        Object object = this.structureMutex;
        synchronized (object) {
            return dataSet.getLocale(name);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public DDSLocale getLocale(DDSDataSet dataSet, Locale locale) {
        Object object = this.structureMutex;
        synchronized (object) {
            return dataSet.getLocale(locale.toString());
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<DDSLocale> getLocales(DDSDataSet dataSet) {
        Object object = this.structureMutex;
        synchronized (object) {
            return dataSet.getLocales();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<DDSLocale> getLocales(DDSDataSet dataSet, String language) {
        Object object = this.structureMutex;
        synchronized (object) {
            ArrayList<DDSLocale> result = new ArrayList<DDSLocale>();
            for (DDSLocale locale : dataSet.getLocales()) {
                if (!locale.getJavaLocale().getLanguage().equals(language)) continue;
                result.add(locale);
            }
            return result;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<DDSLocale> getLocales(DDSDataSet dataSet, String language, String country) {
        Object object = this.structureMutex;
        synchronized (object) {
            ArrayList<DDSLocale> result = new ArrayList<DDSLocale>();
            for (DDSLocale locale : dataSet.getLocales()) {
                if (!locale.getJavaLocale().getLanguage().equals(language) || !locale.getJavaLocale().getCountry().equals(country)) continue;
                result.add(locale);
            }
            return result;
        }
    }

    @Override
    public DDSLocale addLocale(DDSDataSet dataSet, String name) throws LocaleNotFoundException {
        Object object = this.structureMutex;
        synchronized (object) {
            try {
                DDSLocaleImpl locale;
                try {
                    locale = new DDSLocaleImpl(name, (Structure)((DDSDataSetImpl)dataSet).getDataStructure());
                }
                catch (StructureException se) {
                    throw new LocaleNotFoundException("DataSet " + dataSet.getId() + " contains a library with a name which is not a valid Java Locale name : " + name);
                }
                LogCenter.debug(this, "Checking Locale existence : " + locale.getRootLocation().getPath(false));
                if (!((Boolean)this.application.execute(this.application.getApplicationUser(), new ExistsStoreChildOperation(locale.getRootLocation()))).booleanValue()) {
                    throw new LocaleNotFoundException("Location for Locale " + name + " not found in Store at : " + locale.getRootLocation().getCanonicalPath());
                }
                dataSet.addLocale(locale);
                return locale;
            }
            catch (OperationException oe) {
                throw new LocaleNotFoundException(oe);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeLocale(DDSDataSet dataSet, String name) throws LocaleNotFoundException {
        Object object = this.structureMutex;
        synchronized (object) {
            DDSLocale locale = dataSet.getLocale(name);
            if (locale == null) {
                throw new LocaleNotFoundException("Locale could not be found for removal.");
            }
            dataSet.removeLocale(locale);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public DDSLocale createLocale(DDSDataSet dataSet, Locale locale) throws LocaleAlreadyExistsException, OperationException {
        Object object = this.structureMutex;
        synchronized (object) {
            DDSLocaleImpl localeDDS = new DDSLocaleImpl(locale, (Structure)((DDSDataSetImpl)dataSet).getDataStructure());
            if (((Boolean)this.application.execute(this.application.getApplicationUser(), new ExistsStoreChildOperation(localeDDS.getRootLocation()))).booleanValue()) {
                throw new LocaleAlreadyExistsException();
            }
            this.createLocations(localeDDS);
            dataSet.addLocale(localeDDS);
            this.persistConfiguration();
            return localeDDS;
        }
    }

    @Override
    public DDSLocale createLocale(DDSDataSet dataSet, String language) throws LocaleAlreadyExistsException, OperationException {
        return this.createLocale(dataSet, new Locale(language));
    }

    @Override
    public DDSLocale createLocale(DDSDataSet dataSet, String language, String country) throws LocaleAlreadyExistsException, OperationException {
        return this.createLocale(dataSet, new Locale(language, country));
    }

    @Override
    public DDSLocale createLocale(DDSDataSet dataSet, String language, String country, String variant) throws LocaleAlreadyExistsException, OperationException {
        return this.createLocale(dataSet, new Locale(language, country, variant));
    }

    @Override
    public ApplicationStructure getApplicationStructure() {
        return this.application.getMainStore().getRootStructure().getApplicationStructure(this.application.getName());
    }

    @Override
    public Configuration getConfiguration() {
        return this.configuration;
    }

    @Override
    public void setConfiguration(Configuration config) {
        this.configuration = (StructuresConfiguration)config;
    }

    @Override
    public boolean activateConfiguration() {
        LogCenter.debug(this, "Activating Configuration...");
        if (this.configuration != null) {
            for (DataSetConfiguration dataSetConfiguration : this.configuration.getDataSetConfigurations()) {
                try {
                    DDSDataSet dataSet = this.addDataSet(dataSetConfiguration.getAlias(), dataSetConfiguration.getId(), this.application.getStore(dataSetConfiguration.getStoreAlias()), false);
                    dataSet.setDefaultLocale(dataSetConfiguration.getDefaultLocale());
                    continue;
                }
                catch (DataSetNotFoundException dsnfe) {
                    LogCenter.exception(this, "Could not find all the configured Data Sets :", dsnfe);
                    return false;
                }
            }
        }
        LogCenter.debug(this, "Configuration activated.");
        return true;
    }

    @Override
    public boolean configure(Configuration config) {
        this.setConfiguration(config);
        return this.activateConfiguration();
    }

    private Locale localeForFullString(String fullLocaleString) {
        String[] components = fullLocaleString.split("_");
        switch (components.length) {
            case 1: {
                return new Locale(components[0]);
            }
            case 2: {
                return new Locale(components[0], components[1]);
            }
            case 3: {
                return new Locale(components[0], components[1], components[2]);
            }
        }
        return null;
    }

    private void createLocations(DDSDataSet dataSet) throws OperationException {
        if (!dataSet.isLocaleAware()) {
            this.application.execute(this.application.getApplicationUser(), new CreateLocationOperation(dataSet.getLocation(""), null, true));
            if (dataSet.getStructureStrategy().getType() == DDSStructureStrategyType.DOCUMENTUM) {
                this.application.execute(this.application.getApplicationUser(), new CreateLocationOperation(dataSet.getRootLocation().getChildLocation("CollectionMetadata"), null, true));
            }
        } else {
            this.application.execute(this.application.getApplicationUser(), new CreateLocationOperation(dataSet.getRootLocation(), null, true));
            if (dataSet.getStructureStrategy().getType() == DDSStructureStrategyType.DOCUMENTUM) {
                this.application.execute(this.application.getApplicationUser(), new CreateLocationOperation(dataSet.getRootLocation().getChildLocation("Collection"), null, true));
                this.application.execute(this.application.getApplicationUser(), new CreateLocationOperation(dataSet.getRootLocation().getChildLocation("CollectionMetadata"), null, true));
            }
        }
    }

    private void createLocations(DDSLocale locale) throws OperationException {
        this.application.execute(this.application.getApplicationUser(), new CreateLocationOperation(locale.getLocation(""), null, true));
        if (locale.getDataSet().getStructureStrategy().getType() == DDSStructureStrategyType.DOCUMENTUM) {
            this.application.execute(this.application.getApplicationUser(), new CreateLocationOperation(locale.getDataSet().getRootLocation().getChildLocation("CollectionMetadata").getChildLocation(locale.getId()), null, true));
        }
    }

    private void persistConfiguration() {
        Container container = ((ApplicationImpl)this.application).getConfigurationLocation().getChildContainer("Structures.xml");
        try {
            this.application.execute(this.application.getApplicationUser(), new PersistOperation(container, new ObjectContentDescriptor(), new ObjectData(this.configuration), true));
        }
        catch (OperationException oe) {
            LogCenter.exception(this, "Failed to persist StructuresConfiguration", oe);
        }
    }
}

