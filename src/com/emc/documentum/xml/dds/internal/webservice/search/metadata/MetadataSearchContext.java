/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  com.emc.documentum.fs.datamodel.core.ObjectId
 */
package com.emc.documentum.xml.dds.internal.webservice.search.metadata;

import com.emc.documentum.fs.datamodel.core.ObjectId;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(name="MetadataSearchContext", namespace="http://metadata.search.datamodel.dds.xml.documentum.emc.com/")
@XmlAccessorType(value=XmlAccessType.FIELD)
public class MetadataSearchContext {
    @XmlElement(name="ContextId", namespace="http://metadata.search.datamodel.dds.xml.documentum.emc.com/")
    private ObjectId contextId;

    public MetadataSearchContext() {
    }

    public com.emc.documentum.xml.dds.fs.search.metadata.MetadataSearchContext getMetadataSearchContext() {
        return new com.emc.documentum.xml.dds.fs.search.metadata.MetadataSearchContext(){

            @Override
            public String getURI() {
                return MetadataSearchContext.this.contextId != null ? MetadataSearchContext.this.contextId.getId() : null;
            }
        };
    }

    public MetadataSearchContext(ObjectId contextId) {
        this.contextId = contextId;
    }

    public void setContextId(ObjectId contextId) {
        this.contextId = contextId;
    }

    public ObjectId getContextId() {
        return this.contextId;
    }

}

