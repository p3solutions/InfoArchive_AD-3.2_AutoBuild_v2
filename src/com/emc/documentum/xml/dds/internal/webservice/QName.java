/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.internal.webservice;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(name="QName", namespace="http://datamodel.dds.xml.documentum.emc.com/")
public class QName
implements Serializable {
    @XmlElement(name="NamespaceURI", namespace="http://datamodel.dds.xml.documentum.emc.com/")
    private String namespaceURI;
    @XmlElement(name="LocalPart", namespace="http://datamodel.dds.xml.documentum.emc.com/")
    private final String localPart;
    @XmlElement(name="Prefix", namespace="http://datamodel.dds.xml.documentum.emc.com/")
    private final String prefix;

    public QName() {
        this("", "", "");
    }

    public QName(String namespaceURI, String localPart) {
        this(namespaceURI, localPart, "");
    }

    public QName(String namespaceURI, String localPart, String prefix) {
        this.namespaceURI = namespaceURI == null ? "" : namespaceURI;
        if (localPart == null) {
            throw new IllegalArgumentException("local part cannot be \"null\" when creating a QName");
        }
        this.localPart = localPart;
        if (prefix == null) {
            throw new IllegalArgumentException("prefix cannot be \"null\" when creating a QName");
        }
        this.prefix = prefix;
    }

    public QName(String localPart) {
        this("", localPart, "");
    }

    public String getNamespaceURI() {
        return this.namespaceURI;
    }

    public String getLocalPart() {
        return this.localPart;
    }

    public String getPrefix() {
        return this.prefix;
    }

    public final boolean equals(Object objectToTest) {
        if (!(objectToTest instanceof QName)) {
            return false;
        }
        QName qName = (QName)objectToTest;
        return this.namespaceURI.equals(qName.namespaceURI) && this.localPart.equals(qName.localPart);
    }

    public final int hashCode() {
        return this.namespaceURI.hashCode() ^ this.localPart.hashCode();
    }

    public String toString() {
        if (this.namespaceURI.equals("")) {
            return this.localPart;
        }
        return "{" + this.namespaceURI + "}" + this.localPart;
    }
}

