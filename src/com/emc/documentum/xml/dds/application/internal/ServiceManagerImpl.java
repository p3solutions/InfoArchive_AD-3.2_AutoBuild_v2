/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.application.internal;

import com.emc.documentum.xml.dds.application.Application;
import com.emc.documentum.xml.dds.application.ServiceManager;
import com.emc.documentum.xml.dds.configuration.Configurable;
import com.emc.documentum.xml.dds.configuration.Configuration;
import com.emc.documentum.xml.dds.configuration.baseline.ServiceConfiguration;
import com.emc.documentum.xml.dds.configuration.baseline.ServicesConfiguration;
import com.emc.documentum.xml.dds.logging.LogCenter;
import com.emc.documentum.xml.dds.service.Service;
import com.emc.documentum.xml.dds.service.ServiceType;
import com.emc.documentum.xml.dds.util.DependencyResolver;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServiceManagerImpl
implements ServiceManager,
Configurable {
    private final Map<String, Service> serviceNameMapping = new HashMap<String, Service>();
    private final Map<ServiceType, Service> serviceTypeMapping = new HashMap<ServiceType, Service>();
    private final Object serviceMutex = new Object();
    private final Application application;
    private ServicesConfiguration configuration;
    private List<String> serviceStartOrder = new ArrayList<String>();

    public ServiceManagerImpl(Application application) {
        this.application = application;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Service getService(String name) {
        Object object = this.serviceMutex;
        synchronized (object) {
            return this.serviceNameMapping.get(name);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Service getService(ServiceType type) {
        Object object = this.serviceMutex;
        synchronized (object) {
            return this.serviceTypeMapping.get(type);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<Service> getServices() {
        Object object = this.serviceMutex;
        synchronized (object) {
            return new ArrayList<Service>(this.serviceNameMapping.values());
        }
    }

    @Override
    public ServicesConfiguration getConfiguration() {
        return this.configuration;
    }

    @Override
    public void setConfiguration(Configuration configuration) {
        this.configuration = (ServicesConfiguration)configuration;
    }

    @Override
    public boolean configure(Configuration config) {
        this.setConfiguration(config);
        return this.activateConfiguration();
    }

    @Override
    public boolean activateConfiguration() {
        HashMap<String, List<String>> dependencyMap = new HashMap<String, List<String>>();
        if (this.configuration == null || this.configuration.size() == 0) {
            return true;
        }
        for (ServiceConfiguration serviceConfiguration : this.configuration.getList()) {
            dependencyMap.put(serviceConfiguration.getName(), serviceConfiguration.getDependencies());
            this.createService(serviceConfiguration);
        }
        this.serviceStartOrder = DependencyResolver.resolveDependencies(dependencyMap);
        if (this.serviceStartOrder == null) {
            LogCenter.error(this, "Dependencies between Services could not be resolved. Check the Services Configuration.");
            this.serviceStartOrder = new ArrayList<String>();
            return false;
        }
        return true;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean createService(ServiceConfiguration serviceConfiguration) {
        Object object = this.serviceMutex;
        synchronized (object) {
            ServiceType type = serviceConfiguration.getType();
            String name = serviceConfiguration.getName();
            String className = serviceConfiguration.getClassName();
            if (this.serviceNameMapping.get(name) != null) {
                LogCenter.error(this, "Service not created, another Service exists with the same name : " + name);
                return false;
            }
            if (this.serviceTypeMapping.get(type) != null) {
                LogCenter.error(this, "Service not created, another Service exists with the same type : " + type.toString());
                return false;
            }
            Class newServiceClass = null;
            try {
                newServiceClass = Class.forName(className);
            }
            catch (ClassNotFoundException cnfe) {
                LogCenter.exception(this, "Could not find Class object for " + className, cnfe);
            }
            catch (Exception e) {
                LogCenter.exception(this, "Unhandled Exception when creating Service, Type = " + type.getUserReadable() + ", Class = " + className, e);
            }
            if (newServiceClass == null) {
                return false;
            }
            Service newService = null;
            try {
                newService = (Service)newServiceClass.newInstance();
                this.serviceNameMapping.put(name, newService);
                this.serviceTypeMapping.put(type, newService);
                newService.setName(name);
                newService.setApplication(this.application);
                newService.setConfiguration(serviceConfiguration);
            }
            catch (Exception e) {
                LogCenter.exception(this, "Could not instantiate Service, Type = " + type.getUserReadable() + ", Class = " + className, e);
            }
            LogCenter.debug(this, newService == null ? "No Service created : " + name : "Created new Service : " + newService.getName());
            return newService != null;
        }
    }

    @Override
    public boolean initializeServices() {
        LogCenter.debug(this, "Initializing Services...");
        boolean result = true;
        for (String serviceName : this.serviceStartOrder) {
            result = result && this.serviceNameMapping.get(serviceName).initialize();
        }
        if (result) {
            LogCenter.debug(this, "Services initialized.");
        } else {
            LogCenter.debug(this, "Services could not be initialized.");
        }
        return result;
    }

    @Override
    public boolean startServices() {
        boolean result = true;
        for (String serviceName : this.serviceStartOrder) {
            result = result && this.serviceNameMapping.get(serviceName).start();
        }
        return result;
    }

    @Override
    public boolean pauseServices() {
        boolean result = true;
        ArrayList<String> reversed = new ArrayList<String>(this.serviceStartOrder);
        Collections.reverse(reversed);
        for (String serviceName : reversed) {
            result = result && this.serviceNameMapping.get(serviceName).pause();
        }
        return result;
    }

    @Override
    public boolean resumeServices() {
        boolean result = true;
        for (String serviceName : this.serviceStartOrder) {
            result = result && this.serviceNameMapping.get(serviceName).resume();
        }
        return result;
    }

    @Override
    public boolean stopServices() {
        boolean result = true;
        ArrayList<String> reversed = new ArrayList<String>(this.serviceStartOrder);
        Collections.reverse(reversed);
        for (String serviceName : reversed) {
            result = result && this.serviceNameMapping.get(serviceName).stop();
        }
        return result;
    }
}

