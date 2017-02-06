/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.util.internal;

import java.util.HashMap;
import java.util.Map;
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.DOMStringList;

public class LSParserConfiguration
implements DOMConfiguration {
    private final DOMConfiguration domConfig;
    private Map<String, Object> settings = new HashMap<String, Object>();

    public LSParserConfiguration(DOMConfiguration domConfig) {
        this.domConfig = domConfig;
    }

    @Override
    public boolean canSetParameter(String name, Object value) {
        return this.domConfig.canSetParameter(name, value);
    }

    @Override
    public Object getParameter(String name) {
        if (this.settings.containsKey(name)) {
            return this.settings.get(name);
        }
        return this.domConfig.getParameter(name);
    }

    @Override
    public DOMStringList getParameterNames() {
        return this.domConfig.getParameterNames();
    }

    @Override
    public void setParameter(String name, Object value) {
        this.settings.put(name, value);
    }

    public Map<String, Object> getSettings() {
        return this.settings;
    }

    public void setSettings(Map<String, Object> settings) {
        this.settings = settings;
    }
}

