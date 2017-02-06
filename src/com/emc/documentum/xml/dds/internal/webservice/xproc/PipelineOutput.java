/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  com.emc.documentum.fs.datamodel.core.ObjectId
 */
package com.emc.documentum.xml.dds.internal.webservice.xproc;

import com.emc.documentum.fs.datamodel.core.ObjectId;
import com.emc.documentum.xml.dds.internal.webservice.xproc.Source;
import com.emc.documentum.xml.dds.internal.webservice.xproc.SourceMapping;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(name="PipelineOutput", namespace="http://xproc.datamodel.dds.xml.documentum.emc.com/")
@XmlAccessorType(value=XmlAccessType.FIELD)
public class PipelineOutput
implements Serializable {
    @XmlElement(name="PrimaryOutputPort", namespace="http://xproc.datamodel.dds.xml.documentum.emc.com/")
    private String primaryOutputPort;
    @XmlElement(name="Output", namespace="http://xproc.datamodel.dds.xml.documentum.emc.com/")
    private List<SourceMapping> outputs;
    @XmlElement(name="ExternalOutput", namespace="http://xproc.datamodel.dds.xml.documentum.emc.com/")
    private List<ObjectId> externalOutputs;

    public void addOutput(String port, Source source) {
        List<Source> sources;
        if (port == null) {
            throw new IllegalArgumentException("<null> port");
        }
        if (this.outputs == null) {
            this.outputs = new ArrayList<SourceMapping>();
        }
        SourceMapping sourceMapping = null;
        for (SourceMapping mapping : this.outputs) {
            if (!port.equals(mapping.getPort())) continue;
            sourceMapping = mapping;
        }
        if (sourceMapping == null) {
            sourceMapping = new SourceMapping();
            sourceMapping.setPort(port);
            this.outputs.add(sourceMapping);
        }
        if ((sources = sourceMapping.getSources()) == null) {
            sources = new ArrayList<Source>();
            sourceMapping.setSources(sources);
        }
        sources.add(source);
    }

    public void addOutput(String port, List<Source> sources) {
        if (sources == null) {
            throw new IllegalArgumentException("<null> sources");
        }
        if (sources.isEmpty()) {
            this.addOutput(port, (Source)null);
        } else {
            for (Source serializableSource : sources) {
                this.addOutput(port, serializableSource);
            }
        }
    }

    public void addExternalOutput(String uri) {
        if (uri == null) {
            throw new IllegalArgumentException("<null> uri");
        }
        if (this.externalOutputs == null) {
            this.externalOutputs = new ArrayList<ObjectId>();
        }
        this.externalOutputs.add(new ObjectId(uri));
    }

    public String getPrimaryOutputPort() {
        return this.primaryOutputPort;
    }

    public void setPrimaryOutputPort(String primaryOutputPort) {
        this.primaryOutputPort = primaryOutputPort;
    }

    public List<SourceMapping> getOutputs() {
        return this.outputs;
    }

    public void setOutputs(List<SourceMapping> outputs) {
        this.outputs = outputs;
    }

    public List<ObjectId> getExternalOutputs() {
        return this.externalOutputs;
    }

    public void setExternalOutputs(List<ObjectId> externalOutputs) {
        this.externalOutputs = externalOutputs;
    }
}

