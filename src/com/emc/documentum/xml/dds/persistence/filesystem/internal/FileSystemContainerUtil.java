/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.persistence.filesystem.internal;

import com.emc.documentum.xml.dds.logging.LogCenter;
import com.emc.documentum.xml.dds.persistence.ContentDescriptor;
import com.emc.documentum.xml.dds.persistence.Data;
import com.emc.documentum.xml.dds.persistence.Location;
import com.emc.documentum.xml.dds.persistence.data.ByteArrayData;
import com.emc.documentum.xml.dds.persistence.data.InputStreamData;
import com.emc.documentum.xml.dds.persistence.data.StringData;
import com.emc.documentum.xml.dds.persistence.exception.ContainerAlreadyExistsException;
import com.emc.documentum.xml.dds.persistence.exception.ContainerNotFoundException;
import com.emc.documentum.xml.dds.persistence.exception.DeadlockException;
import com.emc.documentum.xml.dds.persistence.exception.LocationNotFoundException;
import com.emc.documentum.xml.dds.persistence.exception.StoreSpecificException;
import com.emc.documentum.xml.dds.persistence.exception.TypeConflictException;
import com.emc.documentum.xml.dds.persistence.filesystem.internal.FileSystemContainer;
import com.emc.documentum.xml.dds.persistence.filesystem.internal.FileSystemLocation;
import com.emc.documentum.xml.dds.persistence.filesystem.internal.FileSystemLocationUtil;
import com.emc.documentum.xml.dds.util.internal.XMLUtils;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public final class FileSystemContainerUtil {
    private FileSystemContainerUtil() {
    }

    public static File retrieveFile(FileSystemContainer container, ContentDescriptor content) throws LocationNotFoundException, ContainerNotFoundException, TypeConflictException, StoreSpecificException, DeadlockException {
        try {
            return FileSystemContainerUtil.retrieveFile(container, false, false, false);
        }
        catch (ContainerAlreadyExistsException caee) {
            throw new StoreSpecificException(caee);
        }
    }

    public static File retrieveFile(FileSystemContainer container, boolean createPath, boolean create, boolean replace) throws LocationNotFoundException, ContainerNotFoundException, ContainerAlreadyExistsException, TypeConflictException, StoreSpecificException, DeadlockException {
        File directory = FileSystemLocationUtil.retrieveDirectory((FileSystemLocation)container.getLocation(), createPath && create, createPath && create, false);
        if (directory == null) {
            throw new LocationNotFoundException("FileSystemContainerUtil : Parent location does not exist : " + container.getLocation().getPath());
        }
        File file = new File(container.getPath());
        if (!file.exists()) {
            if (create) {
                try {
                    file.createNewFile();
                    return file;
                }
                catch (IOException ioe) {
                    LogCenter.error("FileSystemContainerUtil : Problem trying to create file at path : " + file.getAbsolutePath());
                    throw new StoreSpecificException(ioe);
                }
            }
            throw new ContainerNotFoundException("Could not retrieve Container : " + container.getPath());
        }
        if (file.isDirectory()) {
            throw new TypeConflictException("A directory already exists with the same name : " + container.getPath());
        }
        if (create) {
            if (!replace) {
                throw new ContainerAlreadyExistsException("Container already exists : " + container.getPath());
            }
            file.delete();
            try {
                file.createNewFile();
            }
            catch (IOException ioe) {
                throw new StoreSpecificException(ioe);
            }
        }
        return file;
    }

    public static File storeData(FileSystemContainer container, Data<?> data, boolean replace) throws LocationNotFoundException, ContainerAlreadyExistsException, TypeConflictException, StoreSpecificException, DeadlockException {
        LogCenter.debug("FileSystemContainerUtil : Storing data in : " + container.getPath());
        try {
            File file = FileSystemContainerUtil.retrieveFile(container, false, true, replace);
            FileSystemContainerUtil.writeFile(file, data);
            return file;
        }
        catch (ContainerNotFoundException cnfe) {
            throw new StoreSpecificException("Internal error.");
        }
        catch (IOException ioe) {
            throw new StoreSpecificException(ioe);
        }
    }

    private static void writeFile(File file, Data<?> data) throws IOException {
        if (data instanceof StringData) {
            FileOutputStream fos = new FileOutputStream(file);
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            BufferedInputStream bis = new BufferedInputStream(XMLUtils.getInputStream(((StringData)data).content(), "UTF-8"));
            long offset = 0;
            byte[] buffer = new byte[16348];
            while (offset != -1) {
                int read = bis.read(buffer);
                if (read > 0) {
                    bos.write(buffer, 0, read);
                }
                offset = read == -1 ? -1 : offset + (long)read;
            }
            bos.flush();
            bos.close();
            bis.close();
        } else if (data instanceof ByteArrayData) {
            FileOutputStream fos = new FileOutputStream(file);
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            bos.write(((ByteArrayData)data).content());
            bos.flush();
            bos.close();
        } else if (data instanceof InputStreamData) {
            FileOutputStream fos = new FileOutputStream(file);
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            BufferedInputStream bis = new BufferedInputStream(((InputStreamData)data).content());
            long offset = 0;
            byte[] buffer = new byte[16348];
            while (offset != -1) {
                int read = bis.read(buffer);
                if (read > 0) {
                    bos.write(buffer, 0, read);
                }
                offset = read == -1 ? -1 : offset + (long)read;
            }
            bos.flush();
            bos.close();
            bis.close();
        } else {
            LogCenter.error("FileSystemContainerUtil : Trying to write unknown type of Data to : " + file.getAbsolutePath());
        }
    }
}

