/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.operation.library.basic;

import com.emc.documentum.xml.dds.operation.library.basic.AbstractOperation;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractNoStoreOperation<T>
extends AbstractOperation<T> {
    @Override
    public List<String> getStoreAliases() {
        return new ArrayList<String>();
    }
}

