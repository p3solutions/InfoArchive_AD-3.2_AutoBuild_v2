/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.persistence.data;

import com.emc.documentum.xml.dds.persistence.data.AbstractData;
import java.io.InputStream;

public class InputStreamData
extends AbstractData<InputStream> {
    private final InputStream inputStream;

    public InputStreamData(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    @Override
    public InputStream content() {
        return this.inputStream;
    }
}

