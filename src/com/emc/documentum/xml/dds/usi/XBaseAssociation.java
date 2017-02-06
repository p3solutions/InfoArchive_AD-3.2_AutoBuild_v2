/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.usi;

import com.emc.documentum.xml.dds.configuration.Configuration;
import com.emc.documentum.xml.dds.usi.ResponseFilter;

public class XBaseAssociation
implements Configuration {
    private ResponseFilter filter;
    private String xBaseId;

    public XBaseAssociation(ResponseFilter filter, String xBaseId) {
        this.filter = filter;
        this.xBaseId = xBaseId;
    }

    public ResponseFilter getFilter() {
        return this.filter;
    }

    public void setFilter(ResponseFilter filter) {
        this.filter = filter;
    }

    public String getXBaseId() {
        return this.xBaseId == null ? null : this.xBaseId.trim();
    }

    public void setXBaseId(String id) {
        this.xBaseId = id;
    }
}

