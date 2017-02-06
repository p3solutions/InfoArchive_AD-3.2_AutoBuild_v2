/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.persistence.internal;

import com.emc.documentum.xml.dds.configuration.persistence.StoreConfiguration;
import com.emc.documentum.xml.dds.configuration.persistence.StoreUserConfiguration;
import com.emc.documentum.xml.dds.persistence.Store;
import com.emc.documentum.xml.dds.persistence.StoreType;
import com.emc.documentum.xml.dds.persistence.StoreUser;
import com.emc.documentum.xml.dds.persistence.StoreUserFactory;
import com.emc.documentum.xml.dds.structure.internal.RootStructure;

public abstract class AbstractStore
implements Store {
    private final String alias;
    private StoreUser defaultStoreUser;
    private final RootStructure rootStructure;

    public AbstractStore(StoreConfiguration configuration) {
        this.alias = configuration.getAlias();
        this.defaultStoreUser = StoreUserFactory.newStoreUser(configuration.getDefaultStoreUser());
        this.rootStructure = new RootStructure(this);
    }

    @Override
    public String getAlias() {
        return this.alias;
    }

    @Override
    public String getSeparator() {
        return this.getType().getSeparator();
    }

    @Override
    public StoreUser getDefaultStoreUser() {
        return this.defaultStoreUser;
    }

    @Override
    public void setDefaultStoreUser(StoreUser defaultStoreUser) {
        this.defaultStoreUser = defaultStoreUser;
    }

    @Override
    public RootStructure getRootStructure() {
        return this.rootStructure;
    }
}

