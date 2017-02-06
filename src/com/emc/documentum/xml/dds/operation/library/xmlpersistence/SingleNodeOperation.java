/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.operation.library.xmlpersistence;

import com.emc.documentum.xml.dds.operation.exception.OperationException;
import com.emc.documentum.xml.dds.operation.library.basic.AbstractSingleStoreOperation;
import com.emc.documentum.xml.dds.persistence.Store;
import com.emc.documentum.xml.dds.persistence.XMLNode;

public abstract class SingleNodeOperation<T>
extends AbstractSingleStoreOperation<T> {
    private XMLNode xmlNode;

    public SingleNodeOperation(XMLNode xmlNode) throws OperationException {
        this.declareStore(xmlNode.getStore());
        this.xmlNode = xmlNode;
    }

    public SingleNodeOperation() {
    }

    public XMLNode getXMLNode() {
        return this.xmlNode;
    }
}

