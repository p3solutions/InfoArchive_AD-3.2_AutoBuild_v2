/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  com.emc.documentum.fs.datamodel.core.ObjectId
 */
package com.emc.documentum.xml.dds.internal.webservice.xquery;

import com.emc.documentum.fs.datamodel.core.ObjectId;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(name="XQueryContext", namespace="http://xquery.datamodel.dds.xml.documentum.emc.com/")
@XmlAccessorType(value=XmlAccessType.FIELD)
public class XQueryContext {
    @XmlElement(name="ContextId", namespace="http://xquery.datamodel.dds.xml.documentum.emc.com/")
    private ObjectId contextId;

    public XQueryContext() {
    }

    public com.emc.documentum.xml.dds.fs.xquery.XQueryContext getXQueryContext() {
        return new com.emc.documentum.xml.dds.fs.xquery.XQueryContext(){

            @Override
            public String getURI() {
                return XQueryContext.this.contextId != null ? XQueryContext.this.contextId.getId() : null;
            }
        };
    }

    public XQueryContext(ObjectId contextId) {
        this.contextId = contextId;
    }

    public void setContextId(ObjectId contextId) {
        this.contextId = contextId;
    }

    public ObjectId getContextId() {
        return this.contextId;
    }

}

