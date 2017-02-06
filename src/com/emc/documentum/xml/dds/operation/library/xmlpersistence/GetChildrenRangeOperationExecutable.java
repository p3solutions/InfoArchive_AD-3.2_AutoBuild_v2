/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.operation.library.xmlpersistence;

import com.emc.documentum.xml.dds.exception.DDSException;
import com.emc.documentum.xml.dds.operation.library.xmlpersistence.GetChildrenRangeOperation;
import com.emc.documentum.xml.dds.operation.library.xmlpersistence.SingleNodeOperationExecutable;
import com.emc.documentum.xml.dds.persistence.Session;
import com.emc.documentum.xml.dds.persistence.XMLNode;
import java.util.List;
import java.util.Map;

public class GetChildrenRangeOperationExecutable
extends SingleNodeOperationExecutable<GetChildrenRangeOperation, List<XMLNode>> {
    @Override
    public List<XMLNode> run(Map<String, Session> sessionMap) throws DDSException {
        return ((GetChildrenRangeOperation)this.getOperation()).getXMLNode().getChildrenRange(sessionMap.get(((GetChildrenRangeOperation)this.getOperation()).getStoreAlias()), ((GetChildrenRangeOperation)this.getOperation()).getFirst(), ((GetChildrenRangeOperation)this.getOperation()).getLast());
    }
}

