/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.structure.exception;

import com.emc.documentum.xml.dds.exception.DDSException;

public class DataSetAlreadyExistsException
extends DDSException {
    private static final long serialVersionUID = 1;

    public DataSetAlreadyExistsException() {
    }

    public DataSetAlreadyExistsException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public DataSetAlreadyExistsException(String message) {
        super(message);
    }

    public DataSetAlreadyExistsException(Throwable throwable) {
        super(throwable);
    }
}

