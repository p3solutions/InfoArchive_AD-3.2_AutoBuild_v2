/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.persistence.esu.internal;

import com.emc.documentum.xml.dds.DDS;
import com.emc.documentum.xml.dds.operation.exception.OperationException;
import com.emc.documentum.xml.dds.operation.framework.Operation;
import com.emc.documentum.xml.dds.operation.library.persistence.RetrieveOperation;
import com.emc.documentum.xml.dds.persistence.Container;
import com.emc.documentum.xml.dds.persistence.ContentDescriptor;
import com.emc.documentum.xml.dds.persistence.XMLNode;
import com.emc.documentum.xml.dds.persistence.content.XMLContentDescriptor;
import com.emc.documentum.xml.dds.persistence.data.InputStreamData;
import com.emc.documentum.xml.dds.persistence.exception.AmbiguousXPointerException;
import com.emc.documentum.xml.dds.persistence.exception.ContainerNotFoundException;
import com.emc.documentum.xml.dds.persistence.exception.DeadlockException;
import com.emc.documentum.xml.dds.persistence.exception.InvalidContentException;
import com.emc.documentum.xml.dds.persistence.exception.NodeNotFoundException;
import com.emc.documentum.xml.dds.persistence.exception.StoreSpecificException;
import com.emc.documentum.xml.dds.persistence.exception.TypeConflictException;
import com.emc.documentum.xml.dds.persistence.internal.PersistenceUtil;
import com.emc.documentum.xml.dds.user.User;
import java.io.InputStream;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

public final class ESUXMLUtil {
    private ESUXMLUtil() {
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
            InputStream is = ((InputStreamData)DDS.getApplication().execute(DDS.getApplication().getApplicationUser(), new RetrieveOperation(xmlNode.getContainer(), new XMLContentDescriptor()))).content();
            return PersistenceUtil.retrieveXMLNode(new InputSource(is), xmlNode);
        }
        catch (OperationException oe) {
            if (oe.getCause() instanceof StoreSpecificException) {
                throw (StoreSpecificException)oe.getCause();
            }
            if (oe.getCause() instanceof DeadlockException) {
                throw (DeadlockException)oe.getCause();
            }
            if (oe.getCause() instanceof ContainerNotFoundException) {
                throw new NodeNotFoundException("Container represented by XMLNode does not exist : ", oe.getCause());
            }
            if (oe.getCause() instanceof TypeConflictException) {
                throw new NodeNotFoundException(oe.getCause());
            }
            if (oe.getCause() instanceof InvalidContentException) {
                throw new DOMException((short) 12, "Container represented by XMLNode does not contain valid XML.");
            }
            throw new StoreSpecificException("A problem occurred retrieving the data :", oe);
        }
    }
}

