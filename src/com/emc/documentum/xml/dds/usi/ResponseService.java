/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.usi;

import com.emc.documentum.xml.dds.operation.exception.OperationException;
import com.emc.documentum.xml.dds.service.Service;
import com.emc.documentum.xml.dds.service.exception.ServiceNotAvailableException;
import com.emc.documentum.xml.dds.usi.Response;
import com.emc.documentum.xml.dds.usi.ResponseFilter;
import com.emc.documentum.xml.dds.usi.ResponseProcessor;
import com.emc.documentum.xml.dds.xbase.XBase;
import org.w3c.dom.Node;

public interface ResponseService
extends Service {
    public void submit(Response var1) throws ServiceNotAvailableException, OperationException;

    public void submit(String var1) throws ServiceNotAvailableException, OperationException;

    public void submit(Node var1) throws ServiceNotAvailableException, OperationException;

    public int registerPreProcessor(ResponseFilter var1, ResponseProcessor var2);

    public void unregisterPreProcessor(int var1);

    public int registerPostProcessor(ResponseFilter var1, ResponseProcessor var2);

    public void unregisterPostProcessor(int var1);

    public int registerXBase(ResponseFilter var1, XBase var2);

    public void unregisterXBase(int var1);

    public ResponseProcessor getProcessor(String var1);
}

