/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.serialization;

import com.emc.documentum.xml.dds.serialization.SerializerOptions;
import com.emc.documentum.xml.dds.serialization.SerializerType;
import com.emc.documentum.xml.dds.serialization.exception.DeserializationException;
import com.emc.documentum.xml.dds.serialization.exception.SerializationException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

public interface Serializer {
    public SerializerType getType();

    public SerializerOptions getOptions();

    public void setOptions(SerializerOptions var1);

    public void applyOptions();

    public String serialize(Object var1) throws SerializationException;

    public void serialize(Object var1, OutputStream var2) throws SerializationException;

    public void serialize(Object var1, Writer var2) throws SerializationException;

    public Object deserialize(String var1) throws DeserializationException;

    public Object deserialize(InputStream var1) throws DeserializationException;

    public Object deserialize(Reader var1) throws DeserializationException;
}

