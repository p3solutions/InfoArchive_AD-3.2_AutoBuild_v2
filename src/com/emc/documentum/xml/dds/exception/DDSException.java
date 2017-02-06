/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.exception;

public class DDSException
extends Exception {
    private static final long serialVersionUID = 1;

    public DDSException() {
    }

    public DDSException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public DDSException(String message) {
        super(message);
    }

    public DDSException(Throwable throwable) {
        super(throwable);
    }
}

