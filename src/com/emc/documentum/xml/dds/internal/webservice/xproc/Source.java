/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  com.emc.documentum.fs.datamodel.core.ObjectId
 */
package com.emc.documentum.xml.dds.internal.webservice.xproc;

import com.emc.documentum.fs.datamodel.core.ObjectId;
import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(name="Source", namespace="http://xproc.datamodel.dds.xml.documentum.emc.com/")
@XmlAccessorType(value=XmlAccessType.FIELD)
public class Source
implements Serializable {
    @XmlElement(name="Data", namespace="http://xproc.datamodel.dds.xml.documentum.emc.com/")
    private String data;
    @XmlElement(name="PublicId", namespace="http://xproc.datamodel.dds.xml.documentum.emc.com/")
    private ObjectId publicId;
    @XmlElement(name="SystemId", namespace="http://xproc.datamodel.dds.xml.documentum.emc.com/")
    private ObjectId systemId;

    public Source() {
    }

    public Source(ObjectId systemId) {
        this(null, null, systemId);
    }

    public Source(String data, ObjectId publicId, ObjectId systemId) {
        this.data = data;
        this.publicId = publicId;
        this.systemId = systemId;
    }

    public String getData() {
        return this.data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public ObjectId getSystemId() {
        return this.systemId;
    }

    public void setSystemId(ObjectId systemId) {
        this.systemId = systemId;
    }

    public ObjectId getPublicId() {
        return this.publicId;
    }

    public void setPublicId(ObjectId publicId) {
        this.publicId = publicId;
    }
}

