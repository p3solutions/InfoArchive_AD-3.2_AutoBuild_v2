/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.structure.exception;

import com.emc.documentum.xml.dds.exception.DDSException;

public class StructureException
extends DDSException {
    private static final long serialVersionUID = 1;

    public StructureException() {
    }

    public StructureException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public StructureException(String message) {
        super(message);
    }

    public StructureException(Throwable throwable) {
        super(throwable);
    }
}

