/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.persistence.filesystem.internal;

import com.emc.documentum.xml.dds.logging.LogCenter;
import com.emc.documentum.xml.dds.persistence.Location;
import com.emc.documentum.xml.dds.persistence.exception.DeadlockException;
import com.emc.documentum.xml.dds.persistence.exception.LocationNotFoundException;
import com.emc.documentum.xml.dds.persistence.exception.StoreSpecificException;
import com.emc.documentum.xml.dds.persistence.exception.TypeConflictException;
import com.emc.documentum.xml.dds.persistence.filesystem.internal.FileSystemLocation;
import java.io.File;
import java.util.List;

public final class FileSystemLocationUtil {
    private FileSystemLocationUtil() {
    }

    public static File retrieveDirectory(FileSystemLocation directoryLocation, boolean create, boolean createPath, boolean checkOptions) throws LocationNotFoundException, StoreSpecificException, DeadlockException {
        File directory = new File(directoryLocation.getPath(true));
        if (directory.isDirectory()) {
            if (checkOptions) {
                // empty if block
            }
            return directory;
        }
        if (directory.isFile()) {
            throw new LocationNotFoundException("File exists with the same name : " + directoryLocation.getPath(false));
        }
        if (!create) {
            throw new LocationNotFoundException("Path does not exist : " + directoryLocation.getPath(false));
        }
        File parentDirectory = FileSystemLocationUtil.retrieveDirectory((FileSystemLocation)directoryLocation.getParent(), createPath, createPath, false);
        if (parentDirectory != null || createPath || directoryLocation.getPathComponents().size() == 1) {
            LogCenter.debug("FileSystemLocationUtil : Creating Directory : " + directoryLocation.getPath(false));
            try {
                return FileSystemLocationUtil.createDirectory(directoryLocation);
            }
            catch (TypeConflictException tce) {
                throw new LocationNotFoundException("Path does not exist : " + directoryLocation.getPath(false));
            }
        }
        throw new LocationNotFoundException("Path does not exist : " + directoryLocation.getPath(false));
    }

    public static File createDirectory(FileSystemLocation directoryLocation) throws TypeConflictException, StoreSpecificException, DeadlockException {
        File directory = new File(directoryLocation.getPath(true));
        if (directory.isDirectory()) {
            return directory;
        }
        if (directory.isFile()) {
            throw new TypeConflictException("File exists with the same name : " + directoryLocation.getPath(false));
        }
        directory.mkdirs();
        return directory;
    }

    public static void createLocation(Location child, boolean createPath) throws LocationNotFoundException, TypeConflictException {
        Location parent = child.getParent();
        if (parent != null) {
            File parentFile = new File(parent.getPath());
            if (parentFile.exists() && !parentFile.isDirectory()) {
                throw new TypeConflictException("Create Location failed : Path contains a Container : " + parent.getPath());
            }
            if (!parentFile.exists()) {
                if (!createPath) {
                    throw new LocationNotFoundException("Create Location failed : Path does not exist : " + parent.getPath());
                }
                FileSystemLocationUtil.createLocation(parent, createPath);
            }
        }
        File childFile = new File(child.getPath());
        childFile.mkdirs();
    }

    public static void deleteFully(File file) throws StoreSpecificException {
        if (file.exists()) {
            int retries = 0;
            boolean success = false;
            while (!success && retries < 50) {
                if (file.isDirectory()) {
                    for (File child : file.listFiles()) {
                        FileSystemLocationUtil.deleteHelper(child);
                    }
                }
                success = file.delete();
                ++retries;
                if (success) continue;
                try {
                    Thread.sleep(25);
                }
                catch (InterruptedException ie) {}
            }
            if (!success) {
                throw new StoreSpecificException("FileSystem issue : file could not be deleted : " + file.getAbsolutePath());
            }
        }
    }

    private static void deleteHelper(File file) {
        if (file.isDirectory()) {
            for (File child : file.listFiles()) {
                FileSystemLocationUtil.deleteHelper(child);
            }
        }
        file.delete();
    }
}

