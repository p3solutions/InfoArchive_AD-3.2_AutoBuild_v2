/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.xproc.exception;

import com.emc.documentum.xml.dds.exception.DDSException;

public class DDSXProcConfigurationException
extends DDSException {
    private static final long serialVersionUID = 1;

    public DDSXProcConfigurationException() {
    }

    public DDSXProcConfigurationException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public DDSXProcConfigurationException(String message) {
        super(message);
    }

    public DDSXProcConfigurationException(Throwable throwable) {
        super(throwable);
    }
}

