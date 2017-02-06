/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.structure.internal;

import com.emc.documentum.xml.dds.persistence.Container;
import com.emc.documentum.xml.dds.persistence.Location;
import com.emc.documentum.xml.dds.persistence.Store;
import com.emc.documentum.xml.dds.structure.DDSDataSet;
import com.emc.documentum.xml.dds.structure.DDSLocale;
import com.emc.documentum.xml.dds.structure.Structure;
import com.emc.documentum.xml.dds.structure.StructureStrategy;
import com.emc.documentum.xml.dds.structure.StructureType;
import com.emc.documentum.xml.dds.structure.exception.StructureException;
import com.emc.documentum.xml.dds.structure.internal.AbstractStructure;
import com.emc.documentum.xml.dds.structure.internal.DDSLocaleImpl;
import com.emc.documentum.xml.dds.structure.internal.DataStructure;
import com.emc.documentum.xml.dds.structure.internal.MetadataStructure;
import com.emc.documentum.xml.dds.structure.internal.RootStructure;
import com.emc.documentum.xml.dds.structure.strategy.DocumentumStructureStrategy;
import java.util.List;

public class DDSDataSetImpl
extends AbstractStructure
implements DDSDataSet {
    private final boolean localeAware;
    private String alias;
    private final String id;
    private String defaultLocale;
    private RootStructure storeRoot;
    private StructureStrategy structureStrategy;
    private DataStructure dataStructure;
    private MetadataStructure metadataStructure;

    public DDSDataSetImpl(String alias, String id, boolean localeAware, StructureStrategy strategy, RootStructure parent) {
        this.alias = alias;
        this.id = id;
        this.localeAware = localeAware;
        this.storeRoot = parent;
        this.structureStrategy = strategy;
        this.dataStructure = new DataStructure(this);
        this.metadataStructure = new MetadataStructure(this);
    }

    public DDSDataSetImpl(String alias, String id, String defaultLocale, StructureStrategy strategy, RootStructure parent) {
        this.alias = alias;
        this.id = id;
        this.localeAware = true;
        this.defaultLocale = defaultLocale;
        this.storeRoot = parent;
        this.structureStrategy = strategy;
        this.dataStructure = new DataStructure(this);
        this.metadataStructure = new MetadataStructure(this);
    }

    public DataStructure getDataStructure() {
        if (this.dataStructure == null) {
            this.dataStructure = new DataStructure(this);
        }
        return this.dataStructure;
    }

    public MetadataStructure getMetadataStructure() {
        if (this.metadataStructure == null) {
            this.metadataStructure = new MetadataStructure(this);
        }
        return this.metadataStructure;
    }

    @Override
    public boolean isLocaleAware() {
        return this.localeAware;
    }

    @Override
    public String getAlias() {
        return this.alias;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public void setAlias(String alias) {
        this.alias = alias;
    }

    @Override
    public StructureType getType() {
        return StructureType.DATASET;
    }

    @Override
    public RootStructure getRootStructure() {
        return this.storeRoot;
    }

    @Override
    public void setRootStructure(RootStructure structure) {
        this.storeRoot = structure;
    }

    @Override
    public Location getLocation(String relativePath) {
        if (this.isLocaleAware()) {
            return null;
        }
        if (this.dataStructure == null) {
            this.dataStructure = new DataStructure(this);
        }
        Location result = this.dataStructure.getLocation(relativePath);
        result.setContext(this);
        return result;
    }

    @Override
    public Container getContainer(String relativePath, String containerName) {
        if (this.isLocaleAware()) {
            return null;
        }
        if (this.dataStructure == null) {
            this.dataStructure = new DataStructure(this);
        }
        Container result = this.dataStructure.getContainer(relativePath, containerName);
        result.setContext(this);
        return result;
    }

    @Override
    public Container getMetadataContainer(Container container) {
        if (this.dataStructure == null) {
            this.dataStructure = new DataStructure(this);
        }
        if (this.metadataStructure == null) {
            this.metadataStructure = new MetadataStructure(this);
        }
        if (this.structureStrategy instanceof DocumentumStructureStrategy) {
            List<String> relativePath = this.dataStructure.getRelativePath(container.getLocation());
            String separator = this.getRootStructure().getStore().getSeparator();
            StringBuilder newRelativePath = new StringBuilder();
            for (String component : relativePath) {
                newRelativePath.append(component).append(separator);
            }
            return this.metadataStructure.getContainer(newRelativePath.toString(), container.getName());
        }
        return null;
    }

    @Override
    public void addLocale(DDSLocale locale) {
        if (this.dataStructure == null) {
            this.dataStructure = new DataStructure(this);
        }
        if (this.metadataStructure == null) {
            this.metadataStructure = new MetadataStructure(this);
        }
        this.dataStructure.addLocale(locale);
        try {
            this.metadataStructure.addLocale(new DDSLocaleImpl(locale.getId(), (Structure)this.metadataStructure));
        }
        catch (StructureException se) {
            // empty catch block
        }
    }

    @Override
    public void removeLocale(DDSLocale locale) {
        if (this.dataStructure != null) {
            this.dataStructure.removeLocale(locale);
            this.metadataStructure.removeLocale(locale);
        }
    }

    @Override
    public DDSLocale getLocale(String localeId) {
        if (this.dataStructure == null) {
            this.dataStructure = new DataStructure(this);
        }
        return this.dataStructure.getLocale(localeId);
    }

    @Override
    public List<DDSLocale> getLocales() {
        if (this.dataStructure == null) {
            this.dataStructure = new DataStructure(this);
        }
        return this.dataStructure.getLocales();
    }

    @Override
    public String getDefaultLocale() {
        return this.defaultLocale;
    }

    @Override
    public void setDefaultLocale(String defaultLocale) {
        this.defaultLocale = defaultLocale;
    }

    @Override
    public Location getRootLocation() {
        return this.storeRoot.getRootLocation(this);
    }

    @Override
    public Structure getParentStructure() {
        return this.storeRoot;
    }

    public void setParentStructure(Structure parent) {
        this.storeRoot = (RootStructure)parent;
    }

    @Override
    public Location getRootLocation(Structure child) {
        if (child instanceof DataStructure) {
            return this.structureStrategy instanceof DocumentumStructureStrategy ? this.getRootLocation().getDescendantLocation("Collection") : this.getRootLocation();
        }
        if (child instanceof MetadataStructure) {
            return this.structureStrategy instanceof DocumentumStructureStrategy ? this.getRootLocation().getDescendantLocation("CollectionMetadata") : null;
        }
        return null;
    }

    @Override
    public Structure resolveContext(List<String> relativePathComponents) {
        if (!this.localeAware) {
            return this;
        }
        if (relativePathComponents.size() < 2 || !(this.structureStrategy instanceof DocumentumStructureStrategy)) {
            return this;
        }
        DDSLocale locale = this.getLocale(relativePathComponents.get(1));
        return locale == null ? this : locale.resolveContext(relativePathComponents.subList(2, relativePathComponents.size()));
    }

    @Override
    public StructureStrategy getStructureStrategy() {
        return this.structureStrategy;
    }

    @Override
    public void setStructureStrategy(StructureStrategy strategy) {
        this.structureStrategy = strategy;
    }
}

