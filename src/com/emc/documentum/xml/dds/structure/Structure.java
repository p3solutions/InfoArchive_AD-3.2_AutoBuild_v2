/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.structure;

import com.emc.documentum.xml.dds.persistence.Container;
import com.emc.documentum.xml.dds.persistence.Location;
import com.emc.documentum.xml.dds.structure.StructureType;
import com.emc.documentum.xml.dds.structure.internal.RootStructure;
import java.util.List;

public interface Structure {
    public String getId();

    public StructureType getType();

    public RootStructure getRootStructure();

    public Location getRootLocation();

    public Location getLocation(String var1);

    public Container getContainer(String var1, String var2);

    public List<String> getRelativePath(Location var1);

    public Structure getParentStructure();

    public Location getRootLocation(Structure var1);

    public Structure resolveContext(List<String> var1);
}

