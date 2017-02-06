/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.operation.exception;

import com.emc.documentum.xml.dds.operation.exception.OperationException;

public class OperationFailedException
extends OperationException {
    private static final long serialVersionUID = 1;

    public OperationFailedException() {
    }

    public OperationFailedException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public OperationFailedException(String message) {
        super(message);
    }

    public OperationFailedException(Throwable throwable) {
        super(throwable);
    }
}

