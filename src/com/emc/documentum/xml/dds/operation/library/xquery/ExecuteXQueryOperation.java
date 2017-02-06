/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.operation.library.xquery;

import com.emc.documentum.xml.dds.operation.exception.OperationException;
import com.emc.documentum.xml.dds.operation.library.basic.AbstractSingleStoreOperation;
import com.emc.documentum.xml.dds.operation.library.xquery.ExecuteXQueryOperationExecutable;
import com.emc.documentum.xml.dds.persistence.Store;
import com.emc.documentum.xml.dds.persistence.XMLNode;
import com.emc.documentum.xml.dds.xquery.XQueryResultHandler;
import java.util.Map;

public class ExecuteXQueryOperation
extends AbstractSingleStoreOperation<Object> {
    private final String xquery;
    private boolean readOnly = true;
    private XMLNode xmlNode;
    private final Map<String, String> variables;
    private final XQueryResultHandler resultHandler;

    public ExecuteXQueryOperation(Store store, String xquery, Map<String, String> variables, XQueryResultHandler resultHandler, boolean readOnly) throws OperationException {
        this.declareStore(store);
        this.xquery = xquery;
        this.variables = variables;
        this.resultHandler = resultHandler;
        this.readOnly = readOnly;
    }

    public ExecuteXQueryOperation(String xquery, XMLNode xmlNode, Map<String, String> variables, XQueryResultHandler resultHandler, boolean readOnly) throws OperationException {
        this.declareStore(xmlNode.getStore());
        this.xquery = xquery;
        this.xmlNode = xmlNode;
        this.variables = variables;
        this.resultHandler = resultHandler;
        this.readOnly = readOnly;
    }

    public String getXQuery() {
        return this.xquery;
    }

    public XMLNode getXMLNode() {
        return this.xmlNode;
    }

    public Map<String, String> getVariables() {
        return this.variables;
    }

    public XQueryResultHandler getXQueryResultHandler() {
        return this.resultHandler;
    }

    @Override
    public String getExecutableClassName() {
        return ExecuteXQueryOperationExecutable.class.getName();
    }

    @Override
    public boolean isReadOnly(String storeAlias) {
        return this.readOnly;
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }
}

