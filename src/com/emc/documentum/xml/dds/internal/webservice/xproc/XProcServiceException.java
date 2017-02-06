/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  com.emc.documentum.fs.rt.ServiceException
 */
package com.emc.documentum.xml.dds.internal.webservice.xproc;

import com.emc.documentum.fs.rt.ServiceException;
import com.emc.documentum.xml.dds.exception.DDSException;
import javax.xml.ws.WebFault;

@WebFault(name="XProcServiceException", targetNamespace="http://xproc.services.dds.xml.documentum.emc.com/")
public class XProcServiceException
extends ServiceException {
    public XProcServiceException(String message) {
        super(message);
    }

    public XProcServiceException(DDSException e) {
        super((Throwable)e);
    }
}

