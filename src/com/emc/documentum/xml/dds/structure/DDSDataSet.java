/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.structure;

import com.emc.documentum.xml.dds.persistence.Container;
import com.emc.documentum.xml.dds.structure.DDSLocale;
import com.emc.documentum.xml.dds.structure.Structure;
import com.emc.documentum.xml.dds.structure.StructureStrategy;
import com.emc.documentum.xml.dds.structure.internal.RootStructure;
import java.util.List;

public interface DDSDataSet
extends Structure {
    public Container getMetadataContainer(Container var1);

    public String getAlias();

    public void setAlias(String var1);

    public boolean isLocaleAware();

    public void addLocale(DDSLocale var1);

    public void removeLocale(DDSLocale var1);

    public DDSLocale getLocale(String var1);

    public List<DDSLocale> getLocales();

    public String getDefaultLocale();

    public void setDefaultLocale(String var1);

    public StructureStrategy getStructureStrategy();

    public void setStructureStrategy(StructureStrategy var1);

    public void setRootStructure(RootStructure var1);
}

