/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.application;

import com.emc.documentum.xml.dds.configuration.Configurable;
import com.emc.documentum.xml.dds.configuration.persistence.StoreConfiguration;
import com.emc.documentum.xml.dds.configuration.persistence.StoreUserConfiguration;
import com.emc.documentum.xml.dds.persistence.Store;
import java.util.Collection;

public interface StoreManager
extends Configurable {
    public Store getStore(String var1);

    public Collection<Store> getStores();

    public Store addStore(StoreConfiguration var1);

    public void removeStore(String var1);

    public void setDefaultStoreUser(String var1, StoreUserConfiguration var2);
}

