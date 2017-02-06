/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.internal.webservice.xquery;

import com.emc.documentum.xml.dds.internal.webservice.xquery.ResultValue;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(name="AttributeValue", namespace="http://xquery.datamodel.dds.xml.documentum.emc.com/")
@XmlAccessorType(value=XmlAccessType.FIELD)
public class AttributeValue
extends ResultValue {
    @XmlElement(name="Value", namespace="http://xquery.datamodel.dds.xml.documentum.emc.com/")
    private String value;
    @XmlElement(name="LocalName", namespace="http://xquery.datamodel.dds.xml.documentum.emc.com/")
    private String localName;
    @XmlElement(name="NamespaceURI", namespace="http://xquery.datamodel.dds.xml.documentum.emc.com/")
    private String namespaceURI;

    public AttributeValue() {
    }

    public AttributeValue(String type, String localName, String namespaceURI, String value) {
        super(type);
        this.localName = localName;
        this.namespaceURI = namespaceURI;
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
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

