/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.persistence.exception;

import com.emc.documentum.xml.dds.persistence.exception.PersistenceException;

public class InvalidContentException
extends PersistenceException {
    private static final long serialVersionUID = 1;

    public InvalidContentException() {
    }

    public InvalidContentException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public InvalidContentException(String message) {
        super(message);
    }

    public InvalidContentException(Throwable throwable) {
        super(throwable);
    }
}

