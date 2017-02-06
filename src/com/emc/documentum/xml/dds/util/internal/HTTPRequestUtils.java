/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.commons.fileupload.FileItemIterator
 *  org.apache.commons.fileupload.FileItemStream
 *  org.apache.commons.fileupload.FileUploadException
 *  org.apache.commons.fileupload.servlet.ServletFileUpload
 */
package com.emc.documentum.xml.dds.util.internal;

import com.emc.documentum.xml.dds.util.internal.DistributedByteArray;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

public final class HTTPRequestUtils {
    private HTTPRequestUtils() {
    }

    public static ParameterCache getParameterCache(HttpServletRequest request) throws IOException {
        ParameterCache cache = new ParameterCache();
        Enumeration parameters = request.getParameterNames();
        while (parameters.hasMoreElements()) {
            String param = (String)parameters.nextElement();
            String value = request.getParameter(param);
            cache.putParameterValue(param, value);
        }
        if (ServletFileUpload.isMultipartContent((HttpServletRequest)request)) {
            ServletFileUpload upload = new ServletFileUpload();
            try {
                FileItemIterator iter = upload.getItemIterator(request);
                while (iter.hasNext()) {
                    FileItemStream item = iter.next();
                    String fieldName = item.getFieldName();
                    InputStream is = item.openStream();
                    byte[] buffer = new byte[1024];
                    DistributedByteArray dba = new DistributedByteArray();
                    int len = 1;
                    while (len != -1) {
                        len = is.read(buffer, 0, 1024);
                        if (len <= 0) continue;
                        dba.getOutputStream().write(buffer, 0, len);
                    }
                    cache.putParameterValue(fieldName, dba);
                    is.close();
                }
            }
            catch (FileUploadException fue) {
                fue.printStackTrace();
                IOException ioe = new IOException();
                ioe.initCause((Throwable)fue);
                throw ioe;
            }
        }
        return cache;
    }

    public static class ParameterValue {
        private final DistributedByteArray dba;
        private final String value;

        public ParameterValue(String value) {
            this.value = value;
            this.dba = null;
        }

        public ParameterValue(DistributedByteArray dba) {
            this.dba = dba;
            this.value = null;
        }

        public InputStream getInputStream() {
            return this.dba.getInputStream();
        }

        public String getValue() {
            return this.value;
        }

        public boolean isStreamBased() {
            return this.dba != null;
        }
    }

    public static class ParameterCache {
        private final Map<String, ParameterValue> cache = new HashMap<String, ParameterValue>();

        public void putParameterValue(String param, String value) {
            this.cache.put(param, new ParameterValue(value));
        }

        public void putParameterValue(String param, DistributedByteArray dba) {
            this.cache.put(param, new ParameterValue(dba));
        }

        public Iterator<String> getParameters() {
            return this.cache.keySet().iterator();
        }

        public ParameterValue getParameterValue(String param) {
            return this.cache.get(param);
        }
    }

}

