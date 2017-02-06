/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.operation.exception;

import com.emc.documentum.xml.dds.operation.exception.OperationException;

public class RollbackNotAvailableException
extends OperationException {
    private static final long serialVersionUID = 1;

    public RollbackNotAvailableException() {
    }

    public RollbackNotAvailableException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public RollbackNotAvailableException(String message) {
        super(message);
    }

    public RollbackNotAvailableException(Throwable throwable) {
        super(throwable);
    }
}

