/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.persistence.exception;

import com.emc.documentum.xml.dds.persistence.exception.StoreChildNotFoundException;

public class LocationNotFoundException
extends StoreChildNotFoundException {
    private static final long serialVersionUID = 1;

    public LocationNotFoundException() {
    }

    public LocationNotFoundException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public LocationNotFoundException(String message) {
        super(message);
    }

    public LocationNotFoundException(Throwable throwable) {
        super(throwable);
    }
}

