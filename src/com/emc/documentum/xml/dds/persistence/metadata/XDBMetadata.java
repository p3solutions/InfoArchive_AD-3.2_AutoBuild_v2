/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.persistence.metadata;

import com.emc.documentum.xml.dds.persistence.Metadata;
import com.emc.documentum.xml.dds.persistence.MetadataScheme;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class XDBMetadata
implements Map<String, String>,
Metadata {
    private final Map<String, String> internalMap = new HashMap<String, String>();

    public XDBMetadata() {
    }

    public XDBMetadata(Map<String, String> metadataMap) {
        this();
        this.internalMap.putAll(metadataMap);
    }

    @Override
    public MetadataScheme getScheme() {
        return MetadataScheme.XDB;
    }

    @Override
    public void clear() {
        this.internalMap.clear();
    }

    @Override
    public boolean containsKey(Object arg0) {
        return this.internalMap.containsKey(arg0);
    }

    @Override
    public boolean containsValue(Object arg0) {
        return this.internalMap.containsValue(arg0);
    }

    @Override
    public Set<Map.Entry<String, String>> entrySet() {
        return this.internalMap.entrySet();
    }

    @Override
    public String get(Object arg0) {
        return this.internalMap.get(arg0);
    }

    @Override
    public boolean isEmpty() {
        return this.internalMap.isEmpty();
    }

    @Override
    public Set<String> keySet() {
        return this.internalMap.keySet();
    }

    @Override
    public String put(String arg0, String arg1) {
        return this.internalMap.put(arg0, arg1);
    }

    @Override
    public void putAll(Map<? extends String, ? extends String> arg0) {
        this.internalMap.putAll(arg0);
    }

    @Override
    public String remove(Object arg0) {
        return this.internalMap.remove(arg0);
    }

    @Override
    public int size() {
        return this.internalMap.size();
    }

    @Override
    public Collection<String> values() {
        return this.internalMap.values();
    }
}

