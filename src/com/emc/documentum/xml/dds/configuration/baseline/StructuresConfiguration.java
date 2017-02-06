/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.configuration.baseline;

import com.emc.documentum.xml.dds.configuration.ConfigurationList;
import com.emc.documentum.xml.dds.configuration.baseline.DataSetConfiguration;
import com.emc.documentum.xml.dds.configuration.baseline.StructureConfiguration;
import java.util.ArrayList;
import java.util.List;

public class StructuresConfiguration
extends ConfigurationList<StructureConfiguration> {
    private String defaultDataSet;

    public String getDefaultDataSet() {
        return this.defaultDataSet == null ? null : this.defaultDataSet.trim();
    }

    public void setDefaultDataSet(String defaultDataSet) {
        this.defaultDataSet = defaultDataSet;
    }

    public List<DataSetConfiguration> getDataSetConfigurations() {
        List<StructureConfiguration> list = this.getList();
        ArrayList<DataSetConfiguration> result = new ArrayList<DataSetConfiguration>();
        for (StructureConfiguration configuration : list) {
            if (!(configuration instanceof DataSetConfiguration)) continue;
            result.add((DataSetConfiguration)configuration);
        }
        return result;
    }
}

