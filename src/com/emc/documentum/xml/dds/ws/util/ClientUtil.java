/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.apache.http.Header
 *  org.apache.http.cookie.Cookie
 *  org.apache.http.cookie.CookieOrigin
 *  org.apache.http.cookie.MalformedCookieException
 *  org.apache.http.impl.cookie.BestMatchSpec
 *  org.apache.http.message.BasicHeader
 */
package com.emc.documentum.xml.dds.ws.util;

import com.emc.documentum.xml.dds.exception.DDSException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.ws.BindingProvider;
import org.apache.http.Header;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieOrigin;
import org.apache.http.cookie.MalformedCookieException;
import org.apache.http.impl.cookie.BestMatchSpec;
import org.apache.http.message.BasicHeader;

public final class ClientUtil {
    private static final String JSESSIONID = "JSESSIONID";
    private static final String SET_COOKIE = "Set-Cookie";
    private static final String COOKIE = "Cookie";

    public static String getToken(BindingProvider bindingProvider) throws DDSException {
        Map<String, Object> responseContext = bindingProvider.getResponseContext();
        if (responseContext != null) {
            List<String> cookieValues;
            Map headerValues = (Map)responseContext.get("javax.xml.ws.http.response.headers");
            if (headerValues != null && (cookieValues = (List<String>)headerValues.get("Set-Cookie")) != null) {
                BestMatchSpec cookieSpec = new BestMatchSpec();
                for (String cookieString : cookieValues) {
                    try {
                        List<Cookie> cookies = cookieSpec.parse((Header)new BasicHeader("Set-Cookie", cookieString), new CookieOrigin("dummy", 0, "dummy", false));
                        if (cookies == null) continue;
                        for (Cookie cookie : cookies) {
                            if (!"JSESSIONID".equalsIgnoreCase(cookie.getName())) continue;
                            return cookie.getValue();
                        }
                        continue;
                    }
                    catch (MalformedCookieException mfe) {
                        return null;
                    }
                }
            }
        } else {
            throw new DDSException("No response context found to get Token from");
        }
        return null;
    }

    public static void setToken(BindingProvider bindingProvider, String jSessionID) throws DDSException {
        Map<String, Object> requestContext = bindingProvider.getRequestContext();
        if (requestContext != null) {
            ArrayList<String> cookieValues;
            String cookieValue = "JSESSIONID=" + jSessionID;
            HashMap headerValues = (HashMap)requestContext.get("javax.xml.ws.http.request.headers");
            if (headerValues == null) {
                headerValues = new HashMap();
                requestContext.put("javax.xml.ws.http.request.headers", headerValues);
            }
            if ((cookieValues = (ArrayList<String>)headerValues.get("Cookie")) == null) {
                cookieValues = new ArrayList<String>();
                cookieValues.add(cookieValue);
                headerValues.put("Cookie", cookieValues);
            } else {
                BestMatchSpec cookieSpec = new BestMatchSpec();
                boolean found = false;
                int i = 0;
                for (String cookieString : cookieValues) {
                    try {
                        List<Cookie> cookies = cookieSpec.parse((Header)new BasicHeader("Cookie", cookieString), new CookieOrigin("dummy", 0, "dummy", false));
                        if (cookies != null) {
                            for (Cookie cookie : cookies) {
                                if (!"JSESSIONID".equalsIgnoreCase(cookie.getName())) continue;
                                cookieValues.set(i, cookieValue);
                                found = true;
                            }
                        }
                    }
                    catch (MalformedCookieException mfe) {
                        throw new DDSException((Throwable)mfe);
                    }
                    ++i;
                }
                if (!found) {
                    cookieValues.add(cookieValue);
                }
            }
        } else {
            throw new DDSException("No request context found to set Token on");
        }
    }

    private ClientUtil() {
    }
}

