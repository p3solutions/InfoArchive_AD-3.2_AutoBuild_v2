/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.service;

import com.emc.documentum.xml.dds.service.ServiceType;
import com.emc.documentum.xml.dds.util.EnumerationValue;
import java.util.HashMap;
import java.util.Map;

public enum DDSServiceType implements ServiceType
{
    APPLICATION("application", "Application"),
    USER("userservice", "UserService"),
    TOKEN("tokenservice", "TokenService"),
    XPROC("xprocservice", "XProcService"),
    LOGICENGINE("logicengineservice", "LogicEngineService"),
    RESPONSE("responseservice", "ResponseService"),
    SCHEDULE("schedulingservice", "SchedulingService");
    
    private String xmlValue;
    private String userReadable;
    private static final Map<String, EnumerationValue> REVERSEMAP;

    private static void initialize() {
        for (DDSServiceType value : DDSServiceType.values()) {
            REVERSEMAP.put(value.getXMLValue(), value);
        }
    }

    private DDSServiceType(String xmlValue, String userReadable) {
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
            DDSServiceType.initialize();
        }
        return REVERSEMAP.get(xmlValue);
    }

    @Override
    public EnumerationValue reverse(String xmlVal) {
        if (REVERSEMAP.isEmpty()) {
            DDSServiceType.initialize();
        }
        return REVERSEMAP.get(xmlVal);
    }

    static {
        REVERSEMAP = new HashMap<String, EnumerationValue>();
    }
}

