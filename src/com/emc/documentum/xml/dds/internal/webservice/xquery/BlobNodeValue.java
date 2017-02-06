/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.internal.webservice.xquery;

import com.emc.documentum.xml.dds.internal.webservice.xquery.ResultValue;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(name="BlobNodeValue", namespace="http://xquery.datamodel.dds.xml.documentum.emc.com/")
@XmlAccessorType(value=XmlAccessType.FIELD)
public class BlobNodeValue
extends ResultValue {
    @XmlElement(name="Bytes", namespace="http://xquery.datamodel.dds.xml.documentum.emc.com/")
    private byte[] bytes;

    public BlobNodeValue() {
    }

    public BlobNodeValue(String type, byte[] bytes) {
        super(type);
        this.bytes = bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    public byte[] getBytes() {
        return this.bytes;
    }
}

