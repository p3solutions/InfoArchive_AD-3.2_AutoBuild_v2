/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  com.emc.documentum.xml.xproc.pipeline.model.PipelineInput
 */
package com.emc.documentum.xml.dds.servlet.xproc;

import com.emc.documentum.xml.xproc.pipeline.model.PipelineInput;
import java.util.Map;

public interface XProcParameterProvider {
    public String getPipelineURI() throws Exception;

    public PipelineInput getPipelineInput() throws Exception;

    public boolean isReadOnly() throws Exception;

    public String getContentType(String var1) throws Exception;

    public boolean useExternalOutput() throws Exception;

    public String getExternalContentType(String var1) throws Exception;

    public Map<String, String> getHeaders() throws Exception;
}

