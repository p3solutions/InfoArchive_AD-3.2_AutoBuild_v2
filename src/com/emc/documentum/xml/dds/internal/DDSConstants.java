/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.internal;

public final class DDSConstants {
    public static final String APP_LIBRARY = "APPLICATIONS";
    public static final String DATA_LIBRARY = "DATA";
    public static final String SYSTEM_LIBRARY = "SYSTEM";
    public static final String USER_LIBRARY = "users";
    public static final String RESOURCE_LIBRARY = "resources";
    public static final String CONFIGURATION_LIBRARY = "configuration";
    public static final String STORESCONFIGURATIONNAME = "Stores.xml";
    public static final String SERVICESCONFIGURATIONNAME = "Services.xml";
    public static final String STRUCTURESCONFIGURATIONNAME = "Structures.xml";
    public static final String XBASESCONFIGURATIONNAME = "XBases.xml";
    public static final String COLLECTION_DEFAULT = "Collection";
    public static final String COLLECTION_METADATA_DEFAULT = "CollectionMetadata";
    public static final String METADATA_PREFIX = "dds:";
    public static final String DDS_METADATA_SUBSCRIPTION_ELEMENT = "dds:subscription-element";
    public static final String DDS_METADATA_FILENAME = "dds:content-filename";
    public static final String DDS_SCHEMA_ID = "dds:schema-id";
    public static final String DDS_PROPERTY_APPLICATION_CONFIG_PATH = "dds.application.config.path";
    public static final String DDS_DEFAULT_APPLICATION_CONFIG_NAME = "application-bootstrap.xml";
    public static final String DDS_METADATA_LOCALE = "dds:locale";
    public static final String DDS_METADATA_CONTENT_LOCALE = "dds:content-locale";
    public static final int CACHE_PAGES = 10000;
    public static final String SCHEMA_TYPE_XSD = "http://www.w3.org/2001/XMLSchema";
    public static final String SCHEMA_TYPE_DTD = "http://www.w3.org/TR/REC-xml";
    public static final String PARSE_OPTION_VALIDATE = "validate";
    public static final String PARAMETER_SCHEMA_LOCATION = "schema-location";
    public static final String PARAMETER_ERROR_HANDLER = "error-handler";
    public static final String PARAMETER_SCHEMA_TYPE = "schema-type";
    public static final short LIBRARY_LOCK_WITH_PARENT = 1;
    public static final short LIBRARY_DOCUMENTS_DO_NOT_LOCK_WITH_PARENT = 4;
    @Deprecated
    public static final short LIBRARY_CONCURRENT_NAMEBASE = 64;
    public static final short LIBRARY_CONCURRENT_LIBRARY = 128;
    public static final short DEFAULT_LIBRARY_OPTIONS = 128;
    public static final String DDS_NAMESPACE_URI = "http://www.emc.com/documentum/xml/dds";

    private DDSConstants() {
    }
}

