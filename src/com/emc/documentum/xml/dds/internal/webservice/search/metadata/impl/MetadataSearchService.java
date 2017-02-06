/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  com.emc.documentum.fs.datamodel.core.properties.Property
 *  com.emc.documentum.fs.datamodel.core.properties.StringProperty
 */
package com.emc.documentum.xml.dds.internal.webservice.search.metadata.impl;

import com.emc.documentum.fs.datamodel.core.properties.Property;
import com.emc.documentum.fs.datamodel.core.properties.StringProperty;
import com.emc.documentum.xml.dds.exception.DDSException;
import com.emc.documentum.xml.dds.fs.ServiceContext;
import com.emc.documentum.xml.dds.fs.ServiceFactory;
import com.emc.documentum.xml.dds.fs.search.metadata.QueryStringBuilder;
import com.emc.documentum.xml.dds.fs.search.metadata.impl.DefaultQueryStringBuilder;
import com.emc.documentum.xml.dds.internal.webservice.WebServiceContext;
import com.emc.documentum.xml.dds.internal.webservice.WebServiceImpl;
import com.emc.documentum.xml.dds.internal.webservice.search.metadata.MetadataSearchContext;
import com.emc.documentum.xml.dds.internal.webservice.search.metadata.MetadataSearchProperties;
import com.emc.documentum.xml.dds.internal.webservice.search.metadata.MetadataSearchQuery;
import com.emc.documentum.xml.dds.internal.webservice.search.metadata.MetadataSearchResultHandler;
import com.emc.documentum.xml.dds.internal.webservice.search.metadata.MetadataSearchServiceException;
import com.emc.documentum.xml.dds.internal.webservice.search.metadata.ResultValue;
import com.emc.documentum.xml.dds.user.User;
import java.util.List;
import javax.jws.WebService;

@WebService(endpointInterface="com.emc.documentum.xml.dds.internal.webservice.search.metadata.MetadataSearchService")
public class MetadataSearchService
extends WebServiceImpl
implements com.emc.documentum.xml.dds.internal.webservice.search.metadata.MetadataSearchService {
    public static final String SERVICES_NAMESPACE = "http://metadata.search.services.dds.xml.documentum.emc.com/";
    public static final String DATAMODEL_NAMESPACE = "http://metadata.search.datamodel.dds.xml.documentum.emc.com/";

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    public List<ResultValue> search(MetadataSearchContext context, MetadataSearchQuery query, MetadataSearchProperties properties) throws MetadataSearchServiceException {
        if (context == null) {
            throw new MetadataSearchServiceException("search invoked with null MetadataSearchContext");
        }
        if (query == null) {
            throw new MetadataSearchServiceException("search invoked with null MetadataSearchQuery");
        }
        WebServiceContext webServiceContext = new WebServiceContext(this.getWebServiceContext());
        try {
        	MetadataSearchResultHandler metadataSearchResultHandler = new MetadataSearchResultHandler();
            com.emc.documentum.xml.dds.fs.search.metadata.MetadataSearchService metadataSearchService = ServiceFactory.getInstance().createMetadataSearchService(webServiceContext);
            Object queryStringBuilder22 = null;
            Property property = properties.get("query.string.builder.class");
            if (property != null) {
                if (!(property instanceof StringProperty)) throw new MetadataSearchServiceException("Property query.string.builder.class should be a String Property");
                String className = ((StringProperty)property).getValue();
                try {
                    Class clazz = Class.forName(className);
                    Object object = clazz.newInstance();
                    if (!(object instanceof QueryStringBuilder)) {
                        throw new MetadataSearchServiceException("Provided  value for property query.string.builder.class is not an instance of " + QueryStringBuilder.class.getName() + ": " + className);
                    }
                    queryStringBuilder22 = (QueryStringBuilder)object;
                    return (List)metadataSearchService.search(context.getMetadataSearchContext(), query.getMetadataSearchQuery(), (QueryStringBuilder)queryStringBuilder22, properties.getMetadataSearchProperties(), metadataSearchResultHandler);
                }
                catch (ClassNotFoundException cnfe) {
                    throw new MetadataSearchServiceException("Provided  value for property query.string.builder.class not found : " + className, cnfe);
                }
                catch (IllegalAccessException iae) {
                    throw new MetadataSearchServiceException("Provided  value for property query.string.builder.class not instantiated : " + className, iae);
                }
                catch (InstantiationException ie) {
                    throw new MetadataSearchServiceException("Provided  value for property query.string.builder.class not instantiated : " + className, ie);
                }
            } else {
                queryStringBuilder22 = new DefaultQueryStringBuilder(context.getMetadataSearchContext(), query.getMetadataSearchQuery(), webServiceContext.getUser());
            }
            return (List)metadataSearchService.search(context.getMetadataSearchContext(), query.getMetadataSearchQuery(), (QueryStringBuilder)queryStringBuilder22, properties.getMetadataSearchProperties(), metadataSearchResultHandler);
        }
        catch (DDSException de) {
            throw new MetadataSearchServiceException(de);
        }
        catch (Exception e) {
            throw new MetadataSearchServiceException(new DDSException(e));
        }
    }
}

