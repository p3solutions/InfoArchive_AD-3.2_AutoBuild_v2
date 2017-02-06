/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.uri;

import com.emc.documentum.xml.dds.uri.exception.DDSURIException;
import com.emc.documentum.xml.dds.util.internal.StringUtils;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.StringTokenizer;

public final class DDSURI {
    public static final String SCHEME_DDS = "dds";
    private URI uURI;
    private final DomainDescriptor domainDescriptor;
    private String domainSpecificPart;

    public static DDSURI parseURI(String uri) throws DDSURIException {
        Objects.requireNonNull(uri, "<null> uri");
        try {
            return new DDSURI(new URI(uri));
        }
        catch (URISyntaxException use) {
            throw new DDSURIException(use);
        }
    }

    public DDSURI(URI uri) throws DDSURIException {
        Objects.requireNonNull(uri, "<null> uri");
        this.uURI = DDSURI.getURIChecked(uri);
        this.domainDescriptor = DDSURI.getDomainDescriptor(this.uURI);
        this.domainSpecificPart = DDSURI.getDomainSpecificPartFromURI(this.uURI);
    }

    public DDSURI(String domainSpecificPart) throws DDSURIException {
        this(null, domainSpecificPart, false);
    }

    public DDSURI(String domainSpecificPart, boolean relative) throws DDSURIException {
        this(null, domainSpecificPart, relative);
    }

    public DDSURI(Map<String, String> domainAttributes, String domainSpecificPart) throws DDSURIException {
        this(domainAttributes, domainSpecificPart, false);
    }

    public DDSURI(Map<String, String> domainAttributes, String domainSpecificPart, boolean relative) throws DDSURIException {
        this.domainDescriptor = new DomainDescriptor(domainAttributes);
        this.domainSpecificPart = DDSURI.getDomainSpecificPart(domainSpecificPart, relative);
        this.uURI = DDSURI.newInternalURI(this.domainDescriptor, this.domainSpecificPart, relative);
    }

    public Map<String, String> getAttributes() {
        return this.domainDescriptor.getAttributes();
    }

    public String getAttribute(String attribute) {
        Objects.requireNonNull(attribute, "<null> attribute");
        return this.domainDescriptor.getAttribute(attribute);
    }

    public DDSURI setAttribute(String attribute, String value) throws DDSURIException {
        Objects.requireNonNull(attribute, "<null> attribute");
        if (StringUtils.isEmpty(attribute)) {
            throw new DDSURIException("Domain attribute cannot be empty");
        }
        this.domainDescriptor.setAttribute(attribute, value);
        this.uURI = DDSURI.newInternalURI(this.domainDescriptor, this.domainSpecificPart, !this.uURI.isAbsolute());
        return this;
    }

    public String getDomainSpecificPart() {
        return this.domainSpecificPart;
    }

    public void setDomainSpecificPart(String domainSpecificPart) throws DDSURIException {
        this.domainSpecificPart = DDSURI.getDomainSpecificPart(domainSpecificPart, !this.uURI.isAbsolute());
        this.uURI = DDSURI.newInternalURI(this.domainDescriptor, domainSpecificPart, !this.uURI.isAbsolute());
    }

    public String toString() {
        return this.uURI.toString();
    }

    private static URI newInternalURI(DomainDescriptor domainDescriptor, String domainSpecificPart, boolean relative) throws DDSURIException {
        String authority = DDSURI.createDomainDescriptorString(domainDescriptor);
        String schemeSpecificPart = authority == null ? DDSURI.createPathString(domainSpecificPart) : authority + DDSURI.createPathString(domainSpecificPart);
        String fragment = DDSURI.createFragmentString(domainSpecificPart);
        try {
            return DDSURI.getURIChecked(new URI(relative ? null : "dds", schemeSpecificPart, fragment));
        }
        catch (URISyntaxException use) {
            throw new DDSURIException(use);
        }
    }

    private static URI getURIChecked(URI uri) throws DDSURIException {
        String scheme;
        if (uri != null && (scheme = uri.getScheme()) != null && !"dds".equals(scheme)) {
            throw new DDSURIException("Invalid URI scheme: " + scheme);
        }
        return uri;
    }

    private static String getDomainSpecificPart(String domainSpecificPart, boolean isRelative) throws DDSURIException {
        String result = domainSpecificPart;
        if (isRelative) {
            if (result == null || "".equals(result.trim())) {
                throw new DDSURIException("Domain sppecific part cannot be empty for relative URIs");
            }
        } else {
            if (result == null) {
                result = "";
            }
            if (!"".equals(result) && !result.startsWith("/")) {
                result = "" + '/' + result;
            }
        }
        return result;
    }

    private static String getDomainSpecificPartFromURI(URI uri) throws DDSURIException {
        String domainSpecificPartString = StringUtils.isEmpty(uri.getAuthority()) ? uri.getSchemeSpecificPart() : uri.getPath();
        domainSpecificPartString = domainSpecificPartString + (uri.getFragment() == null ? "" : new StringBuilder().append("#").append(uri.getFragment()).toString());
        return DDSURI.getDomainSpecificPart(domainSpecificPartString, !uri.isAbsolute());
    }

    private static String createDomainDescriptorString(DomainDescriptor domainDescriptor) {
        StringBuilder result = new StringBuilder();
        Map<String, String> attributes = domainDescriptor.getAttributes();
        if (attributes != null) {
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
        return result.length() == 0 ? null : "//" + result.toString();
    }

    private static DomainDescriptor getDomainDescriptor(URI uri) throws DDSURIException {
        HashMap<String, String> attributes = new HashMap<String, String>();
        String authority = uri.getAuthority();
        if (!StringUtils.isEmpty(authority)) {
            StringTokenizer tokenizer = new StringTokenizer(authority, ";");
            while (tokenizer.hasMoreTokens()) {
                String token = tokenizer.nextToken();
                int equalsIndex = token.indexOf(61);
                if (equalsIndex == -1) continue;
                String attr = token.substring(0, equalsIndex);
                String value = token.substring(equalsIndex + 1);
                attributes.put(attr, value);
            }
        }
        return new DomainDescriptor(attributes);
    }

    private static String createPathString(String domainSpecificPart) {
        if (domainSpecificPart == null) {
            return null;
        }
        int fragmentIndex = domainSpecificPart.indexOf(35);
        if (fragmentIndex == -1) {
            return domainSpecificPart;
        }
        return domainSpecificPart.substring(0, fragmentIndex);
    }

    private static String createFragmentString(String domainSpecificPart) {
        if (domainSpecificPart == null) {
            return null;
        }
        int fragmentIndex = domainSpecificPart.indexOf(35);
        if (fragmentIndex == -1) {
            return null;
        }
        return domainSpecificPart.substring(fragmentIndex + 1);
    }

    private static final class DomainDescriptor {
        private final Map<String, String> attributes = new HashMap<String, String>();

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

