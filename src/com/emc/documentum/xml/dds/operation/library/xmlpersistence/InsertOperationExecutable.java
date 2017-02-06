/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.operation.library.xmlpersistence;

import com.emc.documentum.xml.dds.exception.DDSException;
import com.emc.documentum.xml.dds.operation.library.xmlpersistence.InsertOperation;
import com.emc.documentum.xml.dds.operation.library.xmlpersistence.SingleNodeOperationExecutable;
import com.emc.documentum.xml.dds.persistence.Session;
import com.emc.documentum.xml.dds.persistence.XMLNode;
import java.util.Map;

public class InsertOperationExecutable
extends SingleNodeOperationExecutable<InsertOperation, XMLNode> {
    @Override
    public XMLNode run(Map<String, Session> sessionMap) throws DDSException {
        return ((InsertOperation)this.getOperation()).getXMLNode().insert(sessionMap.get(((InsertOperation)this.getOperation()).getStoreAlias()), ((InsertOperation)this.getOperation()).getXML(), ((InsertOperation)this.getOperation()).getBefore());
    }
}

