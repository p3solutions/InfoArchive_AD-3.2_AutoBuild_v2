/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.structure.exception;

import com.emc.documentum.xml.dds.exception.DDSException;

public class DataSetNotFoundException
extends DDSException {
    private static final long serialVersionUID = 1;

    public DataSetNotFoundException() {
    }

    public DataSetNotFoundException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public DataSetNotFoundException(String message) {
        super(message);
    }

    public DataSetNotFoundException(Throwable throwable) {
        super(throwable);
    }
}

