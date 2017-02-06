/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.structure.internal;

import com.emc.documentum.xml.dds.persistence.Container;
import com.emc.documentum.xml.dds.persistence.Location;
import com.emc.documentum.xml.dds.structure.Structure;
import java.util.List;

public abstract class AbstractStructure
implements Structure {
    @Override
    public Location getLocation(String relativePath) {
        Location result = this.getRootLocation().getDescendantLocation(relativePath);
        result.setContext(this);
        return result;
    }

    @Override
    public Container getContainer(String relativePath, String name) {
        Container result = this.getRootLocation().getDescendantContainer(relativePath, name);
        result.setContext(this);
        return result;
    }

    @Override
    public List<String> getRelativePath(Location location) {
        List<String> pathComponents = location.getPathComponents();
        return pathComponents.subList(this.getRootLocation().getPathComponents().size(), pathComponents.size());
    }
}

