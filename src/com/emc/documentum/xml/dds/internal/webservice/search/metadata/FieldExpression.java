/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.internal.webservice.search.metadata;

import com.emc.documentum.xml.dds.fs.search.metadata.FieldOperator;
import com.emc.documentum.xml.dds.internal.webservice.search.metadata.MetadataField;
import com.emc.documentum.xml.dds.internal.webservice.search.metadata.MetadataFieldValue;
import com.emc.documentum.xml.dds.internal.webservice.search.metadata.SearchExpression;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(name="FieldExpression", namespace="http://metadata.search.datamodel.dds.xml.documentum.emc.com/")
@XmlAccessorType(value=XmlAccessType.FIELD)
public class FieldExpression
extends SearchExpression {
    @XmlElement(name="Operator", namespace="http://metadata.search.datamodel.dds.xml.documentum.emc.com/")
    private FieldOperator operator;
    @XmlElement(name="MetadataField", namespace="http://metadata.search.datamodel.dds.xml.documentum.emc.com/")
    private MetadataField metadataField;
    @XmlElement(name="MetadataFieldValue", namespace="http://metadata.search.datamodel.dds.xml.documentum.emc.com/")
    private MetadataFieldValue metadataFieldValue;

    public FieldExpression() {
    }

    public FieldExpression(MetadataField metadataField, MetadataFieldValue metadataFieldValue, FieldOperator operator) {
        this.metadataField = metadataField;
        this.metadataFieldValue = metadataFieldValue;
        this.operator = operator;
    }

    public FieldOperator getOperator() {
        return this.operator;
    }

    public void setOperator(FieldOperator operator) {
        this.operator = operator;
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

    public com.emc.documentum.xml.dds.fs.search.metadata.FieldExpression getFieldExpression() {
        return new com.emc.documentum.xml.dds.fs.search.metadata.FieldExpression(){

            @Override
            public com.emc.documentum.xml.dds.fs.search.metadata.MetadataField getMetadataField() {
                return FieldExpression.this.metadataField.getMetadataField();
            }

            @Override
            public FieldOperator getOperator() {
                return FieldExpression.this.operator;
            }

            @Override
            public com.emc.documentum.xml.dds.fs.search.metadata.MetadataFieldValue getMetadataFieldValue() {
                return FieldExpression.this.metadataFieldValue.getMetadataFieldValue();
            }
        };
    }

}

