/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.operation.exception;

import com.emc.documentum.xml.dds.operation.exception.OperationException;

public class CommitFailedException
extends OperationException {
    private static final long serialVersionUID = 1;

    public CommitFailedException() {
    }

    public CommitFailedException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public CommitFailedException(String message) {
        super(message);
    }

    public CommitFailedException(Throwable throwable) {
        super(throwable);
    }
}

