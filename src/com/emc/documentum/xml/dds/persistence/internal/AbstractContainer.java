/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.persistence.internal;

import com.emc.documentum.xml.dds.persistence.Container;
import com.emc.documentum.xml.dds.persistence.Location;
import com.emc.documentum.xml.dds.persistence.Metadata;
import com.emc.documentum.xml.dds.persistence.MetadataScheme;
import com.emc.documentum.xml.dds.persistence.Store;
import com.emc.documentum.xml.dds.persistence.internal.AbstractStoreChild;
import com.emc.documentum.xml.dds.structure.Structure;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractContainer
extends AbstractStoreChild
implements Container {
    private final Location location;
    private final String name;
    private final Map<MetadataScheme, Metadata> metadataMap;

    protected AbstractContainer(Location location, String name) {
        this.location = location;
        this.setContext(location.getContext());
        this.name = name;
        this.metadataMap = new HashMap<MetadataScheme, Metadata>();
    }

    @Override
    public Location getLocation() {
        return this.location;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getPath() {
        return this.location == null ? null : this.location.getPath(true) + this.name;
    }

    @Override
    public String getCanonicalPath() {
        return this.location == null ? null : this.location.getCanonicalPath() + this.name;
    }

    @Override
    public Store getStore() {
        return this.location.getStore();
    }

    @Override
    public boolean isLocation() {
        return false;
    }

    @Override
    public boolean isContainer() {
        return true;
    }

    @Override
    public boolean isXMLNode() {
        return false;
    }

    public Metadata getMetadata(MetadataScheme scheme) {
        return this.metadataMap.get((Object)scheme);
    }

    public List<Metadata> getMetadata() {
        ArrayList<Metadata> result = new ArrayList<Metadata>();
        result.addAll(this.metadataMap.values());
        return result;
    }

    public void addMetadata(Metadata metadata) {
        if (metadata != null) {
            this.metadataMap.put(metadata.getScheme(), metadata);
        }
    }
}

