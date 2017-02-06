/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.persistence.exception;

import com.emc.documentum.xml.dds.persistence.exception.StoreChildNotFoundException;

public class ContainerNotFoundException
extends StoreChildNotFoundException {
    private static final long serialVersionUID = 1;

    public ContainerNotFoundException() {
    }

    public ContainerNotFoundException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public ContainerNotFoundException(String message) {
        super(message);
    }

    public ContainerNotFoundException(Throwable throwable) {
        super(throwable);
    }
}

