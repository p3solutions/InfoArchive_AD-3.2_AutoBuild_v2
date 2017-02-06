/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.util.internal;

import com.emc.documentum.xml.dds.util.internal.StringUtils;
import java.net.URI;
import java.net.URISyntaxException;

public final class URIUtils {
    private static final String SCHEME_FILE = "file";

    private URIUtils() {
    }

    public static String resolve(String uri, String baseURI) throws URISyntaxException {
        URI uResolvedURI = URIUtils.resolveURI(uri, baseURI);
        return URIUtils.toEncodedString(uResolvedURI);
    }

    public static URI resolveURI(String uri, String baseURI) throws URISyntaxException {
        URI uBaseURI = URIUtils.getSafeURI(baseURI);
        URI uURI = URIUtils.getSafeURI(uri);
        if (uURI == null) {
            return uBaseURI;
        }
        if (uBaseURI == null) {
            return uURI;
        }
        return uBaseURI.resolve(uURI);
    }

    public static String normalizeURI(String uri) throws URISyntaxException {
        if (uri == null) {
            throw new IllegalArgumentException("<null> uri");
        }
        URI uURI = new URI(URIUtils.preprocessURI(uri));
        URI uNormalizedURI = uURI.normalize();
        return URIUtils.fixFileURI(uNormalizedURI.toString());
    }

    private static String toEncodedString(URI uri) {
        if (uri == null) {
            return null;
        }
        return uri.toASCIIString();
    }

    private static URI getSafeURI(String uri) throws URISyntaxException {
        URI uNewURI;
        if (StringUtils.isEmpty(uri)) {
            return null;
        }
        String newURI = uri.replaceAll("\\\\", "/");
        try {
            uNewURI = new URI(newURI);
        }
        catch (URISyntaxException e) {
            String ssp;
            String fragment;
            int colonIndex = newURI.indexOf(58);
            String scheme = colonIndex == -1 ? null : newURI.substring(0, colonIndex);
            if (colonIndex < newURI.length() - 1) {
                int fragmentIndex = newURI.indexOf("#", colonIndex);
                if (fragmentIndex == -1) {
                    ssp = newURI.substring(colonIndex + 1);
                    fragment = null;
                } else {
                    ssp = newURI.substring(colonIndex + 1, fragmentIndex);
                    fragment = fragmentIndex < newURI.length() - 1 ? newURI.substring(fragmentIndex + 1) : null;
                }
            } else {
                ssp = "";
                fragment = null;
            }
            uNewURI = new URI(scheme, ssp, fragment);
        }
        if (uNewURI != null) {
            String authority = uNewURI.getAuthority();
            String path = uNewURI.getRawPath();
            String query = uNewURI.getRawFragment();
            String fragment = uNewURI.getRawFragment();
            if (!StringUtils.isEmpty(authority) && StringUtils.isEmpty(path)) {
                uNewURI = new URI(uNewURI.getScheme(), uNewURI.getRawAuthority(), "/", query, fragment);
            }
        }
        return uNewURI;
    }

    private static String preprocessURI(String uri) {
        if (uri == null) {
            return null;
        }
        String newURI = uri.replaceAll("\\\\", "/");
        newURI = uri.replaceAll(" ", "%20");
        return newURI;
    }

    private static String fixFileURI(String uri) {
        if (uri == null) {
            return null;
        }
        String schemeFileColon = "file:";
        if (uri.startsWith("file:")) {
            int pathIndex;
            int index = pathIndex = "file:".length();
            int length = uri.length();
            int slashes = 0;
            while (index < length && uri.charAt(index++) == '/') {
                ++slashes;
            }
            if (slashes == 0) {
                return "file://" + uri.substring(pathIndex);
            }
            if (slashes == 1) {
                return "file:/" + uri.substring(pathIndex);
            }
            return uri;
        }
        return uri;
    }
}

