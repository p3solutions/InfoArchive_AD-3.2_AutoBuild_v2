/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.persistence.exception;

import com.emc.documentum.xml.dds.persistence.exception.PersistenceException;

public class StoreSpecificException
extends PersistenceException {
    private static final long serialVersionUID = 1;

    public StoreSpecificException() {
    }

    public StoreSpecificException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public StoreSpecificException(String message) {
        super(message);
    }

    public StoreSpecificException(Throwable throwable) {
        super(throwable);
    }
}

