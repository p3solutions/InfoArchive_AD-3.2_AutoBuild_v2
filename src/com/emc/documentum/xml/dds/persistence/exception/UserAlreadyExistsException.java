/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.persistence.exception;

import com.emc.documentum.xml.dds.persistence.exception.PersistenceException;

public class UserAlreadyExistsException
extends PersistenceException {
    private static final long serialVersionUID = 1;

    public UserAlreadyExistsException() {
    }

    public UserAlreadyExistsException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public UserAlreadyExistsException(String message) {
        super(message);
    }

    public UserAlreadyExistsException(Throwable throwable) {
        super(throwable);
    }
}

