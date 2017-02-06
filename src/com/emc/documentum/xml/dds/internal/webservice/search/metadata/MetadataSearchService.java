/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.internal.webservice.search.metadata;

import com.emc.documentum.xml.dds.internal.webservice.search.metadata.MetadataSearchContext;
import com.emc.documentum.xml.dds.internal.webservice.search.metadata.MetadataSearchProperties;
import com.emc.documentum.xml.dds.internal.webservice.search.metadata.MetadataSearchQuery;
import com.emc.documentum.xml.dds.internal.webservice.search.metadata.MetadataSearchServiceException;
import com.emc.documentum.xml.dds.internal.webservice.search.metadata.ResultValue;
import java.util.List;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

@WebService(name="MetadataSearchService", targetNamespace="http://metadata.search.services.dds.xml.documentum.emc.com/")
@SOAPBinding(style=SOAPBinding.Style.DOCUMENT, parameterStyle=SOAPBinding.ParameterStyle.WRAPPED)
public interface MetadataSearchService {
    @WebMethod(operationName="search", action="urn:search")
    @WebResult(name="Result", targetNamespace="http://metadata.search.services.dds.xml.documentum.emc.com/")
    public List<ResultValue> search(@WebParam(name="MetadataSearchContext", targetNamespace="http://metadata.search.services.dds.xml.documentum.emc.com/") MetadataSearchContext var1, @WebParam(name="MetadataSearchQuery", targetNamespace="http://metadata.search.services.dds.xml.documentum.emc.com/") MetadataSearchQuery var2, @WebParam(name="MetadataSearchProperties", targetNamespace="http://metadata.search.services.dds.xml.documentum.emc.com/") MetadataSearchProperties var3) throws MetadataSearchServiceException;
}

