/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  com.emc.documentum.fs.datamodel.core.ObjectId
 */
package com.emc.documentum.xml.dds.internal.webservice.search.metadata;

import com.emc.documentum.fs.datamodel.core.ObjectId;
import com.emc.documentum.xml.dds.internal.webservice.search.metadata.MetadataFieldResult;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(name="ResultValue", namespace="http://metadata.search.datamodel.dds.xml.documentum.emc.com/")
@XmlAccessorType(value=XmlAccessType.FIELD)
public class ResultValue {
    @XmlElement(name="ObjectId", namespace="http://metadata.search.datamodel.dds.xml.documentum.emc.com/")
    private ObjectId objectId;
    @XmlElement(name="XMLValue", namespace="http://metadata.search.datamodel.dds.xml.documentum.emc.com/")
    private String xmlValue;
    @XmlElement(name="MetadataFieldResult", namespace="http://metadata.search.datamodel.dds.xml.documentum.emc.com/")
    private List<MetadataFieldResult> metadataFieldResults;

    public ResultValue() {
        this.metadataFieldResults = new ArrayList<MetadataFieldResult>();
    }

    public ResultValue(List<MetadataFieldResult> metadataFieldResults, ObjectId objectId, String xmlValue) {
        this.metadataFieldResults = metadataFieldResults;
        this.objectId = objectId;
        this.xmlValue = xmlValue;
    }

    public ObjectId getObjectId() {
        return this.objectId;
    }

    public void setObjectId(ObjectId objectId) {
        this.objectId = objectId;
    }

    public String getXmlValue() {
        return this.xmlValue;
    }

    public void setXmlValue(String xmlValue) {
        this.xmlValue = xmlValue;
    }

    public List<MetadataFieldResult> getMetadataFieldResults() {
        return this.metadataFieldResults;
    }

    public void setMetadataFieldResults(List<MetadataFieldResult> metadataFieldResults) {
        this.metadataFieldResults = metadataFieldResults;
    }
}

