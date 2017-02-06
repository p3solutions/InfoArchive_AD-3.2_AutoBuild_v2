/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.structure.internal;

import com.emc.documentum.xml.dds.persistence.Location;
import com.emc.documentum.xml.dds.structure.Structure;
import com.emc.documentum.xml.dds.structure.StructureType;
import com.emc.documentum.xml.dds.structure.internal.AbstractStructure;
import com.emc.documentum.xml.dds.structure.internal.ApplicationStructure;
import com.emc.documentum.xml.dds.structure.internal.RootStructure;
import java.util.List;

public class ConfigurationStructure
extends AbstractStructure {
    private final ApplicationStructure parent;

    public ConfigurationStructure(ApplicationStructure parent) {
        this.parent = parent;
    }

    @Override
    public String getId() {
        return StructureType.CONFIGURATION.toString();
    }

    @Override
    public StructureType getType() {
        return StructureType.CONFIGURATION;
    }

    @Override
    public RootStructure getRootStructure() {
        return this.parent.getRootStructure();
    }

    @Override
    public Location getRootLocation() {
        return this.parent.getRootLocation(this);
    }

    @Override
    public Structure getParentStructure() {
        return this.parent;
    }

    @Override
    public Location getRootLocation(Structure child) {
        return null;
    }

    @Override
    public Structure resolveContext(List<String> relativePathComponents) {
        return this;
    }
}

