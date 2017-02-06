/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.configuration;

import com.emc.documentum.xml.dds.configuration.Configuration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ConfigurationList<T extends Configuration>
implements Configuration {
    private static final long serialVersionUID = 1;
    private List<T> configurations;

    public List<T> getList() {
        if (this.configurations == null) {
            this.configurations = new ArrayList<T>();
        }
        return new ArrayList<T>(this.configurations);
    }

    public int size() {
        return this.getList().size();
    }

    public void add(T configuration) {
        if (this.configurations == null) {
            this.configurations = new ArrayList<T>();
        }
        this.configurations.add(configuration);
    }

    public void remove(T configuration) {
        if (this.configurations != null) {
            this.configurations.remove(configuration);
        }
    }

    public boolean contains(T configuration) {
        return this.configurations == null ? false : this.configurations.contains(configuration);
    }
}

