/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.internal.webservice.search.metadata;

import com.emc.documentum.xml.dds.fs.search.metadata.MetadataFieldType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(name="MetadataFieldValue", namespace="http://metadata.search.datamodel.dds.xml.documentum.emc.com/")
@XmlAccessorType(value=XmlAccessType.FIELD)
public class MetadataFieldValue {
    @XmlElement(name="StringValue", namespace="http://metadata.search.datamodel.dds.xml.documentum.emc.com/")
    private String stringValue;
    @XmlElement(name="MetadataFieldType", namespace="http://metadata.search.datamodel.dds.xml.documentum.emc.com/")
    private MetadataFieldType metadataFieldType;

    public MetadataFieldValue() {
    }

    public MetadataFieldValue(MetadataFieldType metadataFieldType, String stringValue) {
        this.metadataFieldType = metadataFieldType;
        this.stringValue = stringValue;
    }

    public String getStringValue() {
        return this.stringValue;
    }

    public void setStringValue(String stringValue) {
        this.stringValue = stringValue;
    }

    public MetadataFieldType getMetadataFieldType() {
        return this.metadataFieldType;
    }

    public void setMetadataFieldType(MetadataFieldType metadataFieldType) {
        this.metadataFieldType = metadataFieldType;
    }

    public com.emc.documentum.xml.dds.fs.search.metadata.MetadataFieldValue getMetadataFieldValue() {
        return new com.emc.documentum.xml.dds.fs.search.metadata.MetadataFieldValue(){

            @Override
            public String getStringValue() {
                return MetadataFieldValue.this.stringValue;
            }

            @Override
            public MetadataFieldType getMetadataFieldType() {
                return MetadataFieldValue.this.metadataFieldType;
            }
        };
    }

}

