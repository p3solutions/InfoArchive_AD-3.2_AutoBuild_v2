/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  com.thoughtworks.xstream.XStream
 *  com.thoughtworks.xstream.XStreamException
 */
package com.emc.documentum.xml.dds.serialization.xstream;

import com.emc.documentum.xml.dds.serialization.Serializer;
import com.emc.documentum.xml.dds.serialization.SerializerOptions;
import com.emc.documentum.xml.dds.serialization.SerializerType;
import com.emc.documentum.xml.dds.serialization.exception.DeserializationException;
import com.emc.documentum.xml.dds.serialization.exception.SerializationException;
import com.emc.documentum.xml.dds.serialization.xstream.XStreamClassSerializationOptions;
import com.emc.documentum.xml.dds.serialization.xstream.XStreamSerializerOptions;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.XStreamException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class XStreamSerializer
implements Serializer {
    private XStream xstream = new XStream();
    private XStreamSerializerOptions options;

    @Override
    public SerializerType getType() {
        return SerializerType.XSTREAM;
    }

    @Override
    public SerializerOptions getOptions() {
        if (this.options == null) {
            this.options = new XStreamSerializerOptions();
        }
        return this.options;
    }

    @Override
    public void setOptions(SerializerOptions options) {
        this.options = (XStreamSerializerOptions)options;
        this.applyOptions();
    }

    @Override
    public Object deserialize(String serialized) throws DeserializationException {
        try {
            return this.xstream.fromXML(serialized);
        }
        catch (XStreamException xse) {
            throw new DeserializationException("Deserialization failed :", (Throwable)xse);
        }
    }

    @Override
    public Object deserialize(InputStream serialized) throws DeserializationException {
        try {
            Object result = this.xstream.fromXML(serialized);
            serialized.close();
            return result;
        }
        catch (IOException ioe) {
            throw new DeserializationException("Deserialization failed :", ioe);
        }
        catch (XStreamException xse) {
            throw new DeserializationException("Deserialization failed :", (Throwable)xse);
        }
    }

    @Override
    public Object deserialize(Reader serialized) throws DeserializationException {
        try {
            Object result = this.xstream.fromXML(serialized);
            serialized.close();
            return result;
        }
        catch (XStreamException xse) {
            throw new DeserializationException("Deserialization failed :", (Throwable)xse);
        }
        catch (IOException ioe) {
            throw new DeserializationException("Deserialization failed :", ioe);
        }
    }

    @Override
    public String serialize(Object object) throws SerializationException {
        try {
            return this.xstream.toXML(object);
        }
        catch (XStreamException xse) {
            throw new SerializationException("Serialization failed :", (Throwable)xse);
        }
    }

    @Override
    public void serialize(Object object, OutputStream outputStream) throws SerializationException {
        try {
            this.xstream.toXML(object, outputStream);
        }
        catch (XStreamException xse) {
            throw new SerializationException("Serialization failed :", (Throwable)xse);
        }
    }

    @Override
    public void serialize(Object object, Writer writer) throws SerializationException {
        try {
            this.xstream.toXML(object, writer);
        }
        catch (XStreamException xse) {
            throw new SerializationException("Serialization failed :", (Throwable)xse);
        }
    }

    @Override
    public void applyOptions() {
        this.xstream = new XStream();
        for (XStreamClassSerializationOptions classOptions : this.options.getAllOptions()) {
            Class objectClass = classOptions.getObjectClass();
            if (classOptions.getClassAlias() != null) {
                this.xstream.alias(classOptions.getClassAlias(), objectClass);
            }
            for (String fieldName22 : classOptions.getOmittedFields()) {
                this.xstream.omitField(objectClass, fieldName22);
            }
            for (String fieldName : classOptions.getAttributes()) {
                this.xstream.useAttributeFor(objectClass, fieldName);
            }
            for (Map.Entry fieldAlias : classOptions.getFieldAliases().entrySet()) {
                this.xstream.aliasField((String)fieldAlias.getValue(), objectClass, (String)fieldAlias.getKey());
            }
            for (Map.Entry attributeAlias : classOptions.getAttributeAliases().entrySet()) {
                this.xstream.aliasAttribute(objectClass, (String)attributeAlias.getKey(), (String)attributeAlias.getValue());
            }
            for (String fieldName2 : classOptions.getDefaultImplicitCollections()) {
                this.xstream.addImplicitCollection(objectClass, fieldName2);
            }
            Map<String, Map<Class<?>, String>> entryMap = classOptions.getEntryAliases();
            for (String fieldName3 : entryMap.keySet()) {
                for (Class entryClass : entryMap.get(fieldName3).keySet()) {
                    this.xstream.addImplicitCollection(objectClass, fieldName3, entryMap.get(fieldName3).get(entryClass), entryClass);
                }
            }
        }
    }
}

