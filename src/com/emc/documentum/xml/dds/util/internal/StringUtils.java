/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.apache.commons.codec.binary.Base64
 */
package com.emc.documentum.xml.dds.util.internal;

import com.emc.documentum.xml.dds.util.internal.Cryptographer;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.codec.binary.Base64;

public final class StringUtils {
    public static boolean isEmpty(String str) {
        if (str == null || str.length() == 0) {
            return true;
        }
        return str.trim().length() == 0;
    }

    public static List<String> stringToList(String str, String regex, boolean trim) {
        if (str == null || regex == null) {
            throw new IllegalArgumentException();
        }
        String[] values = str.split(regex);
        ArrayList<String> result = new ArrayList<String>(values.length);
        for (String value : values) {
            String newValue;
            if (value == null) continue;
            String string = newValue = trim ? value.trim() : value;
            if (StringUtils.isEmpty(newValue)) continue;
            result.add(newValue);
        }
        return result;
    }

    public static String encrypt(String bareString) {
        try {
            return new String(Base64.encodeBase64((byte[])Cryptographer.encrypt(bareString.getBytes("ISO-8859-1"))), "ISO-8859-1");
        }
        catch (UnsupportedEncodingException uee) {
            return null;
        }
    }

    public static String decrypt(String encryptedString) {
        try {
            byte[] bytes = Base64.decodeBase64((byte[])encryptedString.getBytes("ISO-8859-1"));
            byte[] decrypted = Cryptographer.decrypt(bytes);
            return decrypted == null ? null : new String(decrypted, "ISO-8859-1");
        }
        catch (UnsupportedEncodingException uee) {
            return null;
        }
    }

    private StringUtils() {
    }
}

