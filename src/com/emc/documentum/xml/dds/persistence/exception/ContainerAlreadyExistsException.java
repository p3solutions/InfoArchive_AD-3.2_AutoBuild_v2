/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.persistence.exception;

import com.emc.documentum.xml.dds.persistence.exception.StoreChildAlreadyExistsException;

public class ContainerAlreadyExistsException
extends StoreChildAlreadyExistsException {
    private static final long serialVersionUID = 1;

    public ContainerAlreadyExistsException() {
    }

    public ContainerAlreadyExistsException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public ContainerAlreadyExistsException(String message) {
        super(message);
    }

    public ContainerAlreadyExistsException(Throwable throwable) {
        super(throwable);
    }
}

