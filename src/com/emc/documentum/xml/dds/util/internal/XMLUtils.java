/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.util.internal;

import com.emc.documentum.xml.dds.util.internal.DistributedByteArray;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

public final class XMLUtils {
    private XMLUtils() {
    }

    public static InputStream getInputStream(String xml, String encoding) throws UnsupportedEncodingException {
        DistributedByteArray dba = new DistributedByteArray();
        OutputStream os = dba.getOutputStream();
        try {
            if (xml.startsWith("<?xml")) {
                int headerEnd = xml.indexOf(">") + 1;
                String header = xml.substring(0, headerEnd);
                if (header.contains("encoding")) {
                    int encodingStart = header.indexOf("encoding=\"") + "encoding=\"".length();
                    int encodingEnd = header.indexOf("\"", encodingStart);
                    os.write(header.substring(0, encodingStart).getBytes(encoding));
                    os.write(encoding.getBytes(encoding));
                    os.write(header.substring(encodingEnd).getBytes(encoding));
                    os.write(xml.substring(headerEnd).getBytes(encoding));
                } else {
                    os.write(header.getBytes(encoding));
                    os.write(xml.substring(headerEnd).getBytes(encoding));
                }
            } else {
                os.write(xml.getBytes(encoding));
            }
        }
        catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return dba.getInputStream();
    }
}

