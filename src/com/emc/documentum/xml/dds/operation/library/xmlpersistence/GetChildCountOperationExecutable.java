/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.operation.library.xmlpersistence;

import com.emc.documentum.xml.dds.exception.DDSException;
import com.emc.documentum.xml.dds.operation.library.xmlpersistence.GetChildCountOperation;
import com.emc.documentum.xml.dds.operation.library.xmlpersistence.SingleNodeOperationExecutable;
import com.emc.documentum.xml.dds.persistence.Session;
import com.emc.documentum.xml.dds.persistence.XMLNode;
import java.util.Map;

public class GetChildCountOperationExecutable
extends SingleNodeOperationExecutable<GetChildCountOperation, Integer> {
    @Override
    public Integer run(Map<String, Session> sessionMap) throws DDSException {
        return ((GetChildCountOperation)this.getOperation()).getXMLNode().getChildCount(sessionMap.get(((GetChildCountOperation)this.getOperation()).getStoreAlias()));
    }
}

