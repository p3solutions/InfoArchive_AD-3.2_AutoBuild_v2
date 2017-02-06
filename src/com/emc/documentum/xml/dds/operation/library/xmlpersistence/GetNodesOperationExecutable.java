/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.operation.library.xmlpersistence;

import com.emc.documentum.xml.dds.exception.DDSException;
import com.emc.documentum.xml.dds.operation.library.xmlpersistence.GetNodesOperation;
import com.emc.documentum.xml.dds.operation.library.xmlpersistence.MultiNodeOperationExecutable;
import com.emc.documentum.xml.dds.persistence.Session;
import com.emc.documentum.xml.dds.persistence.Store;
import com.emc.documentum.xml.dds.persistence.XMLNode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GetNodesOperationExecutable
extends MultiNodeOperationExecutable<GetNodesOperation, List<XMLNode>> {
    @Override
    public List<XMLNode> run(Map<String, Session> sessionMap) throws DDSException {
        ArrayList<XMLNode> result = new ArrayList<XMLNode>();
        for (XMLNode node : ((GetNodesOperation)this.getOperation()).getXMLNodeList()) {
            result.add(node.getNode(sessionMap.get(node.getStore().getAlias())));
        }
        return result;
    }
}

