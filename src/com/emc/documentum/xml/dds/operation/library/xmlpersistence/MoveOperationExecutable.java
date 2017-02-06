/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.operation.library.xmlpersistence;

import com.emc.documentum.xml.dds.exception.DDSException;
import com.emc.documentum.xml.dds.operation.exception.OperationException;
import com.emc.documentum.xml.dds.operation.library.xmlpersistence.MoveOperation;
import com.emc.documentum.xml.dds.operation.library.xmlpersistence.TwoNodeOperationExecutable;
import com.emc.documentum.xml.dds.persistence.Session;
import com.emc.documentum.xml.dds.persistence.XMLNode;
import java.util.List;
import java.util.Map;

public class MoveOperationExecutable
extends TwoNodeOperationExecutable<MoveOperation, XMLNode> {
    @Override
    public XMLNode run(Map<String, Session> sessionMap) throws DDSException {
        if (((MoveOperation)this.getOperation()).getStoreAliases().size() == 1) {
            Session session = sessionMap.get(((MoveOperation)this.getOperation()).getSourceStoreAlias());
            return ((MoveOperation)this.getOperation()).getSourceXMLNode().move(session, ((MoveOperation)this.getOperation()).getTargetXMLNode(), ((MoveOperation)this.getOperation()).getBefore());
        }
        throw new OperationException("Copying between different Stores not yet supported.");
    }
}

