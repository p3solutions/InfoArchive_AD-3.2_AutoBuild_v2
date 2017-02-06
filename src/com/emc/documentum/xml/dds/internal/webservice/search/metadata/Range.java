/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.internal.webservice.search.metadata;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(name="Range", namespace="http://metadata.search.datamodel.dds.xml.documentum.emc.com/")
@XmlAccessorType(value=XmlAccessType.FIELD)
public class Range {
    @XmlElement(name="BeginIndex", namespace="http://metadata.search.datamodel.dds.xml.documentum.emc.com/")
    private int beginIndex;
    @XmlElement(name="Length", namespace="http://metadata.search.datamodel.dds.xml.documentum.emc.com/")
    private int length;

    public Range() {
    }

    public Range(int beginIndex, int length) {
        this.beginIndex = beginIndex;
        this.length = length;
    }

    public int getBeginIndex() {
        return this.beginIndex;
    }

    public void setBeginIndex(int beginIndex) {
        this.beginIndex = beginIndex;
    }

    public int getLength() {
        return this.length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public com.emc.documentum.xml.dds.fs.search.metadata.Range getRange() {
        return new com.emc.documentum.xml.dds.fs.search.metadata.Range(){

            @Override
            public int getBeginIndex() {
                return Range.this.beginIndex;
            }

            @Override
            public int getLength() {
                return Range.this.length;
            }
        };
    }

}

