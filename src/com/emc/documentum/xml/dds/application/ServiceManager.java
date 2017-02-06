/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.application;

import com.emc.documentum.xml.dds.configuration.Configurable;
import com.emc.documentum.xml.dds.configuration.baseline.ServiceConfiguration;
import com.emc.documentum.xml.dds.service.Service;
import com.emc.documentum.xml.dds.service.ServiceType;
import java.util.List;

public interface ServiceManager
extends Configurable {
    public Service getService(String var1);

    public Service getService(ServiceType var1);

    public List<Service> getServices();

    public boolean createService(ServiceConfiguration var1);

    public boolean initializeServices();

    public boolean startServices();

    public boolean pauseServices();

    public boolean resumeServices();

    public boolean stopServices();
}

