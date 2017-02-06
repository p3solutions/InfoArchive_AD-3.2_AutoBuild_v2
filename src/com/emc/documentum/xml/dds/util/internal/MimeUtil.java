/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.util.internal;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class MimeUtil {
    private static List<MimeEntry> mimeList = new ArrayList<MimeEntry>();

    private MimeUtil() {
    }

    public static String getMimeType(InputStream inputStream, long size) throws IOException {
        byte[] startBytes = new byte[8];
        Arrays.fill(startBytes, (byte) 0);
        inputStream.read(startBytes);
        for (MimeEntry entry : mimeList) {
            if (!entry.matchStart(startBytes) || !entry.matchEnd(null)) continue;
            return entry.getMimeType();
        }
        if (size > 0) {
            byte[] endBytes = new byte[8];
            Arrays.fill(endBytes, (byte) 0);
            inputStream.skip(size - 16);
            int readBytes = inputStream.read(endBytes);
            if (readBytes < 8) {
                int shift = 8 - readBytes;
                System.arraycopy(endBytes, 0, endBytes, shift, readBytes);
                System.arraycopy(startBytes, 8 - shift, endBytes, 0, shift);
            }
            for (MimeEntry entry2 : mimeList) {
                if (!entry2.matchStart(startBytes) || !entry2.matchEnd(endBytes)) continue;
                return entry2.getMimeType();
            }
        }
        return null;
    }

    static {
        try {
            mimeList.add(new MimeEntry("image/gif", "GIF89a"));
            mimeList.add(new MimeEntry("image/gif", "GIF87a"));
            mimeList.add(new MimeEntry("image/jpeg", new byte[]{-1, -40}, new byte[]{-1, -39}));
            mimeList.add(new MimeEntry("image/png", new byte[]{-119, 80, 78, 71, 13, 10, 26, 10}));
            mimeList.add(new MimeEntry("application/x-shockwave-flash", "CWS"));
            mimeList.add(new MimeEntry("application/x-shockwave-flash", "FWS"));
        }
        catch (UnsupportedEncodingException e) {
            // empty catch block
        }
    }

    private static class MimeEntry {
        private static String charset = "ISO-8859-1";
        private final String mime;
        private final byte[] startsWith;
        private byte[] endsWith;

        MimeEntry(String mime, byte[] startsWith, byte[] endsWith) {
            this.mime = mime;
            this.startsWith = startsWith;
            this.endsWith = endsWith;
        }

        MimeEntry(String mime, byte[] startsWith) {
            this.mime = mime;
            this.startsWith = startsWith;
        }

        MimeEntry(String mime, String startsWith) throws UnsupportedEncodingException {
            this(mime, startsWith.getBytes(charset));
        }

        MimeEntry(String mime, String startsWith, byte[] endsWith) throws UnsupportedEncodingException {
            this(mime, startsWith.getBytes(charset), endsWith);
        }

        MimeEntry(String mime, byte[] startsWith, String endsWith) throws UnsupportedEncodingException {
            this(mime, startsWith, endsWith.getBytes(charset));
        }

        MimeEntry(String mime, String startsWith, String endsWith) throws UnsupportedEncodingException {
            this(mime, startsWith.getBytes(charset), endsWith.getBytes(charset));
        }

        String getMimeType() {
            return this.mime;
        }

        boolean matchStart(byte[] bytes) {
            if (bytes == null) {
                return this.startsWith == null;
            }
            if (this.startsWith == null) {
                return true;
            }
            for (int i = 0; i < this.startsWith.length; ++i) {
                if (this.startsWith[i] == bytes[i]) continue;
                return false;
            }
            return true;
        }

        boolean matchEnd(byte[] bytes) {
            if (bytes == null) {
                return this.endsWith == null;
            }
            if (this.endsWith == null) {
                return true;
            }
            int diff = bytes.length - this.endsWith.length;
            for (int i = this.endsWith.length - 1; i >= 0; --i) {
                if (this.endsWith[i] == bytes[diff + i]) continue;
                return false;
            }
            return true;
        }
    }

}

