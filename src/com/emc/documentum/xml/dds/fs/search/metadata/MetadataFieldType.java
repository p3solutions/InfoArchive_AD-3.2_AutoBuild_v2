/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.fs.search.metadata;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

@XmlType(name="MetadataFieldType", namespace="http://metadata.search.datamodel.dds.xml.documentum.emc.com/", propOrder={"STRING", "BOOLEAN", "NUMBER", "DATE_TIME"})
@XmlEnum
public enum MetadataFieldType {
    STRING,
    BOOLEAN,
    NUMBER,
    DATE_TIME;
    

    private MetadataFieldType() {
    }
}

