/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.internal.webservice.xproc;

import com.emc.documentum.xml.dds.internal.webservice.xproc.Source;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(name="SourceMapping", namespace="http://xproc.datamodel.dds.xml.documentum.emc.com/")
@XmlAccessorType(value=XmlAccessType.FIELD)
public class SourceMapping {
    @XmlElement(name="Port", namespace="http://xproc.datamodel.dds.xml.documentum.emc.com/")
    private String port;
    @XmlElement(name="Source", namespace="http://xproc.datamodel.dds.xml.documentum.emc.com/")
    private List<Source> sources;

    public SourceMapping(String port, List<Source> sources) {
        this.port = port;
        this.sources = sources;
    }

    public SourceMapping() {
    }

    public String getPort() {
        return this.port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public List<Source> getSources() {
        return this.sources;
    }

    public void setSources(List<Source> sources) {
        this.sources = sources;
    }
}

