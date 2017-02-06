/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.operation.library.basic;

import com.emc.documentum.xml.dds.application.Application;
import com.emc.documentum.xml.dds.exception.DDSException;
import com.emc.documentum.xml.dds.operation.exception.RollbackFailedException;
import com.emc.documentum.xml.dds.operation.exception.RollbackNotAvailableException;
import com.emc.documentum.xml.dds.operation.framework.Executor;
import com.emc.documentum.xml.dds.operation.framework.Operation;
import com.emc.documentum.xml.dds.operation.framework.OperationExecutable;
import com.emc.documentum.xml.dds.operation.library.basic.AbstractOperationExecutable;
import com.emc.documentum.xml.dds.operation.library.basic.OperationSequence;
import com.emc.documentum.xml.dds.operation.library.result.BlackBoardResult;
import com.emc.documentum.xml.dds.persistence.Session;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class OperationSequenceExecutable
extends AbstractOperationExecutable<OperationSequence, BlackBoardResult> {
    private final List<OperationExecutable<? extends Operation<?>, ?>> executableList = new ArrayList();

    @Override
    public void beforeRun() {
        for (Operation operation : ((OperationSequence)this.getOperation()).getOperations()) {
            this.executableList.add(Executor.createExecutable(this.getApplication(), operation));
        }
    }

    @Override
    public BlackBoardResult run(Map<String, Session> sessionMap) throws DDSException {
        BlackBoardResult result = new BlackBoardResult();
        for (OperationExecutable executable : this.executableList) {
            executable.beforeRun();
            Object operationResult = executable.run(sessionMap);
            if (executable.getOperation().getId() == null) continue;
            result.addResult(executable.getOperation().getId(), operationResult);
        }
        return result;
    }

    @Override
    public void afterRun() {
    }

    @Override
    public boolean canRollback() {
        boolean result = true;
        for (OperationExecutable executable : this.executableList) {
            result &= executable.canRollback();
        }
        return result;
    }

    @Override
    public void rollback() throws RollbackNotAvailableException, RollbackFailedException {
        ArrayList<OperationExecutable> reversed = new ArrayList(this.executableList);
        Collections.reverse(reversed);
        for (OperationExecutable executable : reversed) {
            executable.rollback();
        }
    }
}

