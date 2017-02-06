/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  com.emc.documentum.fs.datamodel.core.ObjectId
 *  com.emc.documentum.fs.datamodel.core.properties.BooleanProperty
 *  com.emc.documentum.fs.datamodel.core.properties.DateProperty
 *  com.emc.documentum.fs.datamodel.core.properties.NumberProperty
 *  com.emc.documentum.fs.datamodel.core.properties.ObjectIdProperty
 *  com.emc.documentum.fs.datamodel.core.properties.Property
 *  com.emc.documentum.fs.datamodel.core.properties.StringProperty
 */
package com.emc.documentum.xml.dds.internal.webservice;

import com.emc.documentum.fs.datamodel.core.ObjectId;
import com.emc.documentum.fs.datamodel.core.properties.BooleanProperty;
import com.emc.documentum.fs.datamodel.core.properties.ObjectIdProperty;
import com.emc.documentum.xml.dds.fs.DateProperty;
import com.emc.documentum.xml.dds.fs.NumberProperty;
import com.emc.documentum.xml.dds.fs.Property;
import com.emc.documentum.xml.dds.fs.StringProperty;
import java.util.Date;

public final class WebService2GenericService {
    public static Property convert(com.emc.documentum.fs.datamodel.core.properties.Property property) {
        if (property instanceof com.emc.documentum.fs.datamodel.core.properties.StringProperty) {
            return WebService2GenericService.convert((com.emc.documentum.fs.datamodel.core.properties.StringProperty)property);
        }
        if (property instanceof BooleanProperty) {
            return WebService2GenericService.convert((BooleanProperty)property);
        }
        if (property instanceof com.emc.documentum.fs.datamodel.core.properties.NumberProperty) {
            return WebService2GenericService.convert((com.emc.documentum.fs.datamodel.core.properties.NumberProperty)property);
        }
        if (property instanceof ObjectIdProperty) {
            return WebService2GenericService.convert((ObjectIdProperty)property);
        }
        if (property instanceof com.emc.documentum.fs.datamodel.core.properties.DateProperty) {
            return WebService2GenericService.convert((com.emc.documentum.fs.datamodel.core.properties.DateProperty)property);
        }
        return null;
    }

    public static StringProperty convert(final com.emc.documentum.fs.datamodel.core.properties.StringProperty property) {
        return new StringProperty(){

            @Override
            public String getName() {
                return property.getName();
            }

            @Override
            public String getValue() {
                return property.getValue();
            }
        };
    }

    public static com.emc.documentum.xml.dds.fs.BooleanProperty convert(final BooleanProperty property) {
        return new com.emc.documentum.xml.dds.fs.BooleanProperty(){

            @Override
            public String getName() {
                return property.getName();
            }

            @Override
            public boolean getValue() {
                return property.getValue();
            }
        };
    }

    public static NumberProperty convert(final com.emc.documentum.fs.datamodel.core.properties.NumberProperty property) {
        return new NumberProperty(){

            @Override
            public String getName() {
                return property.getName();
            }

            @Override
            public Number getValue() {
                return property.getValue();
            }
        };
    }

    public static StringProperty convert(final ObjectIdProperty property) {
        return new StringProperty(){

            @Override
            public String getName() {
                return property.getName();
            }

            @Override
            public String getValue() {
                ObjectId id = property.getValue();
                return id != null ? id.getId() : null;
            }
        };
    }

    public static DateProperty convert(final com.emc.documentum.fs.datamodel.core.properties.DateProperty property) {
        return new DateProperty(){

            @Override
            public String getName() {
                return property.getName();
            }

            @Override
            public Date getValue() {
                return property.getValue();
            }
        };
    }

    private WebService2GenericService() {
    }

}

