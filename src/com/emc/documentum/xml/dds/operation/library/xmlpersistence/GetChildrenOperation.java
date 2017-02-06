/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.operation.library.xmlpersistence;

import com.emc.documentum.xml.dds.operation.exception.OperationException;
import com.emc.documentum.xml.dds.operation.library.xmlpersistence.GetChildrenOperationExecutable;
import com.emc.documentum.xml.dds.operation.library.xmlpersistence.SingleNodeOperation;
import com.emc.documentum.xml.dds.persistence.XMLNode;
import java.util.List;

public class GetChildrenOperation
extends SingleNodeOperation<List<XMLNode>> {
    private short nodeType = -1;

    public GetChildrenOperation(XMLNode xmlNode) throws OperationException {
        super(xmlNode);
    }

    public GetChildrenOperation(XMLNode xmlNode, short nodeType) throws OperationException {
        super(xmlNode);
        this.nodeType = nodeType;
    }

    public short getNodeType() {
        return this.nodeType;
    }

    @Override
    public String getExecutableClassName() {
        return GetChildrenOperationExecutable.class.getName();
    }

    @Override
    public boolean isReadOnly(String storeAlias) {
        return true;
    }
}

