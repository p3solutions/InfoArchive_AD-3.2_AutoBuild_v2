/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public final class PathResolver {
    public static InputStream getInputStream(String path) throws IOException {
        URL url = Thread.currentThread().getContextClassLoader().getResource(path);
        if (url != null) {
            return url.openStream();
        }
        File file = new File(path);
        if (!file.exists()) {
            String fileName = path.substring(path.lastIndexOf("/") + 1);
            url = Thread.currentThread().getContextClassLoader().getResource(fileName);
            if (url != null) {
                return url.openStream();
            }
        }
        return new FileInputStream(file);
    }

    private PathResolver() {
    }
}

