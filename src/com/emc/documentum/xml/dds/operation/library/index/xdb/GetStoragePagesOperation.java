/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.operation.library.index.xdb;

import com.emc.documentum.xml.dds.operation.exception.OperationException;
import com.emc.documentum.xml.dds.operation.library.basic.AbstractSingleStoreOperation;
import com.emc.documentum.xml.dds.operation.library.index.xdb.GetStoragePagesOperationExecutable;
import com.emc.documentum.xml.dds.persistence.Store;
import com.emc.documentum.xml.dds.persistence.XMLNode;

public class GetStoragePagesOperation
extends AbstractSingleStoreOperation<Long> {
    private final XMLNode xmlNode;
    private final String indexName;

    public GetStoragePagesOperation(XMLNode xmlNode, String indexName) throws OperationException {
        this.declareStore(xmlNode.getStore());
        this.xmlNode = xmlNode;
        this.indexName = indexName;
    }

    public XMLNode getXMLNode() {
        return this.xmlNode;
    }

    public String getIndexName() {
        return this.indexName;
    }

    @Override
    public String getExecutableClassName() {
        return GetStoragePagesOperationExecutable.class.getName();
    }

    @Override
    public boolean isReadOnly(String storeAlias) {
        return true;
    }
}

