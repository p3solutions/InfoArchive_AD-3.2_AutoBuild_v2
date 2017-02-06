/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.persistence.exception;

import com.emc.documentum.xml.dds.persistence.exception.PersistenceException;

public class StoreChildAlreadyExistsException
extends PersistenceException {
    private static final long serialVersionUID = 1;

    public StoreChildAlreadyExistsException() {
    }

    public StoreChildAlreadyExistsException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public StoreChildAlreadyExistsException(String message) {
        super(message);
    }

    public StoreChildAlreadyExistsException(Throwable throwable) {
        super(throwable);
    }
}

