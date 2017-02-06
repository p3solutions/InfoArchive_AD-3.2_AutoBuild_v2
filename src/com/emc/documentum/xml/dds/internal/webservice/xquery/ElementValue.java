/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.internal.webservice.xquery;

import com.emc.documentum.xml.dds.internal.webservice.xquery.ResultValue;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(name="ElementValue", namespace="http://xquery.datamodel.dds.xml.documentum.emc.com/")
@XmlAccessorType(value=XmlAccessType.FIELD)
public class ElementValue
extends ResultValue {
    @XmlElement(name="XMLValue", namespace="http://xquery.datamodel.dds.xml.documentum.emc.com/")
    private String value;
    @XmlElement(name="LocalName", namespace="http://xquery.datamodel.dds.xml.documentum.emc.com/")
    private String localName;
    @XmlElement(name="NamespaceURI", namespace="http://xquery.datamodel.dds.xml.documentum.emc.com/")
    private String namespaceURI;

    public ElementValue() {
    }

    public ElementValue(String type, String localName, String namespaceURI, String xmlValue) {
        super(type);
        this.localName = localName;
        this.namespaceURI = namespaceURI;
        this.value = xmlValue;
    }

    public String getXMLValue() {
        return this.value;
    }

    public void setXMLValue(String xmlValue) {
        this.value = xmlValue;
    }

    public String getLocalName() {
        return this.localName;
    }

    public void setLocalName(String localName) {
        this.localName = localName;
    }

    public String getNamespaceURI() {
        return this.namespaceURI;
    }

    public void setNamespaceURI(String namespaceURI) {
        this.namespaceURI = namespaceURI;
    }
}

