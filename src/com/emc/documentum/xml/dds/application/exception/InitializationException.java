/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.application.exception;

import com.emc.documentum.xml.dds.exception.DDSException;

public class InitializationException
extends DDSException {
    private static final long serialVersionUID = 1;

    public InitializationException() {
    }

    public InitializationException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public InitializationException(String message) {
        super(message);
    }

    public InitializationException(Throwable throwable) {
        super(throwable);
    }
}

