/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.operation.library.basic;

import com.emc.documentum.xml.dds.operation.framework.Operation;
import com.emc.documentum.xml.dds.operation.library.basic.OperationSequenceExecutable;
import com.emc.documentum.xml.dds.operation.library.result.BlackBoardResult;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class OperationSequence
implements Operation<BlackBoardResult>,
Iterable<Operation<?>> {
    private String id;
    private final List<Operation<?>> operations = new ArrayList();

    public void addOperation(Operation<?> operation) {
        this.operations.add(operation);
    }

    public void addOperation(String operationId, Operation<?> operation) {
        operation.setId(operationId);
        this.operations.add(operation);
    }

    public List<Operation<?>> getOperations() {
        return this.operations;
    }

    @Override
    public String getExecutableClassName() {
        return OperationSequenceExecutable.class.getName();
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public boolean isReadOnly(String storeAlias) {
        boolean result = true;
        for (Operation operation : this.operations) {
            result &= operation.isReadOnly(storeAlias);
        }
        return result;
    }

    @Override
    public List<String> getStoreAliases() {
        ArrayList<String> result = new ArrayList<String>();
        for (Operation<?> operation : this.operations) {
            for (String storeAlias : operation.getStoreAliases()) {
                if (result.contains(storeAlias)) continue;
                result.add(storeAlias);
            }
        }
        return result;
    }

    @Override
    public Iterator<Operation<?>> iterator() {
        return this.operations.iterator();
    }
}

