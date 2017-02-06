/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.operation.exception;

import com.emc.documentum.xml.dds.operation.exception.OperationException;

public class RollbackFailedException
extends OperationException {
    private static final long serialVersionUID = 1;

    public RollbackFailedException() {
    }

    public RollbackFailedException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public RollbackFailedException(String message) {
        super(message);
    }

    public RollbackFailedException(Throwable throwable) {
        super(throwable);
    }
}

