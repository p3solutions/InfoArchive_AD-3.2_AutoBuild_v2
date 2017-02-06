/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.internal.webservice.search.metadata;

import com.emc.documentum.xml.dds.fs.search.metadata.MetadataFieldType;
import com.emc.documentum.xml.dds.fs.search.metadata.SortOrder;
import com.emc.documentum.xml.dds.internal.webservice.search.metadata.MetadataField;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(name="SortExpression", namespace="http://metadata.search.datamodel.dds.xml.documentum.emc.com/")
@XmlAccessorType(value=XmlAccessType.FIELD)
public class SortExpression {
    @XmlElement(name="MetadataField", namespace="http://metadata.search.datamodel.dds.xml.documentum.emc.com/")
    private MetadataField metadataField;
    @XmlElement(name="MetadataFieldType", namespace="http://metadata.search.datamodel.dds.xml.documentum.emc.com/")
    private MetadataFieldType metadataFieldType;
    @XmlElement(name="SortOrder", namespace="http://metadata.search.datamodel.dds.xml.documentum.emc.com/")
    private SortOrder sortOrder;

    public SortExpression() {
    }

    public SortExpression(MetadataField metadataField, SortOrder sortOrder) {
        this.metadataField = metadataField;
        this.sortOrder = sortOrder;
    }

    public MetadataField getMetadataField() {
        return this.metadataField;
    }

    public void setMetadataField(MetadataField metadataField) {
        this.metadataField = metadataField;
    }

    public MetadataFieldType getMetadataFieldType() {
        return this.metadataFieldType;
    }

    public void setMetadataFieldType(MetadataFieldType metadataFieldType) {
        this.metadataFieldType = metadataFieldType;
    }

    public SortOrder getSortOrder() {
        return this.sortOrder;
    }

    public void setSortOrder(SortOrder sortOrder) {
        this.sortOrder = sortOrder;
    }

    public com.emc.documentum.xml.dds.fs.search.metadata.SortExpression getSortItem() {
        return new com.emc.documentum.xml.dds.fs.search.metadata.SortExpression(){

            @Override
            public com.emc.documentum.xml.dds.fs.search.metadata.MetadataField getMetadataField() {
                return SortExpression.this.metadataField.getMetadataField();
            }

            @Override
            public MetadataFieldType getMetadataFieldType() {
                return SortExpression.this.metadataFieldType;
            }

            @Override
            public SortOrder getSortOrder() {
                return SortExpression.this.sortOrder;
            }
        };
    }

}

