/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.service;

import com.emc.documentum.xml.dds.util.EnumerationValue;
import java.util.HashMap;
import java.util.Map;

public enum State implements EnumerationValue
{
    UNKNOWN("unknown", "Unknown"),
    STOPPED("stopped", "Stopped"),
    INITIALIZING("initializing", "Initializing"),
    INITIALIZED("initialized", "Initialized"),
    STARTING("starting", "Starting"),
    RUNNING("running", "Running"),
    PAUSING("pausing", "Pausing"),
    PAUSED("paused", "Paused"),
    RESUMING("resuming", "Resuming"),
    STOPPING("stopping", "Stopping");
    
    private String xmlValue;
    private String userReadable;
    private static final Map<String, EnumerationValue> REVERSEMAP;

    private static void initialize() {
        for (State value : State.values()) {
            REVERSEMAP.put(value.getXMLValue(), value);
        }
    }

    private State(String xmlValue, String userReadable) {
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
            State.initialize();
        }
        return REVERSEMAP.get(xmlValue);
    }

    @Override
    public EnumerationValue reverse(String xmlVal) {
        if (REVERSEMAP.isEmpty()) {
            State.initialize();
        }
        return REVERSEMAP.get(xmlVal);
    }

    static {
        REVERSEMAP = new HashMap<String, EnumerationValue>();
    }
}

