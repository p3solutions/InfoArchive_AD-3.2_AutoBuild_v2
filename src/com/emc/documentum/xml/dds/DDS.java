/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds;

import com.emc.documentum.xml.dds.application.Application;
import com.emc.documentum.xml.dds.application.exception.InitializationException;
import com.emc.documentum.xml.dds.application.internal.ApplicationImpl;

public final class DDS {
    public static final String DDS_VERSION = "3.2";
    private static Application application;

    public static Application getApplication() {
        return application;
    }

    public static Application createApplication(String xml) throws InitializationException {
        application = new ApplicationImpl(xml);
        return application;
    }

    private DDS() {
    }
}

