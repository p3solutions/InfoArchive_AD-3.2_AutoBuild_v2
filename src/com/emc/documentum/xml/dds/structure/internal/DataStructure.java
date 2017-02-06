/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.structure.internal;

import com.emc.documentum.xml.dds.persistence.Container;
import com.emc.documentum.xml.dds.persistence.Location;
import com.emc.documentum.xml.dds.structure.DDSDataSet;
import com.emc.documentum.xml.dds.structure.DDSLocale;
import com.emc.documentum.xml.dds.structure.Structure;
import com.emc.documentum.xml.dds.structure.StructureType;
import com.emc.documentum.xml.dds.structure.internal.AbstractStructure;
import com.emc.documentum.xml.dds.structure.internal.RootStructure;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataStructure
extends AbstractStructure {
    private final DDSDataSet parent;
    private final Map<String, DDSLocale> localeMap;

    public DataStructure(DDSDataSet parent) {
        this.parent = parent;
        this.localeMap = new HashMap<String, DDSLocale>();
    }

    public void addLocale(DDSLocale locale) {
        this.localeMap.put(locale.getId(), locale);
    }

    public void removeLocale(DDSLocale locale) {
        this.localeMap.remove(locale.getId());
    }

    public DDSLocale getLocale(String localeId) {
        return this.localeMap.get(localeId);
    }

    public List<DDSLocale> getLocales() {
        ArrayList<DDSLocale> result = new ArrayList<DDSLocale>();
        result.addAll(this.localeMap.values());
        return result;
    }

    @Override
    public String getId() {
        return StructureType.DATA.toString();
    }

    @Override
    public StructureType getType() {
        return StructureType.DATA;
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
    public Location getLocation(String relativePath) {
        Location result = this.getRootLocation().getDescendantLocation(relativePath);
        result.setContext(this.parent);
        return result;
    }

    @Override
    public Container getContainer(String relativePath, String name) {
        Container result = this.getRootLocation().getDescendantContainer(relativePath, name);
        result.setContext(this.parent);
        return result;
    }

    @Override
    public Structure getParentStructure() {
        return this.parent;
    }

    @Override
    public Location getRootLocation(Structure child) {
        return this.getRootLocation().getDescendantLocation(child.getId());
    }

    @Override
    public Structure resolveContext(List<String> relativePathComponents) {
        return this;
    }
}

