/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.operation.library.xmlpersistence;

import com.emc.documentum.xml.dds.exception.DDSException;
import com.emc.documentum.xml.dds.operation.exception.OperationException;
import com.emc.documentum.xml.dds.operation.library.xmlpersistence.CopyOperation;
import com.emc.documentum.xml.dds.operation.library.xmlpersistence.TwoNodeOperationExecutable;
import com.emc.documentum.xml.dds.persistence.Session;
import com.emc.documentum.xml.dds.persistence.XMLNode;
import java.util.List;
import java.util.Map;

public class CopyOperationExecutable
extends TwoNodeOperationExecutable<CopyOperation, XMLNode> {
    @Override
    public XMLNode run(Map<String, Session> sessionMap) throws DDSException {
        if (((CopyOperation)this.getOperation()).getStoreAliases().size() == 1) {
            Session session = sessionMap.get(((CopyOperation)this.getOperation()).getSourceStoreAlias());
            return ((CopyOperation)this.getOperation()).getSourceXMLNode().copy(session, ((CopyOperation)this.getOperation()).getTargetXMLNode(), ((CopyOperation)this.getOperation()).getBefore());
        }
        throw new OperationException("Copying between different Stores not yet supported.");
    }
}

