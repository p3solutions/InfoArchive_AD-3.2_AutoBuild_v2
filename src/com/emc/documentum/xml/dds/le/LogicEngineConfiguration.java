/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.le;

import com.emc.documentum.xml.dds.configuration.baseline.ServiceConfiguration;

public class LogicEngineConfiguration
extends ServiceConfiguration {
    private String dataModuleResolver;
    private String stateManager;
    private String s1000DVersion;

    public String getDataModuleResolver() {
        return this.dataModuleResolver == null ? null : this.dataModuleResolver.trim();
    }

    public void setDataModuleResolver(String dataModuleResolver) {
        this.dataModuleResolver = dataModuleResolver;
    }

    public String getStateManager() {
        return this.stateManager == null ? null : this.stateManager.trim();
    }

    public void setStateManager(String stateManager) {
        this.stateManager = stateManager;
    }

    public String getS1000DVersion() {
        return this.s1000DVersion == null ? null : this.s1000DVersion.trim();
    }

    public void setS1000DVersion(String version) {
        this.s1000DVersion = version;
    }
}

