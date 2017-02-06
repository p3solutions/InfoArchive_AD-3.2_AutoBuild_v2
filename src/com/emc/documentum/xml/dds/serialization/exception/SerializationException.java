/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.serialization.exception;

import com.emc.documentum.xml.dds.exception.DDSException;

public class SerializationException
extends DDSException {
    private static final long serialVersionUID = 1;

    public SerializationException() {
    }

    public SerializationException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public SerializationException(String message) {
        super(message);
    }

    public SerializationException(Throwable throwable) {
        super(throwable);
    }
}

