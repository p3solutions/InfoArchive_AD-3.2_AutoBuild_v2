/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.persistence.metadata;

import com.emc.documentum.xml.dds.persistence.Data;
import com.emc.documentum.xml.dds.persistence.Metadata;
import com.emc.documentum.xml.dds.persistence.MetadataScheme;

public class DocumentumMetadata
implements Metadata {
    private Data<?> content;

    public DocumentumMetadata(Data<?> data) {
        this.content = data;
    }

    public Data<?> getData() {
        return this.content;
    }

    public void setData(Data<?> data) {
        this.content = data;
    }

    @Override
    public MetadataScheme getScheme() {
        return MetadataScheme.DOCUMENTUM;
    }
}

