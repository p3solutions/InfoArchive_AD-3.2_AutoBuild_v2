/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.internal.webservice.xproc;

import com.emc.documentum.xml.dds.internal.webservice.QName;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(name="Parameter", namespace="http://xproc.datamodel.dds.xml.documentum.emc.com/")
@XmlAccessorType(value=XmlAccessType.FIELD)
public class Parameter {
    @XmlElement(name="QName", namespace="http://xproc.datamodel.dds.xml.documentum.emc.com/")
    private QName qName;
    @XmlElement(name="Value", namespace="http://xproc.datamodel.dds.xml.documentum.emc.com/")
    private String value;

    public Parameter(QName qName, String value) {
        this.qName = qName;
        this.value = value;
    }

    public Parameter() {
    }

    public QName getQName() {
        return this.qName;
    }

    public void setQName(QName qname) {
        this.qName = qname;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}

