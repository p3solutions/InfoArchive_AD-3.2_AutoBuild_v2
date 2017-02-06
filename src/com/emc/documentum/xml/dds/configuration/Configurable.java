/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.configuration;

import com.emc.documentum.xml.dds.configuration.Configuration;

public interface Configurable {
    public boolean configure(Configuration var1);

    public boolean activateConfiguration();

    public Configuration getConfiguration();

    public void setConfiguration(Configuration var1);
}

