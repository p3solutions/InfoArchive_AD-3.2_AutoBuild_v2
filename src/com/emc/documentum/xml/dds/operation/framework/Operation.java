/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.operation.framework;

import java.util.List;

public interface Operation<T> {
    public String getId();

    public void setId(String var1);

    public boolean isReadOnly(String var1);

    public String getExecutableClassName();

    public List<String> getStoreAliases();
}

