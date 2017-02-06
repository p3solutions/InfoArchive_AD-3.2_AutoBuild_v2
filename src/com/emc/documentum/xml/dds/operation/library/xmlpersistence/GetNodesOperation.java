/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.operation.library.xmlpersistence;

import com.emc.documentum.xml.dds.operation.exception.OperationException;
import com.emc.documentum.xml.dds.operation.library.xmlpersistence.GetNodesOperationExecutable;
import com.emc.documentum.xml.dds.operation.library.xmlpersistence.MultiNodeOperation;
import com.emc.documentum.xml.dds.persistence.XMLNode;
import java.util.List;

public class GetNodesOperation
extends MultiNodeOperation<List<XMLNode>> {
    public GetNodesOperation(List<XMLNode> xmlNodeList) throws OperationException {
        super(xmlNodeList);
    }

    @Override
    public String getExecutableClassName() {
        return GetNodesOperationExecutable.class.getName();
    }

    @Override
    public boolean isReadOnly(String storeAlias) {
        return true;
    }
}

