/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.operation.library.xmlpersistence;

import com.emc.documentum.xml.dds.operation.exception.OperationException;
import com.emc.documentum.xml.dds.operation.library.xmlpersistence.CopyOperationExecutable;
import com.emc.documentum.xml.dds.operation.library.xmlpersistence.TwoNodeOperation;
import com.emc.documentum.xml.dds.persistence.XMLNode;

public class CopyOperation
extends TwoNodeOperation<XMLNode> {
    private final boolean before;

    public CopyOperation(XMLNode sourceXMLNode, XMLNode targetXMLNode, boolean before) throws OperationException {
        super(sourceXMLNode, targetXMLNode);
        this.before = before;
    }

    public boolean getBefore() {
        return this.before;
    }

    @Override
    public String getExecutableClassName() {
        return CopyOperationExecutable.class.getName();
    }

    @Override
    public boolean isReadOnly(String storeAlias) {
        return false;
    }
}

