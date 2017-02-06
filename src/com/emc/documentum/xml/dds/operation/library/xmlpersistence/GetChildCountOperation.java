/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.operation.library.xmlpersistence;

import com.emc.documentum.xml.dds.operation.exception.OperationException;
import com.emc.documentum.xml.dds.operation.library.xmlpersistence.GetChildCountOperationExecutable;
import com.emc.documentum.xml.dds.operation.library.xmlpersistence.SingleNodeOperation;
import com.emc.documentum.xml.dds.persistence.XMLNode;

public class GetChildCountOperation
extends SingleNodeOperation<Integer> {
    public GetChildCountOperation(XMLNode xmlNode) throws OperationException {
        super(xmlNode);
    }

    @Override
    public String getExecutableClassName() {
        return GetChildCountOperationExecutable.class.getName();
    }

    @Override
    public boolean isReadOnly(String storeAlias) {
        return true;
    }
}

