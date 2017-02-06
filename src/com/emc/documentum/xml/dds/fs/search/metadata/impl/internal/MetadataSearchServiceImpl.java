/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.fs.search.metadata.impl.internal;

import com.emc.documentum.xml.dds.application.Application;
import com.emc.documentum.xml.dds.exception.DDSException;
import com.emc.documentum.xml.dds.fs.ServiceContext;
import com.emc.documentum.xml.dds.fs.impl.internal.ServiceImpl;
import com.emc.documentum.xml.dds.fs.search.metadata.MetadataSearchContext;
import com.emc.documentum.xml.dds.fs.search.metadata.MetadataSearchProperties;
import com.emc.documentum.xml.dds.fs.search.metadata.MetadataSearchQuery;
import com.emc.documentum.xml.dds.fs.search.metadata.MetadataSearchResultHandler;
import com.emc.documentum.xml.dds.fs.search.metadata.MetadataSearchService;
import com.emc.documentum.xml.dds.fs.search.metadata.MetadataSearchServiceException;
import com.emc.documentum.xml.dds.fs.search.metadata.QueryStringBuilder;
import com.emc.documentum.xml.dds.fs.search.metadata.impl.DefaultQueryStringBuilder;
import com.emc.documentum.xml.dds.operation.framework.Operation;
import com.emc.documentum.xml.dds.operation.library.xquery.ExecuteXQueryOperation;
import com.emc.documentum.xml.dds.persistence.Location;
import com.emc.documentum.xml.dds.persistence.XMLNode;
import com.emc.documentum.xml.dds.structure.DDSDataSet;
import com.emc.documentum.xml.dds.uri.URITarget;
import com.emc.documentum.xml.dds.user.User;
import com.emc.documentum.xml.dds.xquery.XQueryResultHandler;
import java.util.HashMap;
import java.util.Map;

public class MetadataSearchServiceImpl
extends ServiceImpl
implements MetadataSearchService {
    public MetadataSearchServiceImpl(ServiceContext serviceContext) {
        super(serviceContext);
    }

    @Override
    public <H, R> R search(MetadataSearchContext context, MetadataSearchQuery query, QueryStringBuilder queryStringBuilder, MetadataSearchProperties properties, final MetadataSearchResultHandler<H, R> resultHandler) throws DDSException {
        if (context == null) {
            throw new MetadataSearchServiceException("search invoked with null MetadataSearchContext");
        }
        if (query == null) {
            throw new MetadataSearchServiceException("search invoked with null MetadataSearchQuery");
        }
        if (resultHandler == null) {
            throw new MetadataSearchServiceException("search invoked with null MetadataSearchResultHandler");
        }
        try {
            Application application = MetadataSearchServiceImpl.getApplication();
            User user = this.getServiceContext().getUser();
            QueryStringBuilder nonNullQueryStringBuilder = queryStringBuilder;
            if (queryStringBuilder == null) {
                nonNullQueryStringBuilder = new DefaultQueryStringBuilder(context, query, user);
            }
            URITarget target = MetadataSearchServiceImpl.getURIResolver(application).resolveURI(context.getURI(), user);
            DDSDataSet contextDataSet = target.getDataSet();
            XQueryResultHandler wrappingHandler = new XQueryResultHandler(){

                @Override
                public Object transformXQueryResult(Object input) throws DDSException {
                    return resultHandler.transformSearchResult((H) input);
                }
            };
            return (R)application.execute(user, new ExecuteXQueryOperation(nonNullQueryStringBuilder.getQueryString(), contextDataSet.getRootLocation().asXMLNode(), new HashMap<String, String>(), wrappingHandler, true));
        }
        catch (Exception e) {
            if (e instanceof DDSException) {
                throw (DDSException)e;
            }
            throw new MetadataSearchServiceException(e);
        }
    }

}

