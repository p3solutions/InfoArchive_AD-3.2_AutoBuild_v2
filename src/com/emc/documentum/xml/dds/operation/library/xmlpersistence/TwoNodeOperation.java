/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.operation.library.xmlpersistence;

import com.emc.documentum.xml.dds.operation.exception.OperationException;
import com.emc.documentum.xml.dds.operation.library.basic.AbstractTwoStoreOperation;
import com.emc.documentum.xml.dds.persistence.Store;
import com.emc.documentum.xml.dds.persistence.XMLNode;

public abstract class TwoNodeOperation<T>
extends AbstractTwoStoreOperation<T> {
    private final XMLNode sourceXMLNode;
    private final XMLNode targetXMLNode;

    public TwoNodeOperation(XMLNode sourceXMLNode, XMLNode targetXMLNode) throws OperationException {
        this.declareSourceStore(sourceXMLNode.getStore());
        this.declareTargetStore(sourceXMLNode.getStore());
        this.sourceXMLNode = sourceXMLNode;
        this.targetXMLNode = targetXMLNode;
    }

    public XMLNode getSourceXMLNode() {
        return this.sourceXMLNode;
    }

    public XMLNode getTargetXMLNode() {
        return this.targetXMLNode;
    }
}

