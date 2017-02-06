/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.operation.library.index.xdb;

import com.emc.documentum.xml.dds.operation.exception.OperationException;
import com.emc.documentum.xml.dds.operation.library.basic.AbstractSingleStoreOperation;
import com.emc.documentum.xml.dds.operation.library.index.xdb.RemoveIndexesOperationExecutable;
import com.emc.documentum.xml.dds.persistence.Store;
import com.emc.documentum.xml.dds.persistence.XMLNode;
import java.util.List;

public class RemoveIndexesOperation
extends AbstractSingleStoreOperation<Object> {
    private final XMLNode xmlNode;
    private final List<String> indexNames;

    public RemoveIndexesOperation(XMLNode xmlNode, List<String> indexNames) throws OperationException {
        this.declareStore(xmlNode.getStore());
        this.xmlNode = xmlNode;
        this.indexNames = indexNames;
    }

    public XMLNode getXMLNode() {
        return this.xmlNode;
    }

    public List<String> getIndexNames() {
        return this.indexNames;
    }

    @Override
    public String getExecutableClassName() {
        return RemoveIndexesOperationExecutable.class.getName();
    }

    @Override
    public boolean isReadOnly(String storeAlias) {
        return false;
    }
}

