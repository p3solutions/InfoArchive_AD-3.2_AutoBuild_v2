/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.usi;

import com.emc.documentum.xml.dds.configuration.Configuration;
import com.emc.documentum.xml.dds.usi.ResponseFilter;
import com.emc.documentum.xml.dds.usi.ResponseProcessor;

public class ProcessorAssociation
implements Configuration {
    private ResponseFilter filter;
    private ResponseProcessor processor;

    public ProcessorAssociation(ResponseFilter filter, ResponseProcessor processor) {
        this.filter = filter;
        this.processor = processor;
    }

    public ResponseFilter getFilter() {
        return this.filter;
    }

    public void setFilter(ResponseFilter filter) {
        this.filter = filter;
    }

    public ResponseProcessor getProcessor() {
        return this.processor;
    }

    public void setProcessor(ResponseProcessor processor) {
        this.processor = processor;
    }
}

