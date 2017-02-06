/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.usi;

import org.w3c.dom.Node;

public interface Response {
    public static final String TIME = "time";
    public static final String REQUEST = "request";
    public static final String CONTEXT = "context";

    public Node asNode();

    public String asString();

    public String getRequestId();

    public String getContext();

    public String getTime();
}

