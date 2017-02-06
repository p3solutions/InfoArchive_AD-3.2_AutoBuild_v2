/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.internal.webservice;

import javax.annotation.Resource;
import javax.xml.ws.WebServiceContext;

public class WebServiceImpl {
    public static final String SERVICES_NAMESPACE = "http://services.dds.xml.documentum.emc.com/";
    public static final String DATAMODEL_NAMESPACE = "http://datamodel.dds.xml.documentum.emc.com/";
    @Resource
    private WebServiceContext wsContext;

    protected WebServiceContext getWebServiceContext() {
        return this.wsContext;
    }
}

