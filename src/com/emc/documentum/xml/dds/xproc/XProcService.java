/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.xproc;

import com.emc.documentum.xml.dds.service.Service;
import com.emc.documentum.xml.dds.service.exception.ServiceNotAvailableException;
import com.emc.documentum.xml.dds.user.User;
import com.emc.documentum.xml.dds.xproc.DDSXProc;
import com.emc.documentum.xml.dds.xproc.exception.DDSXProcConfigurationException;

public interface XProcService
extends Service {
    public DDSXProc newXProc(User var1, boolean var2) throws DDSXProcConfigurationException, ServiceNotAvailableException;
}

