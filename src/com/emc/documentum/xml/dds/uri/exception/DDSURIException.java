/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.uri.exception;

import com.emc.documentum.xml.dds.exception.DDSException;

public class DDSURIException
extends DDSException {
    private static final long serialVersionUID = 1;

    public DDSURIException() {
    }

    public DDSURIException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public DDSURIException(String message) {
        super(message);
    }

    public DDSURIException(Throwable throwable) {
        super(throwable);
    }
}

