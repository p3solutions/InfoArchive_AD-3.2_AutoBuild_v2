/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  com.emc.documentum.fs.datamodel.core.properties.DefaultPropertyValueVisitorFactory
 *  com.emc.documentum.fs.datamodel.core.properties.IPropertyValueVisitor
 *  com.emc.documentum.fs.datamodel.core.properties.Property
 */
package com.emc.documentum.xml.dds.internal.webservice;

import com.emc.documentum.fs.datamodel.core.properties.DefaultPropertyValueVisitorFactory;
import com.emc.documentum.fs.datamodel.core.properties.IPropertyValueVisitor;
import com.emc.documentum.fs.datamodel.core.properties.Property;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

@XmlType(name="PropertySet", namespace="http://datamodel.dds.xml.documentum.emc.com/")
@XmlAccessorType(value=XmlAccessType.FIELD)
public class PropertySet
implements Cloneable {
    @XmlElements(value={@XmlElement(name="Property", namespace="http://properties.core.datamodel.fs.documentum.emc.com/")})
    private final List<Property> properties = new ArrayList<Property>();
    @XmlAttribute
    private boolean isInternal;
    @XmlTransient
    private final HashMap<String, Property> propertyMap = new HashMap();

    public PropertySet() {
    }

    public /* varargs */ PropertySet(Property ... properties) {
        this();
        this.set(properties);
    }

    public <T> Property set(String name, T value) {
        Property newProperty = this.newProperty(name, value);
        this.internalSetProperty(newProperty);
        return newProperty;
    }

    public /* varargs */ void set(Property ... props) {
        for (Property property : props) {
            this.internalSetProperty(property);
        }
    }

    private <T> Property newProperty(String name, T value) {
        IPropertyValueVisitor visitor = DefaultPropertyValueVisitorFactory.getInstance().createVisitor();
        visitor.visit(value);
        Property retval = (Property)visitor.getResult();
        retval.setName(name);
        return retval;
    }

    public Iterator<Property> iterator() {
        return this.properties.iterator();
    }

    public Property get(String name) {
        if (this.propertyMap.size() != this.properties.size()) {
            this.propertyMap.clear();
            for (Property p : this.properties) {
                this.propertyMap.put(p.getName(), p);
            }
        }
        return this.propertyMap.get(name);
    }

    @XmlTransient
    public String getValueAsString() {
        StringBuilder buffer = new StringBuilder();
        Iterator<Property> iterator = this.iterator();
        while (iterator.hasNext()) {
            Property property = iterator.next();
            buffer.append(property.getName()).append("=").append(property.getValueAsString());
            if (!iterator.hasNext()) continue;
            buffer.append("|");
        }
        return buffer.toString();
    }

    private void internalSetProperty(Property property) {
        String propertyName = property.getName();
        if (this.propertyMap.get(propertyName) != null) {
            this.propertyMap.remove(propertyName);
            Iterator<Property> iterator = this.properties.iterator();
            while (iterator.hasNext()) {
                Property p = iterator.next();
                if (!p.getName().equals(property.getName())) continue;
                iterator.remove();
            }
        }
        this.propertyMap.put(propertyName, property);
        this.properties.add(property);
    }

    public PropertySet clone() throws CloneNotSupportedException {
        super.clone();
        PropertySet set = new PropertySet();
        for (Property property : this.properties) {
            set.set(property.clone());
        }
        return set;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        PropertySet that = (PropertySet)o;
        if (that.properties.size() != this.properties.size()) {
            return false;
        }
        for (Property p : this.properties) {
            if (that.properties.contains((Object)p)) continue;
            return false;
        }
        return true;
    }

    public int hashCode() {
        return this.properties.hashCode();
    }

    public String toString() {
        return this.properties.toString();
    }

    public List<Property> getProperties() {
        return this.properties;
    }
}

