/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.user.exception;

import com.emc.documentum.xml.dds.exception.DDSException;

public class UserException
extends DDSException {
    private static final long serialVersionUID = 1;

    public UserException() {
    }

    public UserException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public UserException(String message) {
        super(message);
    }

    public UserException(Throwable throwable) {
        super(throwable);
    }
}

