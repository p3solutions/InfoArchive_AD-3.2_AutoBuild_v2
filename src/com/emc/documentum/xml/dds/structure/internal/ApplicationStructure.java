/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.structure.internal;

import com.emc.documentum.xml.dds.persistence.Location;
import com.emc.documentum.xml.dds.structure.Structure;
import com.emc.documentum.xml.dds.structure.StructureType;
import com.emc.documentum.xml.dds.structure.internal.AbstractStructure;
import com.emc.documentum.xml.dds.structure.internal.ConfigurationStructure;
import com.emc.documentum.xml.dds.structure.internal.ResourceStructure;
import com.emc.documentum.xml.dds.structure.internal.RootStructure;
import com.emc.documentum.xml.dds.structure.internal.UserStructure;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ApplicationStructure
extends AbstractStructure {
    private final String applicationId;
    private final RootStructure parent;
    private final Map<String, UserStructure> userMap;
    private final ResourceStructure resourceStructure;
    private final ConfigurationStructure configurationStructure;

    public ApplicationStructure(String applicationId, RootStructure parent) {
        this.applicationId = applicationId;
        this.parent = parent;
        this.userMap = new HashMap<String, UserStructure>();
        this.resourceStructure = new ResourceStructure(this);
        this.configurationStructure = new ConfigurationStructure(this);
    }

    public void addUserStructure(UserStructure structure) {
        this.userMap.put(structure.getId(), structure);
    }

    public UserStructure getUserStructure(String userId) {
        return this.userMap.get(userId);
    }

    public ResourceStructure getResourceStructure() {
        return this.resourceStructure;
    }

    public ConfigurationStructure getConfigurationStructure() {
        return this.configurationStructure;
    }

    @Override
    public String getId() {
        return this.applicationId;
    }

    @Override
    public StructureType getType() {
        return StructureType.APPLICATION;
    }

    @Override
    public RootStructure getRootStructure() {
        return this.parent;
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
        if (child instanceof UserStructure) {
            return this.getRootLocation().getChildLocation("users").getChildLocation(((UserStructure)child).getId());
        }
        if (child instanceof ResourceStructure) {
            return this.getRootLocation().getChildLocation("resources");
        }
        if (child instanceof ConfigurationStructure) {
            return this.getRootLocation().getChildLocation("configuration");
        }
        return null;
    }

    @Override
    public Structure resolveContext(List<String> relativePathComponents) {
        if (relativePathComponents.size() == 0) {
            return this;
        }
        if ("users".equals(relativePathComponents.get(0))) {
            if (relativePathComponents.size() == 1) {
                return this;
            }
            UserStructure userStructure = this.userMap.get(relativePathComponents.get(1));
            return userStructure == null ? this : userStructure.resolveContext(relativePathComponents.subList(2, relativePathComponents.size()));
        }
        if ("resources".equals(relativePathComponents.get(0))) {
            return this.resourceStructure.resolveContext(relativePathComponents.subList(1, relativePathComponents.size()));
        }
        if ("configuration".equals(relativePathComponents.get(0))) {
            return this.configurationStructure.resolveContext(relativePathComponents.subList(1, relativePathComponents.size()));
        }
        return this;
    }
}

