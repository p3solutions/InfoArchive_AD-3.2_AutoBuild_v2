/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.internal.webservice.xproc;

import com.emc.documentum.xml.dds.internal.webservice.xproc.PipelineInput;
import com.emc.documentum.xml.dds.internal.webservice.xproc.PipelineOutput;
import com.emc.documentum.xml.dds.internal.webservice.xproc.Source;
import com.emc.documentum.xml.dds.internal.webservice.xproc.XProcProperties;
import com.emc.documentum.xml.dds.internal.webservice.xproc.XProcServiceException;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

@WebService(name="XProcService", targetNamespace="http://xproc.services.dds.xml.documentum.emc.com/")
@SOAPBinding(style=SOAPBinding.Style.DOCUMENT, parameterStyle=SOAPBinding.ParameterStyle.WRAPPED)
public interface XProcService {
    @WebMethod(operationName="runPipeline", action="urn:runPipeline")
    @WebResult(name="PipelineOutput", targetNamespace="http://xproc.services.dds.xml.documentum.emc.com/")
    public PipelineOutput runPipeline(@WebParam(name="Pipeline", targetNamespace="http://xproc.services.dds.xml.documentum.emc.com/") Source var1, @WebParam(name="PipelineInput", targetNamespace="http://xproc.services.dds.xml.documentum.emc.com/") PipelineInput var2, @WebParam(name="XProcProperties", targetNamespace="http://xproc.services.dds.xml.documentum.emc.com/") XProcProperties var3) throws XProcServiceException;
}

