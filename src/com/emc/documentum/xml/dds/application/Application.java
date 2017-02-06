/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.application;

import com.emc.documentum.xml.dds.application.ServiceManager;
import com.emc.documentum.xml.dds.application.StoreManager;
import com.emc.documentum.xml.dds.application.StructureManager;
import com.emc.documentum.xml.dds.application.XBaseManager;
import com.emc.documentum.xml.dds.operation.exception.OperationFailedException;
import com.emc.documentum.xml.dds.operation.framework.Operation;
import com.emc.documentum.xml.dds.operation.framework.OperationManager;
import com.emc.documentum.xml.dds.persistence.SessionStoreUserStrategy;
import com.emc.documentum.xml.dds.persistence.Store;
import com.emc.documentum.xml.dds.service.Service;
import com.emc.documentum.xml.dds.structure.DDSDataSet;
import com.emc.documentum.xml.dds.structure.DDSLocale;
import com.emc.documentum.xml.dds.uri.URIResolver;
import com.emc.documentum.xml.dds.user.User;
import com.emc.documentum.xml.dds.xbase.XBase;

public interface Application
extends Service {
    @Override
    public String getName();

    public Store getMainStore();

    public URIResolver getDefaultURIResolver();

    public void setDefaultURIResolver(URIResolver var1);

    public User getApplicationUser();

    public SessionStoreUserStrategy getSessionStoreUserStrategy();

    public OperationManager getOperationManager();

    public ServiceManager getServiceManager();

    public StoreManager getStoreManager();

    public StructureManager getStructureManager();

    public XBaseManager getXBaseManager();

    public <T> T execute(User var1, Operation<T> var2) throws OperationFailedException;

    public Service getService(String var1);

    public Store getStore(String var1);

    public DDSDataSet getDataSet(String var1);

    public DDSDataSet getDefaultDataSet();

    public DDSLocale getLocale(String var1, String var2);

    public DDSLocale getDefaultLocale(String var1);

    public XBase getXBase(String var1);
}

