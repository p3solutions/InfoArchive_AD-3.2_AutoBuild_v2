/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.service.exception;

import com.emc.documentum.xml.dds.exception.DDSException;

public class ServiceException
extends DDSException {
    private static final long serialVersionUID = 1;

    public ServiceException() {
    }

    public ServiceException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public ServiceException(String message) {
        super(message);
    }

    public ServiceException(Throwable throwable) {
        super(throwable);
    }
}

