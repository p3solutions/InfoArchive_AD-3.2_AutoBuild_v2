/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.internal.webservice.xquery;

import com.emc.documentum.xml.dds.internal.webservice.xquery.ResultValue;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(name="ProcessingInstructionValue", namespace="http://xquery.datamodel.dds.xml.documentum.emc.com/")
@XmlAccessorType(value=XmlAccessType.FIELD)
public class ProcessingInstructionValue
extends ResultValue {
    @XmlElement(name="Data", namespace="http://xquery.datamodel.dds.xml.documentum.emc.com/")
    private String data;
    @XmlElement(name="Target", namespace="http://xquery.datamodel.dds.xml.documentum.emc.com/")
    private String target;

    public ProcessingInstructionValue() {
    }

    public ProcessingInstructionValue(String type, String target, String data) {
        super(type);
        this.target = target;
        this.data = data;
    }

    public String getData() {
        return this.data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getTarget() {
        return this.target;
    }

    public void setTarget(String target) {
        this.target = target;
    }
}

