/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.persistence.exception;

import com.emc.documentum.xml.dds.persistence.exception.PersistenceException;

public class StoreChildNotFoundException
extends PersistenceException {
    private static final long serialVersionUID = 1;

    public StoreChildNotFoundException() {
    }

    public StoreChildNotFoundException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public StoreChildNotFoundException(String message) {
        super(message);
    }

    public StoreChildNotFoundException(Throwable throwable) {
        super(throwable);
    }
}

