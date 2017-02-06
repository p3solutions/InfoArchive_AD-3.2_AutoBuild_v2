/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  com.emc.documentum.xml.xproc.io.Source
 *  com.emc.documentum.xml.xproc.pipeline.model.PipelineInput
 */
package com.emc.documentum.xml.dds.ws.xproc.impl;

import com.emc.documentum.xml.dds.exception.DDSException;
import com.emc.documentum.xml.dds.fs.ServiceContext;
import com.emc.documentum.xml.dds.fs.ServiceFactory;
import com.emc.documentum.xml.dds.fs.xproc.XProcProperties;
import com.emc.documentum.xml.dds.internal.webservice.WebServiceContext;
import com.emc.documentum.xml.dds.internal.webservice.WebServiceImpl;
import com.emc.documentum.xml.dds.internal.webservice.xproc.PipelineInput;
import com.emc.documentum.xml.dds.internal.webservice.xproc.PipelineOutput;
import com.emc.documentum.xml.dds.internal.webservice.xproc.XProcResultHandler;
import com.emc.documentum.xml.dds.internal.webservice.xproc.XProcServiceException;
import com.emc.documentum.xml.xproc.io.Source;
import javax.jws.WebService;

@WebService(endpointInterface="com.emc.documentum.xml.dds.internal.webservice.xproc.XProcService")
public class XProcService
extends WebServiceImpl
implements com.emc.documentum.xml.dds.internal.webservice.xproc.XProcService {
    public static final String SERVICES_NAMESPACE = "http://xproc.services.dds.xml.documentum.emc.com/";
    public static final String DATAMODEL_NAMESPACE = "http://xproc.datamodel.dds.xml.documentum.emc.com/";

    @Override
    public PipelineOutput runPipeline(com.emc.documentum.xml.dds.internal.webservice.xproc.Source pipeline, PipelineInput pipelineInput, com.emc.documentum.xml.dds.internal.webservice.xproc.XProcProperties properties) throws XProcServiceException {
        if (pipeline == null) {
            throw new XProcServiceException("runPipeline invoked with null Source pipeline");
        }
        WebServiceContext webServiceContext = new WebServiceContext(this.getWebServiceContext());
        try {
            XProcResultHandler xProcResultHandler = new XProcResultHandler();
            Source newPipeline = xProcResultHandler.deserialize(pipeline);
            com.emc.documentum.xml.xproc.pipeline.model.PipelineInput newPipelineInput = xProcResultHandler.deserialize(pipelineInput);
            com.emc.documentum.xml.dds.fs.xproc.XProcService xprocService = ServiceFactory.getInstance().createXProcService(webServiceContext);
            com.emc.documentum.xml.dds.internal.webservice.xproc.XProcProperties props = properties == null ? new com.emc.documentum.xml.dds.internal.webservice.xproc.XProcProperties() : properties;
            return (PipelineOutput)xprocService.runPipeline(newPipeline, newPipelineInput, props.getXProcProperties(), xProcResultHandler);
        }
        catch (DDSException de) {
            throw new XProcServiceException(de);
        }
        catch (Exception e) {
            throw new XProcServiceException(new DDSException(e));
        }
    }
}

