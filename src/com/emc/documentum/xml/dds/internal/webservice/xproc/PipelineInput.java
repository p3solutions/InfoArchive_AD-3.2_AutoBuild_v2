/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.internal.webservice.xproc;

import com.emc.documentum.xml.dds.internal.webservice.xproc.Option;
import com.emc.documentum.xml.dds.internal.webservice.xproc.ParameterMapping;
import com.emc.documentum.xml.dds.internal.webservice.xproc.SourceMapping;
import java.io.Serializable;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(name="PipelineInput", namespace="http://xproc.datamodel.dds.xml.documentum.emc.com/")
@XmlAccessorType(value=XmlAccessType.FIELD)
public class PipelineInput
implements Serializable {
    @XmlElement(name="Input", namespace="http://xproc.datamodel.dds.xml.documentum.emc.com/")
    private List<SourceMapping> inputs;
    @XmlElement(name="ParameterMapping", namespace="http://xproc.datamodel.dds.xml.documentum.emc.com/")
    private List<ParameterMapping> parameterMappings;
    @XmlElement(name="Option", namespace="http://xproc.datamodel.dds.xml.documentum.emc.com/")
    private List<Option> options;

    public PipelineInput(List<SourceMapping> inputs, List<Option> options, List<ParameterMapping> parameterMappings) {
        this.inputs = inputs;
        this.options = options;
        this.parameterMappings = parameterMappings;
    }

    public PipelineInput() {
    }

    public List<SourceMapping> getInputs() {
        return this.inputs;
    }

    public void setInputs(List<SourceMapping> inputs) {
        this.inputs = inputs;
    }

    public List<ParameterMapping> getParameterMappings() {
        return this.parameterMappings;
    }

    public void setParameterMappings(List<ParameterMapping> parameterMappings) {
        this.parameterMappings = parameterMappings;
    }

    public List<Option> getOptions() {
        return this.options;
    }

    public void setOptions(List<Option> options) {
        this.options = options;
    }
}

