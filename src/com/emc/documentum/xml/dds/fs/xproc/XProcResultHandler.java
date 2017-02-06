/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  com.emc.documentum.xml.xproc.pipeline.model.PipelineOutput
 */
package com.emc.documentum.xml.dds.fs.xproc;

import com.emc.documentum.xml.dds.exception.DDSException;
import com.emc.documentum.xml.xproc.pipeline.model.PipelineOutput;

public interface XProcResultHandler<T> {
    public T transformXProcResult(PipelineOutput var1) throws DDSException;
}

