/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.operation.library.xmlpersistence;

import com.emc.documentum.xml.dds.operation.exception.OperationException;
import com.emc.documentum.xml.dds.operation.library.basic.AbstractMultiStoreOperation;
import com.emc.documentum.xml.dds.persistence.Store;
import com.emc.documentum.xml.dds.persistence.XMLNode;
import java.util.List;

public abstract class MultiNodeOperation<T>
extends AbstractMultiStoreOperation<T> {
    private final List<XMLNode> xmlNodeList;

    public MultiNodeOperation(List<XMLNode> xmlNodeList) throws OperationException {
        for (XMLNode node : xmlNodeList) {
            this.declareStore(node.getStore());
        }
        this.xmlNodeList = xmlNodeList;
    }

    public List<XMLNode> getXMLNodeList() {
        return this.xmlNodeList;
    }
}

