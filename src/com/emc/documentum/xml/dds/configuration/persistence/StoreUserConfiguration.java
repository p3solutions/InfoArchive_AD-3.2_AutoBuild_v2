/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.configuration.persistence;

import com.emc.documentum.xml.dds.configuration.Configuration;
import com.emc.documentum.xml.dds.persistence.StoreType;

public interface StoreUserConfiguration
extends Configuration {
    public StoreType getType();
}

