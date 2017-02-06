/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.fs.impl.internal;

import com.emc.documentum.xml.dds.DDS;
import com.emc.documentum.xml.dds.application.Application;
import com.emc.documentum.xml.dds.fs.Service;
import com.emc.documentum.xml.dds.fs.ServiceContext;
import com.emc.documentum.xml.dds.uri.URIResolver;

public abstract class ServiceImpl
implements Service {
    private final ServiceContext serviceContext;

    public ServiceImpl(ServiceContext serviceContext) {
        this.serviceContext = serviceContext;
    }

    @Override
    public ServiceContext getServiceContext() {
        return this.serviceContext;
    }

    public static Application getApplication() {
        return DDS.getApplication();
    }

    public static URIResolver getURIResolver(Application application) {
        return application.getDefaultURIResolver();
    }
}

