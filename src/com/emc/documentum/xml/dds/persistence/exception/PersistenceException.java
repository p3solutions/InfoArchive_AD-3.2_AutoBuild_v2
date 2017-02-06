/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.persistence.exception;

import com.emc.documentum.xml.dds.exception.DDSException;

public class PersistenceException
extends DDSException {
    private static final long serialVersionUID = 1;

    public PersistenceException() {
    }

    public PersistenceException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public PersistenceException(String message) {
        super(message);
    }

    public PersistenceException(Throwable throwable) {
        super(throwable);
    }
}

