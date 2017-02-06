/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.persistence.exception;

import com.emc.documentum.xml.dds.persistence.exception.PersistenceException;

public class DeadlockException
extends PersistenceException {
    private static final long serialVersionUID = 1;

    public DeadlockException() {
    }

    public DeadlockException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public DeadlockException(String message) {
        super(message);
    }

    public DeadlockException(Throwable throwable) {
        super(throwable);
    }
}

