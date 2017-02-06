/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  com.emc.documentum.fs.datamodel.core.properties.Property
 */
package com.emc.documentum.xml.dds.internal.webservice.xquery;

import java.util.Iterator;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.emc.documentum.fs.datamodel.core.properties.Property;
import com.emc.documentum.xml.dds.fs.xquery.XQueryVariable;
import com.emc.documentum.xml.dds.fs.xquery.XQueryVariables;
import com.emc.documentum.xml.dds.internal.webservice.PropertySet;

@XmlType(name="XQuery", namespace="http://xquery.datamodel.dds.xml.documentum.emc.com/")
@XmlAccessorType(value=XmlAccessType.FIELD)
public class XQuery {
    @XmlElement(name="XQueryString", namespace="http://xquery.datamodel.dds.xml.documentum.emc.com/")
    private String queryString;
    @XmlElement(name="XQueryVariables", namespace="http://xquery.datamodel.dds.xml.documentum.emc.com/")
    private PropertySet xqueryVariables;

    public XQuery(String xqueryString) {
        this.queryString = xqueryString;
    }

    public XQuery() {
    }

    public com.emc.documentum.xml.dds.fs.xquery.XQuery getXQuery() {
        return new com.emc.documentum.xml.dds.fs.xquery.XQuery(){

            @Override
            public String getXQueryString() {
                return XQuery.this.queryString;
            }

            @Override
            public XQueryVariables getXQueryVariables() {
                return new XQueryVariables(){

                    @Override
                    public Iterator<XQueryVariable> iterator() {
                        return new Iterator<XQueryVariable>(){
                            private Iterator<Property> iterator;

                            @Override
                            public boolean hasNext() {
                                return this.iterator.hasNext();
                            }

                            @Override
                            public XQueryVariable next() {
                                return new XQueryVariable(){
                                    private Property property;

                                    @Override
                                    public String getName() {
                                        return this.property.getName();
                                    }

                                    @Override
                                    public String getValue() {
                                        return this.property.getValueAsString();
                                    }
                                };
                            }

                            @Override
                            public void remove() {
                            }

                        };
                    }

                    @Override
                    public XQueryVariable get(String name) {
                        final Property property = XQuery.this.xqueryVariables.get(name);
                        if (property == null) {
                            return null;
                        }
                        return new XQueryVariable(){

                            @Override
                            public String getName() {
                                return property.getName();
                            }

                            @Override
                            public String getValue() {
                                return property.getValueAsString();
                            }
                        };
                    }

                };
            }

        };
    }

    public void setXQueryString(String string) {
        this.queryString = string;
    }

    public String getXQueryString() {
        return this.queryString;
    }

    public void setXQueryVariables(PropertySet variables) {
        this.xqueryVariables = variables;
    }

    public PropertySet getVariables() {
        return this.xqueryVariables;
    }

}

