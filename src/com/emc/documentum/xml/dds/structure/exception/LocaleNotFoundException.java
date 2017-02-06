/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.structure.exception;

import com.emc.documentum.xml.dds.exception.DDSException;

public class LocaleNotFoundException
extends DDSException {
    private static final long serialVersionUID = 1;

    public LocaleNotFoundException() {
    }

    public LocaleNotFoundException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public LocaleNotFoundException(String message) {
        super(message);
    }

    public LocaleNotFoundException(Throwable throwable) {
        super(throwable);
    }
}

