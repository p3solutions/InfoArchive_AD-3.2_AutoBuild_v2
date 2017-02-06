/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.gwt.client.util;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class DDSURI {
    public static final String SCHEME_DDS = "dds";
    public static final String ATTRIBUTE_DOMAIN = "DOMAIN";
    public static final String ATTRIBUTE_STORE = "STORE";
    public static final String ATTRIBUTE_DATASET = "DATASET";
    public static final String ATTRIBUTE_LOCALE = "LOCALE";
    public static final String DOMAIN_DATA = "data";
    public static final String DOMAIN_RESOURCE = "resource";
    public static final String DOMAIN_USER = "user";
    public static final String DOMAIN_DOCUMENTUM_METADATA = "documentum-metadata";
    private final DomainDescriptor domainDescriptor;
    private String domainSpecificPart;
    private final boolean relative;

    public static DDSURI parseURI(String uri) {
        String[] tokens;
        Objects.requireNonNull(uri, "<null> uri");
        int colonIndex = -1;
        boolean relative = false;
        if (uri.matches("[a-zA-Z]+:.*")) {
            colonIndex = uri.indexOf(58);
            String scheme = uri.substring(0, colonIndex);
            if (!"dds".equals(scheme)) {
                throw new IllegalArgumentException("Unsupported URI scheme: " + scheme);
            }
        } else {
            relative = true;
        }
        String schemeSpecificPart = uri.substring(colonIndex + 1);
        String domainDescriptorPart = null;
        String domainSpecificPart = null;
        if (schemeSpecificPart.startsWith("//")) {
            int endSlash = (schemeSpecificPart = schemeSpecificPart.substring(2)).indexOf(47);
            if (endSlash == -1) {
                domainDescriptorPart = schemeSpecificPart;
                domainSpecificPart = "";
            } else {
                domainDescriptorPart = schemeSpecificPart.substring(0, endSlash);
                domainSpecificPart = schemeSpecificPart.substring(endSlash);
            }
        } else {
            domainDescriptorPart = "";
            domainSpecificPart = schemeSpecificPart;
        }
        HashMap<String, String> attributes = new HashMap<String, String>();
        for (String token : tokens = domainDescriptorPart.split(";")) {
            int equalsIndex = token.indexOf(61);
            if (equalsIndex == -1) continue;
            String attr = token.substring(0, equalsIndex);
            String value = token.substring(equalsIndex + 1);
            attributes.put(attr, value);
        }
        return new DDSURI(attributes, domainSpecificPart, relative);
    }

    public DDSURI(String domainSpecificPart) {
        this(null, domainSpecificPart);
    }

    public DDSURI(String domainSpecificPart, boolean relative) {
        this(null, domainSpecificPart, relative);
    }

    public DDSURI(Map<String, String> domainAttributes, String domainSpecificPart) {
        this(domainAttributes, domainSpecificPart, false);
    }

    public DDSURI(Map<String, String> domainAttributes, String domainSpecificPart, boolean relative) {
        this.domainDescriptor = new DomainDescriptor(domainAttributes);
        this.domainSpecificPart = DDSURI.getDomainSpecificPart(domainSpecificPart);
        this.relative = relative;
    }

    public Map<String, String> getAttributes() {
        return this.domainDescriptor.getAttributes();
    }

    public String getAttribute(String attribute) {
        return this.domainDescriptor.getAttribute(attribute);
    }

    public DDSURI setAttribute(String attribute, String value) {
        this.domainDescriptor.setAttribute(attribute, value);
        return this;
    }

    public String getDomainSpecificPart() {
        return this.domainSpecificPart;
    }

    public void setDomainSpecificPart(String domainSpecificPart) {
        this.domainSpecificPart = DDSURI.getDomainSpecificPart(domainSpecificPart);
    }

    public static String escapeURI(String uri) {
        StringBuilder result = new StringBuilder();
        boolean escapeReserved = false;
        for (int i = 0; i < uri.length(); ++i) {
            char c = uri.charAt(i);
            if (c == '%') {
                if (i >= uri.length() - 2 || !Character.isLetterOrDigit(uri.charAt(i + 1)) || !Character.isLetterOrDigit(uri.charAt(i + 2))) continue;
                result.append(c);
                continue;
            }
            if (Character.isLetterOrDigit(c) || c == '-' || c == '_' || c == '.' || c == '!' || c == '~' || c == '*' || c == '\'' || c == '(' || c == ')') {
                result.append(c);
                continue;
            }
            if (!(escapeReserved || c != ';' && c != '/' && c != '?' && c != ':' && c != '@' && c != '&' && c != '=' && c != '+' && c != '$' && c != ',' && c != '#')) {
                result.append(c);
                continue;
            }
            result.append('%');
            String hex = Integer.toHexString(c & 255);
            result.append(hex.toUpperCase(Locale.ENGLISH));
        }
        return result.toString();
    }

    public String toString() {
        String schemeSpecificPart = this.createDomainDescriptorString() + (this.domainSpecificPart == null ? "" : this.domainSpecificPart);
        return DDSURI.escapeURI((this.relative ? "" : "dds:") + schemeSpecificPart);
    }

    private static String getDomainSpecificPart(String domainSpecificPart) {
        String result = domainSpecificPart;
        if (result == null) {
            result = "";
        }
        if (!"".equals(result) && !result.startsWith("/")) {
            result = "" + '/' + result;
        }
        return result;
    }

    private String createDomainDescriptorString() {
        Map<String, String> attributes;
        StringBuilder result = new StringBuilder();
        if (this.domainDescriptor != null && (attributes = this.domainDescriptor.getAttributes()) != null) {
            boolean dirty = false;
            for (Map.Entry<String, String> entry : attributes.entrySet()) {
                String attribute = entry.getKey();
                String value = entry.getValue();
                if (value == null) continue;
                if (dirty) {
                    result.append(';');
                } else {
                    dirty = true;
                }
                result.append(attribute).append('=').append(value);
            }
        }
        return result.length() == 0 ? "" : "//" + result.toString();
    }

    public static final class DomainDescriptor {
        private final Map<String, String> attributes = new HashMap<String, String>();

        public DomainDescriptor() {
        }

        public DomainDescriptor(Map<String, String> attrs) {
            if (attrs != null) {
                for (Map.Entry<String, String> entry : attrs.entrySet()) {
                    String attr = entry.getKey();
                    String value = entry.getValue();
                    this.setAttribute(attr, value);
                }
            }
        }

        public void setAttribute(String attribute, String value) {
            this.attributes.put(attribute.toUpperCase(Locale.ENGLISH), value);
        }

        public String getAttribute(String attribute) {
            return this.attributes.get(attribute.toUpperCase(Locale.ENGLISH));
        }

        public Map<String, String> getAttributes() {
            return this.attributes;
        }
    }

}

