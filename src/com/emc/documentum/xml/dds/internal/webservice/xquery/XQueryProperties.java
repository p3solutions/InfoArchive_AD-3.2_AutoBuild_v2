/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  com.emc.documentum.fs.datamodel.core.properties.Property
 */
package com.emc.documentum.xml.dds.internal.webservice.xquery;

import com.emc.documentum.xml.dds.fs.Property;
import com.emc.documentum.xml.dds.internal.webservice.PropertySet;
import com.emc.documentum.xml.dds.internal.webservice.WebService2GenericService;
import java.util.Iterator;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(name="XQueryProperties", namespace="http://xquery.datamodel.dds.xml.documentum.emc.com/")
@XmlAccessorType(value=XmlAccessType.FIELD)
public class XQueryProperties
extends PropertySet {
    @XmlElement(name="ReadOnly", namespace="http://xquery.datamodel.dds.xml.documentum.emc.com/")
    private boolean readOnly;

    public boolean isReadOnly() {
        return this.readOnly;
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    public com.emc.documentum.xml.dds.fs.xquery.XQueryProperties getXQueryProperties() {
        return new com.emc.documentum.xml.dds.fs.xquery.XQueryProperties(){

            @Override
            public Iterator<Property> iterator() {
                return new Iterator<Property>(){
                    private Iterator<com.emc.documentum.fs.datamodel.core.properties.Property> iterator;

                    @Override
                    public boolean hasNext() {
                        return this.iterator.hasNext();
                    }

                    @Override
                    public Property next() {
                        com.emc.documentum.fs.datamodel.core.properties.Property property = this.iterator.next();
                        return WebService2GenericService.convert(property);
                    }

                    @Override
                    public void remove() {
                    }
                };
            }

            @Override
            public Property get(String name) {
                com.emc.documentum.fs.datamodel.core.properties.Property property = XQueryProperties.this.get(name);
                return WebService2GenericService.convert(property);
            }

            @Override
            public boolean isReadOnly() {
                return XQueryProperties.this.readOnly;
            }

        };
    }

}

