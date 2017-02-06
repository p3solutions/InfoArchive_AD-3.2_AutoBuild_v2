/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.internal.webservice.xproc;

import com.emc.documentum.xml.dds.internal.webservice.xproc.Parameter;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(name="ParameterMapping", namespace="http://xproc.datamodel.dds.xml.documentum.emc.com/")
@XmlAccessorType(value=XmlAccessType.FIELD)
public class ParameterMapping {
    @XmlElement(name="Port", namespace="http://xproc.datamodel.dds.xml.documentum.emc.com/")
    private String port;
    @XmlElement(name="Parameter", namespace="http://xproc.datamodel.dds.xml.documentum.emc.com/")
    private List<Parameter> parameters;

    public ParameterMapping(String port, List<Parameter> parameters) {
        this.port = port;
        this.parameters = parameters;
    }

    public ParameterMapping() {
    }

    public String getPort() {
        return this.port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public List<Parameter> getParameters() {
        return this.parameters;
    }

    public void setParameters(List<Parameter> parameters) {
        this.parameters = parameters;
    }
}

