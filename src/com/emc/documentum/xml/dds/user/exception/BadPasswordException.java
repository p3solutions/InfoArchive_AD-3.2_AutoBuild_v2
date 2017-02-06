/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.user.exception;

import com.emc.documentum.xml.dds.user.exception.UserException;

public class BadPasswordException
extends UserException {
    private static final long serialVersionUID = 1;

    public BadPasswordException() {
    }

    public BadPasswordException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public BadPasswordException(String message) {
        super(message);
    }

    public BadPasswordException(Throwable throwable) {
        super(throwable);
    }
}

