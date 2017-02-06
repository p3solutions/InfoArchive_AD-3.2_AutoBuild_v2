/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  com.emc.documentum.fs.rt.ServiceException
 */
package com.emc.documentum.xml.dds.internal.webservice.search.metadata;

import com.emc.documentum.fs.rt.ServiceException;
import com.emc.documentum.xml.dds.exception.DDSException;
import javax.xml.ws.WebFault;

@WebFault(name="MetadataSearchServiceException", targetNamespace="http://metadata.search.services.dds.xml.documentum.emc.com/")
public class MetadataSearchServiceException
extends ServiceException {
    public MetadataSearchServiceException(String message) {
        super(message);
    }

    public MetadataSearchServiceException(String message, Throwable t) {
        super(message, t);
    }

    public MetadataSearchServiceException(DDSException e) {
        super((Throwable)e);
    }
}

