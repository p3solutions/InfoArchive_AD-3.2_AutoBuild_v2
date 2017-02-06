/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  com.emc.documentum.fs.rt.ServiceException
 */
package com.emc.documentum.xml.dds.internal.webservice.xquery;

import com.emc.documentum.fs.rt.ServiceException;
import com.emc.documentum.xml.dds.exception.DDSException;
import javax.xml.ws.WebFault;

@WebFault(name="XQueryServiceException", targetNamespace="http://xquery.services.dds.xml.documentum.emc.com/")
public class XQueryServiceException
extends ServiceException {
    public XQueryServiceException(String message) {
        super(message);
    }

    public XQueryServiceException(DDSException e) {
        super((Throwable)e);
    }
}

