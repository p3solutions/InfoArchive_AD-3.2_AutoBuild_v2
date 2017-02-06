/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.persistence.exception;

import com.emc.documentum.xml.dds.persistence.exception.PersistenceException;

public class UserNotFoundException
extends PersistenceException {
    private static final long serialVersionUID = 1;

    public UserNotFoundException() {
    }

    public UserNotFoundException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public UserNotFoundException(String message) {
        super(message);
    }

    public UserNotFoundException(Throwable throwable) {
        super(throwable);
    }
}

