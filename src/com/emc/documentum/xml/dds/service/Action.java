/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.service;

import com.emc.documentum.xml.dds.util.EnumerationValue;
import java.util.HashMap;
import java.util.Map;

public enum Action implements EnumerationValue
{
    NONE("none", "None"),
    INITIALIZE("initialize", "Initialize"),
    START("start", "Start"),
    PAUSE("pause", "Pause"),
    RESUME("resume", "Resume"),
    STOP("stop", "Stop"),
    INTERNAL("internal", "Internal");
    
    private String xmlValue;
    private String userReadable;
    private static final Map<String, EnumerationValue> REVERSEMAP;

    private static void initialize() {
        for (Action value : Action.values()) {
            REVERSEMAP.put(value.getXMLValue(), value);
        }
    }

    private Action(String xmlValue, String userReadable) {
        this.xmlValue = xmlValue;
        this.userReadable = userReadable;
    }

    @Override
    public String getXMLValue() {
        return this.xmlValue;
    }

    @Override
    public String getUserReadable() {
        return this.userReadable;
    }

    public static EnumerationValue reverseLookup(String xmlValue) {
        if (REVERSEMAP.isEmpty()) {
            Action.initialize();
        }
        return REVERSEMAP.get(xmlValue);
    }

    @Override
    public EnumerationValue reverse(String xmlVal) {
        if (REVERSEMAP.isEmpty()) {
            Action.initialize();
        }
        return REVERSEMAP.get(xmlVal);
    }

    static {
        REVERSEMAP = new HashMap<String, EnumerationValue>();
    }
}

