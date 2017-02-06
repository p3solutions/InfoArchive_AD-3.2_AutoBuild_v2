/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.configuration.baseline;

import com.emc.documentum.xml.dds.configuration.Configuration;
import com.emc.documentum.xml.dds.service.ServiceType;
import java.util.ArrayList;
import java.util.List;

public class ServiceConfiguration
implements Configuration {
    private final List<String> dependencies = new ArrayList<String>();
    private ServiceType type;
    private String name;
    private String className;

    public List<String> getDependencies() {
        if (this.dependencies == null) {
            return new ArrayList<String>();
        }
        ArrayList<String> result = new ArrayList<String>();
        for (String dependency : this.dependencies) {
            result.add(dependency.trim());
        }
        return result;
    }

    public void addDependency(String serviceName) {
        if (!this.dependencies.contains(serviceName)) {
            this.dependencies.add(serviceName);
        }
    }

    public ServiceType getType() {
        return this.type;
    }

    public void setType(ServiceType type) {
        this.type = type;
    }

    public String getName() {
        return this.name == null ? null : this.name.trim();
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getClassName() {
        return this.className == null ? null : this.className.trim();
    }

    public void setClassName(String className) {
        this.className = className;
    }
}

