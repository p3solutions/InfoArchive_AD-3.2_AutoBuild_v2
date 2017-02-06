/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.persistence.exception;

import com.emc.documentum.xml.dds.persistence.exception.PersistenceException;

public class StoreNotFoundException
extends PersistenceException {
    private static final long serialVersionUID = 1;

    public StoreNotFoundException() {
    }

    public StoreNotFoundException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public StoreNotFoundException(String message) {
        super(message);
    }

    public StoreNotFoundException(Throwable throwable) {
        super(throwable);
    }
}

