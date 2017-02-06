/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.operation.exception;

import com.emc.documentum.xml.dds.operation.exception.OperationException;

public class BeginFailedException
extends OperationException {
    private static final long serialVersionUID = 1;

    public BeginFailedException() {
    }

    public BeginFailedException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public BeginFailedException(String message) {
        super(message);
    }

    public BeginFailedException(Throwable throwable) {
        super(throwable);
    }
}

