/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.persistence.exception;

import com.emc.documentum.xml.dds.persistence.exception.PersistenceException;

public class IllegalActionException
extends PersistenceException {
    private static final long serialVersionUID = 1;

    public IllegalActionException() {
    }

    public IllegalActionException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public IllegalActionException(String message) {
        super(message);
    }

    public IllegalActionException(Throwable throwable) {
        super(throwable);
    }
}

