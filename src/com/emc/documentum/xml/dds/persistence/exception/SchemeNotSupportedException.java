/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.persistence.exception;

import com.emc.documentum.xml.dds.persistence.exception.PersistenceException;

public class SchemeNotSupportedException
extends PersistenceException {
    private static final long serialVersionUID = 1;

    public SchemeNotSupportedException() {
    }

    public SchemeNotSupportedException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public SchemeNotSupportedException(String message) {
        super(message);
    }

    public SchemeNotSupportedException(Throwable throwable) {
        super(throwable);
    }
}

