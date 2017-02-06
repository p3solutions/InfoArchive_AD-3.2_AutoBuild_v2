/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.operation.library.xmlpersistence;

import com.emc.documentum.xml.dds.exception.DDSException;
import com.emc.documentum.xml.dds.operation.library.xmlpersistence.GetChildrenOperation;
import com.emc.documentum.xml.dds.operation.library.xmlpersistence.SingleNodeOperationExecutable;
import com.emc.documentum.xml.dds.persistence.Session;
import com.emc.documentum.xml.dds.persistence.XMLNode;
import java.util.List;
import java.util.Map;

public class GetChildrenOperationExecutable
extends SingleNodeOperationExecutable<GetChildrenOperation, List<XMLNode>> {
    @Override
    public List<XMLNode> run(Map<String, Session> sessionMap) throws DDSException {
        if (((GetChildrenOperation)this.getOperation()).getNodeType() < 0) {
            return ((GetChildrenOperation)this.getOperation()).getXMLNode().getChildren(sessionMap.get(((GetChildrenOperation)this.getOperation()).getStoreAlias()));
        }
        return ((GetChildrenOperation)this.getOperation()).getXMLNode().getChildren(sessionMap.get(((GetChildrenOperation)this.getOperation()).getStoreAlias()), ((GetChildrenOperation)this.getOperation()).getNodeType());
    }
}

