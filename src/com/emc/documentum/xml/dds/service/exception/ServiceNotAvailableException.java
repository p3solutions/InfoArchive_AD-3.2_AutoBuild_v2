/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.service.exception;

import com.emc.documentum.xml.dds.exception.DDSException;

public class ServiceNotAvailableException
extends DDSException {
    private static final long serialVersionUID = 1;

    public ServiceNotAvailableException() {
    }

    public ServiceNotAvailableException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public ServiceNotAvailableException(String message) {
        super(message);
    }

    public ServiceNotAvailableException(Throwable throwable) {
        super(throwable);
    }
}

