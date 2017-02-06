/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.persistence.data;

import com.emc.documentum.xml.dds.persistence.data.AbstractData;

public class StringData
extends AbstractData<String> {
    private final String string;

    public StringData(String string) {
        this.string = string;
    }

    @Override
    public String content() {
        return this.string;
    }
}

