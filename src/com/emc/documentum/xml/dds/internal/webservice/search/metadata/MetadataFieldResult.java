/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.internal.webservice.search.metadata;

import com.emc.documentum.xml.dds.internal.webservice.search.metadata.MetadataField;
import com.emc.documentum.xml.dds.internal.webservice.search.metadata.MetadataFieldValue;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(name="MetadataFieldResult", namespace="http://metadata.search.datamodel.dds.xml.documentum.emc.com/")
@XmlAccessorType(value=XmlAccessType.FIELD)
public class MetadataFieldResult {
    @XmlElement(name="MetadataField", namespace="http://metadata.search.datamodel.dds.xml.documentum.emc.com/")
    private MetadataField metadataField;
    @XmlElement(name="MetadataFieldValue", namespace="http://metadata.search.datamodel.dds.xml.documentum.emc.com/")
    private MetadataFieldValue metadataFieldValue;

    public MetadataFieldResult() {
    }

    public MetadataFieldResult(MetadataField metadataField, MetadataFieldValue metadataFieldValue) {
        this.metadataField = metadataField;
        this.metadataFieldValue = metadataFieldValue;
    }

    public MetadataField getMetadataField() {
        return this.metadataField;
    }

    public void setMetadataField(MetadataField metadataField) {
        this.metadataField = metadataField;
    }

    public MetadataFieldValue getMetadataFieldValue() {
        return this.metadataFieldValue;
    }

    public void setMetadataFieldValue(MetadataFieldValue metadataFieldValue) {
        this.metadataFieldValue = metadataFieldValue;
    }
}

