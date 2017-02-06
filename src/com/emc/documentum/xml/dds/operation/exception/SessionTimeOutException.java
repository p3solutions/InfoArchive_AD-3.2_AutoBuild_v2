/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.operation.exception;

import com.emc.documentum.xml.dds.operation.exception.OperationFailedException;

public class SessionTimeOutException
extends OperationFailedException {
    private static final long serialVersionUID = 1;

    public SessionTimeOutException() {
    }

    public SessionTimeOutException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public SessionTimeOutException(String message) {
        super(message);
    }

    public SessionTimeOutException(Throwable throwable) {
        super(throwable);
    }
}

