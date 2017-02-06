/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.persistence.exception;

import com.emc.documentum.xml.dds.persistence.exception.PersistenceException;

public class InvalidStoreTypeException
extends PersistenceException {
    private static final long serialVersionUID = 1;

    public InvalidStoreTypeException() {
    }

    public InvalidStoreTypeException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public InvalidStoreTypeException(String message) {
        super(message);
    }

    public InvalidStoreTypeException(Throwable throwable) {
        super(throwable);
    }
}

