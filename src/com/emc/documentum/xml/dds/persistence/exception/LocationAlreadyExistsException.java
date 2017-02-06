/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.persistence.exception;

import com.emc.documentum.xml.dds.persistence.exception.StoreChildAlreadyExistsException;

public class LocationAlreadyExistsException
extends StoreChildAlreadyExistsException {
    private static final long serialVersionUID = 1;

    public LocationAlreadyExistsException() {
    }

    public LocationAlreadyExistsException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public LocationAlreadyExistsException(String message) {
        super(message);
    }

    public LocationAlreadyExistsException(Throwable throwable) {
        super(throwable);
    }
}

