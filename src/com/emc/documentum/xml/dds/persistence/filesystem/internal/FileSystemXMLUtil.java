/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.persistence.filesystem.internal;

import com.emc.documentum.xml.dds.persistence.Container;
import com.emc.documentum.xml.dds.persistence.XMLNode;
import com.emc.documentum.xml.dds.persistence.content.XMLContentDescriptor;
import com.emc.documentum.xml.dds.persistence.exception.AmbiguousXPointerException;
import com.emc.documentum.xml.dds.persistence.exception.ContainerNotFoundException;
import com.emc.documentum.xml.dds.persistence.exception.DeadlockException;
import com.emc.documentum.xml.dds.persistence.exception.LocationNotFoundException;
import com.emc.documentum.xml.dds.persistence.exception.NodeNotFoundException;
import com.emc.documentum.xml.dds.persistence.exception.StoreSpecificException;
import com.emc.documentum.xml.dds.persistence.exception.TypeConflictException;
import com.emc.documentum.xml.dds.persistence.filesystem.internal.FileSystemContainer;
import com.emc.documentum.xml.dds.persistence.filesystem.internal.FileSystemContainerUtil;
import com.emc.documentum.xml.dds.persistence.internal.PersistenceUtil;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

public final class FileSystemXMLUtil {
    private FileSystemXMLUtil() {
    }

    public static Node retrieveNode(XMLNode xmlNode) throws NodeNotFoundException, AmbiguousXPointerException, StoreSpecificException, DeadlockException {
        try {
            if (xmlNode.asNode() != null) {
                return xmlNode.asNode();
            }
        }
        catch (ClassCastException cce) {
            throw new NodeNotFoundException(cce);
        }
        try {
            File documentFile = FileSystemContainerUtil.retrieveFile((FileSystemContainer)xmlNode.getContainer(), new XMLContentDescriptor());
            FileInputStream fis = new FileInputStream(documentFile);
            Node result = PersistenceUtil.retrieveXMLNode(new InputSource(fis), xmlNode);
            try {
                fis.close();
            }
            catch (IOException ioe) {
                // empty catch block
            }
            return result;
        }
        catch (LocationNotFoundException lnfe) {
            throw new NodeNotFoundException("Location could not be found : ", lnfe);
        }
        catch (FileNotFoundException fnfe) {
            throw new NodeNotFoundException("Container could not be found :", fnfe);
        }
        catch (ContainerNotFoundException cnfe) {
            throw new NodeNotFoundException("Container could not be found : :", cnfe);
        }
        catch (TypeConflictException tce) {
            throw new NodeNotFoundException("Container could not be found : :", tce);
        }
    }
}

