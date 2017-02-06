/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.service;

import com.emc.documentum.xml.dds.application.Application;
import com.emc.documentum.xml.dds.configuration.Configurable;
import com.emc.documentum.xml.dds.service.Action;
import com.emc.documentum.xml.dds.service.ServiceType;
import com.emc.documentum.xml.dds.service.State;

public interface Service
extends Configurable {
    public Application getApplication();

    public void setApplication(Application var1);

    public String getName();

    public void setName(String var1);

    public ServiceType getType();

    public State getState();

    public Action getLastAction();

    public boolean initialize();

    public boolean start();

    public boolean stop();

    public boolean pause();

    public boolean resume();

    public boolean fullStartup();
}

