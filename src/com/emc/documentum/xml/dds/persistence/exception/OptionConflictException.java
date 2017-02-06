/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.persistence.exception;

import com.emc.documentum.xml.dds.persistence.exception.PersistenceException;

public class OptionConflictException
extends PersistenceException {
    private static final long serialVersionUID = 1;

    public OptionConflictException() {
    }

    public OptionConflictException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public OptionConflictException(String message) {
        super(message);
    }

    public OptionConflictException(Throwable throwable) {
        super(throwable);
    }
}

