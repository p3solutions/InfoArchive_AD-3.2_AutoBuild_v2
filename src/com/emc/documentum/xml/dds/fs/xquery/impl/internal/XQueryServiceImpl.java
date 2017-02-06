/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.fs.xquery.impl.internal;

import com.emc.documentum.xml.dds.application.Application;
import com.emc.documentum.xml.dds.exception.DDSException;
import com.emc.documentum.xml.dds.fs.ServiceContext;
import com.emc.documentum.xml.dds.fs.impl.internal.ServiceImpl;
import com.emc.documentum.xml.dds.fs.xquery.XQuery;
import com.emc.documentum.xml.dds.fs.xquery.XQueryContext;
import com.emc.documentum.xml.dds.fs.xquery.XQueryProperties;
import com.emc.documentum.xml.dds.fs.xquery.XQueryResultHandler;
import com.emc.documentum.xml.dds.fs.xquery.XQueryService;
import com.emc.documentum.xml.dds.fs.xquery.XQueryServiceException;
import com.emc.documentum.xml.dds.fs.xquery.XQueryVariable;
import com.emc.documentum.xml.dds.fs.xquery.XQueryVariables;
import com.emc.documentum.xml.dds.operation.framework.Operation;
import com.emc.documentum.xml.dds.operation.library.xquery.ExecuteXQueryOperation;
import com.emc.documentum.xml.dds.persistence.Store;
import com.emc.documentum.xml.dds.persistence.StoreChild;
import com.emc.documentum.xml.dds.persistence.XMLNode;
import com.emc.documentum.xml.dds.uri.URITarget;
import com.emc.documentum.xml.dds.user.User;
import java.util.HashMap;
import java.util.Map;

public class XQueryServiceImpl
extends ServiceImpl
implements XQueryService {
    public XQueryServiceImpl(ServiceContext serviceContext) {
        super(serviceContext);
    }

    @Override
    public <H, R> R runXQuery(XQueryContext xqueryContext, XQuery xquery, XQueryProperties properties, final XQueryResultHandler<H, R> resultHandler) throws DDSException {
        if (xqueryContext == null) {
            throw new XQueryServiceException("runXQuery invoked with null XQueryContext");
        }
        if (xquery == null) {
            throw new XQueryServiceException("runXQuery invoked with null XQuery");
        }
        if (resultHandler == null) {
            throw new XQueryServiceException("runXQuery invoked with null XQueryResultHandler");
        }
        try {
            HashMap<String, String> variableMap = new HashMap<String, String>();
            XQueryVariables xqueryVariables = xquery.getXQueryVariables();
            if (xqueryVariables != null) {
                for (XQueryVariable xqueryVariable : xqueryVariables) {
                    variableMap.put(xqueryVariable.getName(), xqueryVariable.getValue());
                }
            }
            boolean readOnly = true;
            if (properties != null) {
                readOnly = properties.isReadOnly();
            }
            Application application = ServiceImpl.getApplication();
            User user = this.getServiceContext().getUser();
            com.emc.documentum.xml.dds.xquery.XQueryResultHandler wrappingHandler = new com.emc.documentum.xml.dds.xquery.XQueryResultHandler(){

                @Override
                public Object transformXQueryResult(Object input) throws DDSException {
                    return resultHandler.transformXQueryResult((H) input);
                }
            };
            if (xqueryContext.getURI() == null || "".equals(xqueryContext.getURI())) {
                return (R)application.execute(user, new ExecuteXQueryOperation(application.getMainStore(), xquery.getXQueryString(), null, wrappingHandler, readOnly));
            }
            URITarget target = ServiceImpl.getURIResolver(application).resolveURI(xqueryContext.getURI(), user);
            StoreChild storeChild = target.getStoreChild();
            return (R)application.execute(user, new ExecuteXQueryOperation(xquery.getXQueryString(), storeChild.asXMLNode(), variableMap, wrappingHandler, readOnly));
        }
        catch (Exception e) {
            if (e instanceof DDSException) {
                throw (DDSException)e;
            }
            throw new XQueryServiceException(e);
        }
    }

}

