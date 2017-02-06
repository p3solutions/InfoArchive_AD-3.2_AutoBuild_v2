/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.operation.exception;

import com.emc.documentum.xml.dds.exception.DDSException;

public class OperationException
extends DDSException {
    private static final long serialVersionUID = 1;

    public OperationException() {
    }

    public OperationException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public OperationException(String message) {
        super(message);
    }

    public OperationException(Throwable throwable) {
        super(throwable);
    }
}

