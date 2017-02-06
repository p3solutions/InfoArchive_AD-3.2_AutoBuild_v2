/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.serialization.xstream;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XStreamClassSerializationOptions {
    private Class<?> objectClass;
    private String classAlias;
    private final List<String> omittedFields = new ArrayList<String>();
    private final List<String> attributes = new ArrayList<String>();
    private final List<String> defaultImplicitCollections = new ArrayList<String>();
    private final Map<String, Map<Class<?>, String>> entryAliases = new HashMap();
    private final Map<String, String> fieldAliases = new HashMap<String, String>();
    private final Map<String, String> attributeAliases = new HashMap<String, String>();

    public Class<?> getObjectClass() {
        return this.objectClass;
    }

    public void setObjectClass(Class<?> objectClass) {
        this.objectClass = objectClass;
    }

    public String getClassAlias() {
        return this.classAlias;
    }

    public List<String> getOmittedFields() {
        return this.omittedFields;
    }

    public List<String> getAttributes() {
        return this.attributes;
    }

    public List<String> getDefaultImplicitCollections() {
        return this.defaultImplicitCollections;
    }

    public Map<String, Map<Class<?>, String>> getEntryAliases() {
        return this.entryAliases;
    }

    public Map<String, String> getFieldAliases() {
        return this.fieldAliases;
    }

    public Map<String, String> getAttributeAliases() {
        return this.attributeAliases;
    }

    public void addOmittedField(String fieldName) {
        if (!this.omittedFields.contains(fieldName)) {
            this.omittedFields.add(fieldName);
        }
    }

    public void addAttribute(String fieldName) {
        if (!this.attributes.contains(fieldName)) {
            this.attributes.add(fieldName);
        }
    }

    public void addDefaultImplicitCollection(String fieldName) {
        if (!this.defaultImplicitCollections.contains(fieldName)) {
            this.defaultImplicitCollections.add(fieldName);
        }
    }

    public void addEntryAlias(String fieldName, Class<?> entryClass, String alias) {
        Map classMap = this.entryAliases.get(fieldName);
        if (classMap == null) {
            classMap = new HashMap();
            this.entryAliases.put(fieldName, classMap);
        }
        classMap.put(entryClass, alias);
    }

    public void setClassAlias(String alias) {
        this.classAlias = alias;
    }

    public void addFieldAlias(String fieldName, String alias) {
        this.fieldAliases.put(fieldName, alias);
    }

    public void addAttributeAlias(String attributeName, String alias) {
        this.attributeAliases.put(attributeName, alias);
    }
}

