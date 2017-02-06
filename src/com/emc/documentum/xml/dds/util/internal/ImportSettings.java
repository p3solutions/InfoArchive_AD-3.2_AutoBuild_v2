/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.util.internal;

import com.emc.documentum.xml.dds.util.internal.LSParserConfiguration;
import org.w3c.dom.DOMConfiguration;

public class ImportSettings {
    private int libraryOptions = 128;
    private final LSParserConfiguration domConfiguration;
    private String locale;
    private String retentionLibName;

    public ImportSettings(DOMConfiguration domConfiguration) {
        this.domConfiguration = new LSParserConfiguration(domConfiguration);
    }

    public int getLibraryOptions() {
        return this.libraryOptions;
    }

    public void setLibraryOptions(int libraryOptions) {
        this.libraryOptions = libraryOptions;
    }

    public LSParserConfiguration getDOMConfiguration() {
        return this.domConfiguration;
    }

    public String getLocale() {
        return this.locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getRetentionLibName() {
        return this.retentionLibName;
    }

    public void setRetentionLibName(String retentionLibName) {
        this.retentionLibName = retentionLibName;
    }
}

