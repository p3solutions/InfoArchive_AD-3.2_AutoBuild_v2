/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.persistence.exception;

import com.emc.documentum.xml.dds.persistence.exception.StoreChildNotFoundException;

public class NodeNotFoundException
extends StoreChildNotFoundException {
    private static final long serialVersionUID = 1;

    public NodeNotFoundException() {
    }

    public NodeNotFoundException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public NodeNotFoundException(String message) {
        super(message);
    }

    public NodeNotFoundException(Throwable throwable) {
        super(throwable);
    }
}

