/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.structure.exception;

import com.emc.documentum.xml.dds.exception.DDSException;

public class LocaleAlreadyExistsException
extends DDSException {
    private static final long serialVersionUID = 1;

    public LocaleAlreadyExistsException() {
    }

    public LocaleAlreadyExistsException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public LocaleAlreadyExistsException(String message) {
        super(message);
    }

    public LocaleAlreadyExistsException(Throwable throwable) {
        super(throwable);
    }
}

