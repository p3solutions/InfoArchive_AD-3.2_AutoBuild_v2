/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.usi;

import com.emc.documentum.xml.dds.configuration.baseline.ServiceConfiguration;
import com.emc.documentum.xml.dds.usi.ProcessorAssociation;
import com.emc.documentum.xml.dds.usi.ResponseFilter;
import com.emc.documentum.xml.dds.usi.ResponseProcessor;
import com.emc.documentum.xml.dds.usi.XBaseAssociation;
import com.emc.documentum.xml.dds.xbase.XBase;
import java.util.ArrayList;
import java.util.List;

public class ResponseServiceConfiguration
extends ServiceConfiguration {
    private final List<ProcessorAssociation> preProcessors = new ArrayList<ProcessorAssociation>();
    private final List<ProcessorAssociation> postProcessors = new ArrayList<ProcessorAssociation>();
    private final List<XBaseAssociation> xBases = new ArrayList<XBaseAssociation>();

    public List<ProcessorAssociation> getPreProcessors() {
        return this.preProcessors;
    }

    public List<ProcessorAssociation> getPostProcessors() {
        return this.postProcessors;
    }

    public List<XBaseAssociation> getXBases() {
        return this.xBases;
    }

    public void addPreProcessor(ResponseFilter filter, ResponseProcessor processor) {
        this.preProcessors.add(new ProcessorAssociation(filter, processor));
    }

    public void addPostProcessor(ResponseFilter filter, ResponseProcessor processor) {
        this.postProcessors.add(new ProcessorAssociation(filter, processor));
    }

    public void addXBase(ResponseFilter filter, String xBaseId) {
        this.xBases.add(new XBaseAssociation(filter, xBaseId));
    }

    public void addXBase(ResponseFilter filter, XBase xBase) {
        this.xBases.add(new XBaseAssociation(filter, xBase.getId()));
    }
}

