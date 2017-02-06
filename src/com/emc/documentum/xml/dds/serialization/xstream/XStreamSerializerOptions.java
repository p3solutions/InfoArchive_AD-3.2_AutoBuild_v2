/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.serialization.xstream;

import com.emc.documentum.xml.dds.serialization.SerializerOptions;
import com.emc.documentum.xml.dds.serialization.SerializerType;
import com.emc.documentum.xml.dds.serialization.xstream.XStreamClassSerializationOptions;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class XStreamSerializerOptions
implements SerializerOptions {
    private final Map<Class<?>, XStreamClassSerializationOptions> classMap = new HashMap();

    @Override
    public SerializerType getType() {
        return SerializerType.XSTREAM;
    }

    public Collection<XStreamClassSerializationOptions> getAllOptions() {
        return this.classMap.values();
    }

    public XStreamClassSerializationOptions getOptionsForClass(Class<?> objectClass) {
        XStreamClassSerializationOptions options = this.classMap.get(objectClass);
        if (options == null) {
            options = new XStreamClassSerializationOptions();
            options.setObjectClass(objectClass);
            this.classMap.put(objectClass, options);
        }
        return options;
    }

    public void addClassOptions(XStreamClassSerializationOptions options) {
        this.classMap.put(options.getObjectClass(), options);
    }
}

