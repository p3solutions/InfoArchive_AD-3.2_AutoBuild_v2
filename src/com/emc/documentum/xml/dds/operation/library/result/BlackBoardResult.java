/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.operation.library.result;

import java.util.HashMap;

public class BlackBoardResult
extends HashMap<String, Object> {
    private static final long serialVersionUID = 1;

    public void addResult(String id, Object result) {
        this.put(id, result);
    }
}

