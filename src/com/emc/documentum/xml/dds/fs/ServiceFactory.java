/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.fs;

import com.emc.documentum.xml.dds.DDS;
import com.emc.documentum.xml.dds.fs.ServiceContext;
import com.emc.documentum.xml.dds.fs.search.metadata.MetadataSearchService;
import com.emc.documentum.xml.dds.fs.search.metadata.impl.internal.MetadataSearchServiceImpl;
import com.emc.documentum.xml.dds.fs.xproc.XProcService;
import com.emc.documentum.xml.dds.fs.xproc.impl.internal.XProcServiceImpl;
import com.emc.documentum.xml.dds.fs.xquery.XQueryService;
import com.emc.documentum.xml.dds.fs.xquery.impl.internal.XQueryServiceImpl;
import com.emc.documentum.xml.dds.user.User;

public class ServiceFactory {
    private static ServiceFactory instance;

    public static synchronized ServiceFactory getInstance() {
        if (instance == null) {
            instance = new ServiceFactory();
        }
        return instance;
    }

    public XQueryService createXQueryService(ServiceContext serviceContext) {
        return new XQueryServiceImpl(serviceContext);
    }

    public XProcService createXProcService(ServiceContext serviceContext) {
        return new XProcServiceImpl(serviceContext);
    }

    public MetadataSearchService createMetadataSearchService(ServiceContext serviceContext) {
        return new MetadataSearchServiceImpl(serviceContext);
    }

    public ServiceContext createDefaultUserContext() {
        return new ServiceContext(){
            private User user;

            @Override
            public User getUser() {
                return this.user;
            }
        };
    }

}

