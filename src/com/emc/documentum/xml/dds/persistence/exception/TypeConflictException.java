/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.persistence.exception;

import com.emc.documentum.xml.dds.persistence.exception.PersistenceException;

public class TypeConflictException
extends PersistenceException {
    private static final long serialVersionUID = 1;

    public TypeConflictException() {
    }

    public TypeConflictException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public TypeConflictException(String message) {
        super(message);
    }

    public TypeConflictException(Throwable throwable) {
        super(throwable);
    }
}

