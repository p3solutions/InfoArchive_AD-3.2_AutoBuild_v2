/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.ws.xquery.impl;

import com.emc.documentum.xml.dds.exception.DDSException;
import com.emc.documentum.xml.dds.fs.ServiceContext;
import com.emc.documentum.xml.dds.fs.ServiceFactory;
import com.emc.documentum.xml.dds.fs.xquery.XQuery;
import com.emc.documentum.xml.dds.internal.webservice.WebServiceContext;
import com.emc.documentum.xml.dds.internal.webservice.WebServiceImpl;
import com.emc.documentum.xml.dds.internal.webservice.xquery.ResultValue;
import com.emc.documentum.xml.dds.internal.webservice.xquery.XQueryContext;
import com.emc.documentum.xml.dds.internal.webservice.xquery.XQueryProperties;
import com.emc.documentum.xml.dds.internal.webservice.xquery.XQueryResultHandler;
import com.emc.documentum.xml.dds.internal.webservice.xquery.XQueryServiceException;
import com.emc.documentum.xml.dds.logging.LogCenter;
import java.util.List;
import javax.jws.WebService;

@WebService(endpointInterface="com.emc.documentum.xml.dds.internal.webservice.xquery.XQueryService")
public class XQueryService
extends WebServiceImpl
implements com.emc.documentum.xml.dds.internal.webservice.xquery.XQueryService {
    public static final String SERVICES_NAMESPACE = "http://xquery.services.dds.xml.documentum.emc.com/";
    public static final String DATAMODEL_NAMESPACE = "http://xquery.datamodel.dds.xml.documentum.emc.com/";

    @Override
    public List<ResultValue> runXQuery(XQueryContext xqueryContext, com.emc.documentum.xml.dds.internal.webservice.xquery.XQuery xquery, XQueryProperties properties) throws XQueryServiceException {
        if (xqueryContext == null) {
            throw new XQueryServiceException("runXQuery invoked with null XQueryContext");
        }
        if (xquery == null) {
            throw new XQueryServiceException("runXQuery invoked with null XQuery");
        }
        WebServiceContext webServiceContext = new WebServiceContext(this.getWebServiceContext());
        try {
            com.emc.documentum.xml.dds.fs.xquery.XQueryService xqueryService = ServiceFactory.getInstance().createXQueryService(webServiceContext);
            XQueryProperties props = properties == null ? new XQueryProperties() : properties;
            return (List)xqueryService.runXQuery(xqueryContext.getXQueryContext(), xquery.getXQuery(), props.getXQueryProperties(), new XQueryResultHandler());
        }
        catch (DDSException de) {
            LogCenter.exception(this, "Failed to execute query : Context = " + xqueryContext.toString() + ", XQuery = " + xquery.toString(), de);
            throw new XQueryServiceException(de);
        }
    }
}

