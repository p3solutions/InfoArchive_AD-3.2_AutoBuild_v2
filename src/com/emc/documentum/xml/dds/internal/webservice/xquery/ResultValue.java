/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.internal.webservice.xquery;

import com.emc.documentum.xml.dds.internal.webservice.xquery.AttributeValue;
import com.emc.documentum.xml.dds.internal.webservice.xquery.BlobNodeValue;
import com.emc.documentum.xml.dds.internal.webservice.xquery.CDataValue;
import com.emc.documentum.xml.dds.internal.webservice.xquery.CommentValue;
import com.emc.documentum.xml.dds.internal.webservice.xquery.DocumentValue;
import com.emc.documentum.xml.dds.internal.webservice.xquery.ElementValue;
import com.emc.documentum.xml.dds.internal.webservice.xquery.ProcessingInstructionValue;
import com.emc.documentum.xml.dds.internal.webservice.xquery.SimpleValue;
import com.emc.documentum.xml.dds.internal.webservice.xquery.TextValue;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

@XmlType(name="ResultValue", namespace="http://xquery.datamodel.dds.xml.documentum.emc.com/")
@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlSeeAlso(value={AttributeValue.class, BlobNodeValue.class, CDataValue.class, CommentValue.class, DocumentValue.class, ElementValue.class, ProcessingInstructionValue.class, SimpleValue.class, TextValue.class})
public abstract class ResultValue {
    @XmlElement(name="Type", namespace="http://xquery.datamodel.dds.xml.documentum.emc.com/")
    private String type;

    public ResultValue() {
    }

    public ResultValue(String type) {
        this.type = type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return this.type;
    }
}

