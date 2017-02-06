/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.operation.library.xmlpersistence;

import com.emc.documentum.xml.dds.operation.exception.OperationException;
import com.emc.documentum.xml.dds.operation.library.xmlpersistence.GetChildrenRangeOperationExecutable;
import com.emc.documentum.xml.dds.operation.library.xmlpersistence.SingleNodeOperation;
import com.emc.documentum.xml.dds.persistence.XMLNode;
import java.util.List;

public class GetChildrenRangeOperation
extends SingleNodeOperation<List<XMLNode>> {
    private final int first;
    private final int last;

    public GetChildrenRangeOperation(XMLNode xmlNode, int first, int last) throws OperationException {
        super(xmlNode);
        this.first = first;
        this.last = last;
    }

    public int getFirst() {
        return this.first;
    }

    public int getLast() {
        return this.last;
    }

    @Override
    public String getExecutableClassName() {
        return GetChildrenRangeOperationExecutable.class.getName();
    }

    @Override
    public boolean isReadOnly(String storeAlias) {
        return true;
    }
}

