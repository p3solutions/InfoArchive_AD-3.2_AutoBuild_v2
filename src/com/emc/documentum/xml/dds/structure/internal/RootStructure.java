/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.structure.internal;

import com.emc.documentum.xml.dds.persistence.Container;
import com.emc.documentum.xml.dds.persistence.Location;
import com.emc.documentum.xml.dds.persistence.Store;
import com.emc.documentum.xml.dds.structure.DDSDataSet;
import com.emc.documentum.xml.dds.structure.Structure;
import com.emc.documentum.xml.dds.structure.StructureType;
import com.emc.documentum.xml.dds.structure.internal.AbstractStructure;
import com.emc.documentum.xml.dds.structure.internal.ApplicationStructure;
import com.emc.documentum.xml.dds.structure.internal.SystemStructure;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class RootStructure
extends AbstractStructure {
    private final Store store;
    private final Map<String, DDSDataSet> dataSetsByAlias;
    private final Map<String, DDSDataSet> dataSetsById;
    private final Map<String, ApplicationStructure> applicationStructures;
    private final SystemStructure systemStructure;

    public RootStructure(Store store) {
        this.store = store;
        this.dataSetsByAlias = new HashMap<String, DDSDataSet>();
        this.dataSetsById = new HashMap<String, DDSDataSet>();
        this.applicationStructures = new HashMap<String, ApplicationStructure>();
        this.systemStructure = new SystemStructure(this);
    }

    public void addDDSDataSet(DDSDataSet dataSet) {
        this.dataSetsByAlias.put(dataSet.getAlias(), dataSet);
        this.dataSetsById.put(dataSet.getId(), dataSet);
    }

    public DDSDataSet getDDSDataSetByAlias(String dataSetAlias) {
        return this.dataSetsByAlias.get(dataSetAlias);
    }

    public DDSDataSet getDDSDataSetById(String dataSetId) {
        return this.dataSetsById.get(dataSetId);
    }

    public void addApplicationStructure(ApplicationStructure applicationStructure) {
        this.applicationStructures.put(applicationStructure.getId(), applicationStructure);
    }

    public ApplicationStructure getApplicationStructure(String applicationId) {
        return this.applicationStructures.get(applicationId);
    }

    public SystemStructure getSystemStructure() {
        return this.systemStructure;
    }

    @Override
    public String getId() {
        return this.store.getAlias();
    }

    @Override
    public StructureType getType() {
        return StructureType.STOREROOT;
    }

    public Store getStore() {
        return this.store;
    }

    @Override
    public RootStructure getRootStructure() {
        return this;
    }

    @Override
    public Location getRootLocation() {
        return this.store.getLocation("");
    }

    @Override
    public Location getLocation(String relativePath) {
        Location result = this.store.getLocation(relativePath);
        result.setContext(this);
        return result;
    }

    @Override
    public Container getContainer(String relativePath, String name) {
        Container result = this.store.getContainer(relativePath, name);
        result.setContext(this);
        return result;
    }

    @Override
    public Structure getParentStructure() {
        return null;
    }

    @Override
    public Location getRootLocation(Structure child) {
        if (child instanceof DDSDataSet) {
            return this.store.getLocation("DATA/" + child.getId());
        }
        if (child instanceof ApplicationStructure) {
            return this.store.getLocation("APPLICATIONS/" + child.getId());
        }
        if (child instanceof SystemStructure) {
            return this.store.getLocation("SYSTEM");
        }
        return null;
    }

    @Override
    public Structure resolveContext(List<String> relativePathComponents) {
        if (relativePathComponents == null || relativePathComponents.size() == 0) {
            return this;
        }
        if ("DATA".equals(relativePathComponents.get(0))) {
            DDSDataSet dataSet = this.dataSetsById.get(relativePathComponents.get(1));
            return dataSet == null ? this : dataSet.resolveContext(relativePathComponents.subList(2, relativePathComponents.size()));
        }
        if ("APPLICATIONS".equals(relativePathComponents.get(0))) {
            ApplicationStructure applicationStructure = this.applicationStructures.get(relativePathComponents.get(1));
            return applicationStructure == null ? this : applicationStructure.resolveContext(relativePathComponents.subList(2, relativePathComponents.size()));
        }
        if ("SYSTEM".equals(relativePathComponents.get(0))) {
            return this.systemStructure.resolveContext(relativePathComponents.subList(1, relativePathComponents.size()));
        }
        return this;
    }

    public Container getDataSetConfigurationContainer(String dataSetId) {
        return this.getContainer("DATA", dataSetId + ".xml");
    }
}

