/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.internal.webservice.xquery;

import com.emc.documentum.xml.dds.internal.webservice.xquery.ResultValue;
import com.emc.documentum.xml.dds.internal.webservice.xquery.XQuery;
import com.emc.documentum.xml.dds.internal.webservice.xquery.XQueryContext;
import com.emc.documentum.xml.dds.internal.webservice.xquery.XQueryProperties;
import com.emc.documentum.xml.dds.internal.webservice.xquery.XQueryServiceException;
import java.util.List;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

@WebService(name="XQueryService", targetNamespace="http://xquery.services.dds.xml.documentum.emc.com/")
@SOAPBinding(style=SOAPBinding.Style.DOCUMENT, parameterStyle=SOAPBinding.ParameterStyle.WRAPPED)
public interface XQueryService {
    @WebMethod(operationName="runXQuery", action="urn:runXQuery")
    @WebResult(name="Result", targetNamespace="http://xquery.services.dds.xml.documentum.emc.com/")
    public List<ResultValue> runXQuery(@WebParam(name="XQueryContext", targetNamespace="http://xquery.services.dds.xml.documentum.emc.com/") XQueryContext var1, @WebParam(name="XQuery", targetNamespace="http://xquery.services.dds.xml.documentum.emc.com/") XQuery var2, @WebParam(name="XQueryProperties", targetNamespace="http://xquery.services.dds.xml.documentum.emc.com/") XQueryProperties var3) throws XQueryServiceException;
}

