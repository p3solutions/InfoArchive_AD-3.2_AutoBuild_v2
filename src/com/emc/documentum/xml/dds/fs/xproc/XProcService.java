/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  com.emc.documentum.xml.xproc.io.Source
 *  com.emc.documentum.xml.xproc.pipeline.model.PipelineInput
 *  com.emc.documentum.xml.xproc.pipeline.model.PipelineOutput
 */
package com.emc.documentum.xml.dds.fs.xproc;

import com.emc.documentum.xml.dds.exception.DDSException;
import com.emc.documentum.xml.dds.fs.xproc.XProcProperties;
import com.emc.documentum.xml.dds.fs.xproc.XProcResultHandler;
import com.emc.documentum.xml.xproc.io.Source;
import com.emc.documentum.xml.xproc.pipeline.model.PipelineInput;
import com.emc.documentum.xml.xproc.pipeline.model.PipelineOutput;

public interface XProcService {
    public PipelineOutput runPipeline(Source var1, PipelineInput var2, XProcProperties var3) throws DDSException;

    public <H> H runPipeline(Source var1, PipelineInput var2, XProcProperties var3, XProcResultHandler<H> var4) throws DDSException;
}

