/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.serialization.exception;

import com.emc.documentum.xml.dds.exception.DDSException;

public class DeserializationException
extends DDSException {
    private static final long serialVersionUID = 1;

    public DeserializationException() {
    }

    public DeserializationException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public DeserializationException(String message) {
        super(message);
    }

    public DeserializationException(Throwable throwable) {
        super(throwable);
    }
}

