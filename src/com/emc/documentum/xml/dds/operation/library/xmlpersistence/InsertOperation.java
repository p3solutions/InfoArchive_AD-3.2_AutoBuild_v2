/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.operation.library.xmlpersistence;

import com.emc.documentum.xml.dds.operation.exception.OperationException;
import com.emc.documentum.xml.dds.operation.library.xmlpersistence.InsertOperationExecutable;
import com.emc.documentum.xml.dds.operation.library.xmlpersistence.SingleNodeOperation;
import com.emc.documentum.xml.dds.persistence.XMLNode;

public class InsertOperation
extends SingleNodeOperation<XMLNode> {
    private final String xml;
    private final boolean before;

    public InsertOperation(XMLNode xmlNode, String xml, boolean before) throws OperationException {
        super(xmlNode);
        this.xml = xml;
        this.before = before;
    }

    public String getXML() {
        return this.xml;
    }

    public boolean getBefore() {
        return this.before;
    }

    @Override
    public String getExecutableClassName() {
        return InsertOperationExecutable.class.getName();
    }

    @Override
    public boolean isReadOnly(String storeAlias) {
        return false;
    }
}

