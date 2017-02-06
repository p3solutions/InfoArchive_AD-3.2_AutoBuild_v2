/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.internal.webservice.xquery;

import com.emc.documentum.xml.dds.internal.webservice.xquery.ResultValue;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(name="DocumentValue", namespace="http://xquery.datamodel.dds.xml.documentum.emc.com/")
@XmlAccessorType(value=XmlAccessType.FIELD)
public class DocumentValue
extends ResultValue {
    @XmlElement(name="XMLValue", namespace="http://xquery.datamodel.dds.xml.documentum.emc.com/")
    private String xmlValue;

    public DocumentValue() {
    }

    public DocumentValue(String type, String xmlValue) {
        super(type);
        this.xmlValue = xmlValue;
    }

    public String getXMLValue() {
        return this.xmlValue;
    }

    public void setXMLValue(String value) {
        this.xmlValue = value;
    }
}

