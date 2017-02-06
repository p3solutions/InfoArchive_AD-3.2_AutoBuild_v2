/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.util.internal;

public final class FilenameUtils {
    private static final char EXTENSION_SEPARATOR = '.';
    private static final char UNIX_SEPARATOR = '/';
    private static final char WINDOWS_SEPARATOR = '\\';
    private static final char SYSTEM_SEPARATOR = '/';
    private static final char OTHER_SEPARATOR = 92;

    private FilenameUtils() {
    }

    private static boolean isSeparator(char ch) {
        return ch == '/' || ch == '\\';
    }

    public static String normalize(String filename) {
        return FilenameUtils.doNormalize(filename, true);
    }

    public static String normalizeNoEndSeparator(String filename) {
        return FilenameUtils.doNormalize(filename, false);
    }

    private static String doNormalize(String filename, boolean keepSeparator) {
        if (filename == null) {
            return null;
        }
        int size = filename.length();
        if (size == 0) {
            return filename;
        }
        int prefix = FilenameUtils.getPrefixLength(filename);
        if (prefix < 0) {
            return null;
        }
        char[] array = new char[size + 2];
        filename.getChars(0, filename.length(), array, 0);
        for (int i = 0; i < array.length; ++i) {
            if (array[i] != OTHER_SEPARATOR) continue;
            array[i] = 47;
        }
        boolean lastIsDirectory = true;
        if (array[size - 1] != '/') {
            array[size++] = 47;
            lastIsDirectory = false;
        }
        int i2 = prefix + 1;
        while (i2 < size) {
            if (array[i2] == '/' && array[i2 - 1] == '/') {
                System.arraycopy(array, i2, array, i2 - 1, size - i2);
                --size;
                continue;
            }
            ++i2;
        }
        i2 = prefix + 1;
        while (i2 < size) {
            if (array[i2] == '/' && array[i2 - 1] == '.' && (i2 == prefix + 1 || array[i2 - 2] == '/')) {
                if (i2 == size - 1) {
                    lastIsDirectory = true;
                }
                System.arraycopy(array, i2 + 1, array, i2 - 1, size - i2);
                size -= 2;
                continue;
            }
            ++i2;
        }
        i2 = prefix + 2;
        block3 : while (i2 < size) {
            if (array[i2] == '/' && array[i2 - 1] == '.' && array[i2 - 2] == '.' && (i2 == prefix + 2 || array[i2 - 3] == '/')) {
                if (i2 == prefix + 2) {
                    return null;
                }
                if (i2 == size - 1) {
                    lastIsDirectory = true;
                }
                for (int j = i2 - 4; j >= prefix; --j) {
                    if (array[j] != '/') continue;
                    System.arraycopy(array, i2 + 1, array, j + 1, size - i2);
                    size -= i2 - j;
                    i2 = j + 1;
                    continue block3;
                }
                System.arraycopy(array, i2 + 1, array, prefix, size - i2);
                size -= i2 + 1 - prefix;
                i2 = prefix + 1;
            }
            ++i2;
        }
        if (size <= 0) {
            return "";
        }
        if (size <= prefix) {
            return new String(array, 0, size);
        }
        if (lastIsDirectory && keepSeparator) {
            return new String(array, 0, size);
        }
        return new String(array, 0, size - 1);
    }

    public static String concat(String basePath, String fullFilenameToAdd) {
        int prefix = FilenameUtils.getPrefixLength(fullFilenameToAdd);
        if (prefix < 0) {
            return null;
        }
        if (prefix > 0) {
            return FilenameUtils.normalize(fullFilenameToAdd);
        }
        if (basePath == null) {
            return null;
        }
        int len = basePath.length();
        if (len == 0) {
            return FilenameUtils.normalize(fullFilenameToAdd);
        }
        char ch = basePath.charAt(len - 1);
        if (FilenameUtils.isSeparator(ch)) {
            return FilenameUtils.normalize(basePath + fullFilenameToAdd);
        }
        return FilenameUtils.normalize(basePath + '/' + fullFilenameToAdd);
    }

    public static int getPrefixLength(String filename) {
        if (filename == null) {
            return -1;
        }
        int len = filename.length();
        if (len == 0) {
            return 0;
        }
        char ch0 = filename.charAt(0);
        if (ch0 == ':') {
            return -1;
        }
        if (len == 1) {
            if (ch0 == '~') {
                return 2;
            }
            return FilenameUtils.isSeparator(ch0) ? 1 : 0;
        }
        if (ch0 == '~') {
            int posUnix = filename.indexOf(47, 1);
            int posWin = filename.indexOf(92, 1);
            if (posUnix == -1 && posWin == -1) {
                return len + 1;
            }
            posUnix = posUnix == -1 ? posWin : posUnix;
            posWin = posWin == -1 ? posUnix : posWin;
            return Math.min(posUnix, posWin) + 1;
        }
        char ch1 = filename.charAt(1);
        if (ch1 == ':') {
            if ((ch0 = Character.toUpperCase(ch0)) >= 'A' && ch0 <= 'Z') {
                if (len == 2 || !FilenameUtils.isSeparator(filename.charAt(2))) {
                    return 2;
                }
                return 3;
            }
            return -1;
        }
        if (FilenameUtils.isSeparator(ch0) && FilenameUtils.isSeparator(ch1)) {
            int posUnix = filename.indexOf(47, 2);
            int posWin = filename.indexOf(92, 2);
            if (posUnix == -1 && posWin == -1 || posUnix == 2 || posWin == 2) {
                return -1;
            }
            posUnix = posUnix == -1 ? posWin : posUnix;
            posWin = posWin == -1 ? posUnix : posWin;
            return Math.min(posUnix, posWin) + 1;
        }
        return FilenameUtils.isSeparator(ch0) ? 1 : 0;
    }

    public static int indexOfLastSeparator(String filename) {
        if (filename == null) {
            return -1;
        }
        int lastUnixPos = filename.lastIndexOf(47);
        int lastWindowsPos = filename.lastIndexOf(92);
        return Math.max(lastUnixPos, lastWindowsPos);
    }

    public static int indexOfExtension(String filename) {
        if (filename == null) {
            return -1;
        }
        int extensionPos = filename.lastIndexOf(46);
        int lastSeparator = FilenameUtils.indexOfLastSeparator(filename);
        return lastSeparator > extensionPos ? -1 : extensionPos;
    }

    public static String getPrefix(String filename) {
        if (filename == null) {
            return null;
        }
        int len = FilenameUtils.getPrefixLength(filename);
        if (len < 0) {
            return null;
        }
        if (len > filename.length()) {
            return filename + '/';
        }
        return filename.substring(0, len);
    }

    public static String getPathNoEndSeparator(String filename) {
        return FilenameUtils.doGetPath(filename, 0);
    }

    private static String doGetPath(String filename, int separatorAdd) {
        if (filename == null) {
            return null;
        }
        int prefix = FilenameUtils.getPrefixLength(filename);
        if (prefix < 0) {
            return null;
        }
        int index = FilenameUtils.indexOfLastSeparator(filename);
        if (prefix >= filename.length() || index < 0) {
            return "";
        }
        return filename.substring(prefix, index + separatorAdd);
    }

    public static String getFullPath(String filename) {
        return FilenameUtils.doGetFullPath(filename, true);
    }

    public static String getFullPathNoEndSeparator(String filename) {
        return FilenameUtils.doGetFullPath(filename, false);
    }

    private static String doGetFullPath(String filename, boolean includeSeparator) {
        if (filename == null) {
            return null;
        }
        int prefix = FilenameUtils.getPrefixLength(filename);
        if (prefix < 0) {
            return null;
        }
        if (prefix >= filename.length()) {
            if (includeSeparator) {
                return FilenameUtils.getPrefix(filename);
            }
            return filename;
        }
        int index = FilenameUtils.indexOfLastSeparator(filename);
        if (index < 0) {
            return filename.substring(0, prefix);
        }
        int end = index + (includeSeparator ? 1 : 0);
        return filename.substring(0, end);
    }

    public static String getName(String filename) {
        if (filename == null) {
            return null;
        }
        int index = FilenameUtils.indexOfLastSeparator(filename);
        return filename.substring(index + 1);
    }

    public static String getBaseName(String filename) {
        return FilenameUtils.removeExtension(FilenameUtils.getName(filename));
    }

    public static String getExtension(String filename) {
        if (filename == null) {
            return null;
        }
        int index = FilenameUtils.indexOfExtension(filename);
        if (index == -1) {
            return "";
        }
        return filename.substring(index + 1);
    }

    public static String removeExtension(String filename) {
        if (filename == null) {
            return null;
        }
        int index = FilenameUtils.indexOfExtension(filename);
        if (index == -1) {
            return filename;
        }
        return filename.substring(0, index);
    }
}

