/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.operation.library.xmlpersistence;

import com.emc.documentum.xml.dds.exception.DDSException;
import com.emc.documentum.xml.dds.operation.library.xmlpersistence.GetNodeOperation;
import com.emc.documentum.xml.dds.operation.library.xmlpersistence.SingleNodeOperationExecutable;
import com.emc.documentum.xml.dds.persistence.Location;
import com.emc.documentum.xml.dds.persistence.Session;
import com.emc.documentum.xml.dds.persistence.XMLNode;
import java.util.Map;

public class GetNodeOperationExecutable
extends SingleNodeOperationExecutable<GetNodeOperation, XMLNode> {
    @Override
    public XMLNode run(Map<String, Session> sessionMap) throws DDSException {
        if (((GetNodeOperation)this.getOperation()).getLocation() == null) {
            return ((GetNodeOperation)this.getOperation()).getXMLNode().getNode(sessionMap.get(((GetNodeOperation)this.getOperation()).getStoreAlias()));
        }
        XMLNode node = ((GetNodeOperation)this.getOperation()).getLocation().asXMLNode();
        return node.getNode(sessionMap.get(((GetNodeOperation)this.getOperation()).getStoreAlias()));
    }
}

