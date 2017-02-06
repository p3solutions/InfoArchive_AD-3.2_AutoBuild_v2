/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.internal.webservice.xproc;

import com.emc.documentum.xml.dds.internal.webservice.QName;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(name="Option", namespace="http://xproc.datamodel.dds.xml.documentum.emc.com/")
@XmlAccessorType(value=XmlAccessType.FIELD)
public class Option {
    @XmlElement(name="QName", namespace="http://xproc.datamodel.dds.xml.documentum.emc.com/")
    private QName qname;
    @XmlElement(name="Value", namespace="http://xproc.datamodel.dds.xml.documentum.emc.com/")
    private String value;

    public Option(QName qname, String value) {
        this.qname = qname;
        this.value = value;
    }

    public Option() {
    }

    public QName getQname() {
        return this.qname;
    }

    public void setQname(QName qname) {
        this.qname = qname;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}

