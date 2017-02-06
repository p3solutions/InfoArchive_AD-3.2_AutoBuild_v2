/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.le;

import com.emc.documentum.xml.dds.le.DDSLogicEngine;
import com.emc.documentum.xml.dds.le.DDSProcessDataModuleRenderer;
import com.emc.documentum.xml.dds.le.exception.DDSLogicEngineConfigurationException;
import com.emc.documentum.xml.dds.le.exception.DDSProcessDataModuleRendererConfigurationException;
import com.emc.documentum.xml.dds.service.Service;
import com.emc.documentum.xml.dds.service.exception.ServiceNotAvailableException;
import com.emc.documentum.xml.dds.user.User;

public interface LogicEngineService
extends Service {
    public DDSLogicEngine newLogicEngine(User var1) throws DDSLogicEngineConfigurationException, ServiceNotAvailableException;

    public DDSProcessDataModuleRenderer newProcessDataModuleRenderer(User var1) throws DDSProcessDataModuleRendererConfigurationException, ServiceNotAvailableException;
}

