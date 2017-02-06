/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.operation.library.index.xdb;

import com.emc.documentum.xml.dds.operation.exception.OperationException;
import com.emc.documentum.xml.dds.operation.library.basic.AbstractSingleStoreOperation;
import com.emc.documentum.xml.dds.operation.library.index.xdb.GetKeysOperationExecutable;
import com.emc.documentum.xml.dds.persistence.Store;
import com.emc.documentum.xml.dds.persistence.XMLNode;
import java.util.List;

public class GetKeysOperation
extends AbstractSingleStoreOperation<List<String>> {
    private final XMLNode xmlNode;
    private final String indexName;

    public GetKeysOperation(XMLNode xmlNode, String indexName) throws OperationException {
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
        return GetKeysOperationExecutable.class.getName();
    }

    @Override
    public boolean isReadOnly(String storeAlias) {
        return true;
    }
}

