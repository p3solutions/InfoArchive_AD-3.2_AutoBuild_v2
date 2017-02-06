/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.internal.webservice.search.metadata;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(name="MetadataField", namespace="http://metadata.search.datamodel.dds.xml.documentum.emc.com/")
@XmlAccessorType(value=XmlAccessType.FIELD)
public class MetadataField {
    @XmlElement(name="Name", namespace="http://metadata.search.datamodel.dds.xml.documentum.emc.com/")
    private String name;

    public MetadataField() {
    }

    public MetadataField(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public com.emc.documentum.xml.dds.fs.search.metadata.MetadataField getMetadataField() {
        return new com.emc.documentum.xml.dds.fs.search.metadata.MetadataField(){

            @Override
            public String getName() {
                return MetadataField.this.name;
            }
        };
    }

}

