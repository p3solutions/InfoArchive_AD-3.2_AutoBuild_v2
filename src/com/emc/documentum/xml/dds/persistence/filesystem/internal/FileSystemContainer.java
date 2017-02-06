/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.apache.xerces.parsers.DOMParser
 */
package com.emc.documentum.xml.dds.persistence.filesystem.internal;

import com.emc.documentum.xml.dds.persistence.Container;
import com.emc.documentum.xml.dds.persistence.ContentDescriptor;
import com.emc.documentum.xml.dds.persistence.Data;
import com.emc.documentum.xml.dds.persistence.Location;
import com.emc.documentum.xml.dds.persistence.Metadata;
import com.emc.documentum.xml.dds.persistence.MetadataScheme;
import com.emc.documentum.xml.dds.persistence.Session;
import com.emc.documentum.xml.dds.persistence.StoreType;
import com.emc.documentum.xml.dds.persistence.XMLNode;
import com.emc.documentum.xml.dds.persistence.content.ObjectContentDescriptor;
import com.emc.documentum.xml.dds.persistence.content.XMLContentDescriptor;
import com.emc.documentum.xml.dds.persistence.data.AbstractData;
import com.emc.documentum.xml.dds.persistence.data.InputStreamData;
import com.emc.documentum.xml.dds.persistence.data.ObjectData;
import com.emc.documentum.xml.dds.persistence.data.StringData;
import com.emc.documentum.xml.dds.persistence.exception.ContainerAlreadyExistsException;
import com.emc.documentum.xml.dds.persistence.exception.ContainerNotFoundException;
import com.emc.documentum.xml.dds.persistence.exception.DeadlockException;
import com.emc.documentum.xml.dds.persistence.exception.IllegalActionException;
import com.emc.documentum.xml.dds.persistence.exception.InvalidContentException;
import com.emc.documentum.xml.dds.persistence.exception.LocationNotFoundException;
import com.emc.documentum.xml.dds.persistence.exception.SchemeNotSupportedException;
import com.emc.documentum.xml.dds.persistence.exception.StoreSpecificException;
import com.emc.documentum.xml.dds.persistence.exception.TypeConflictException;
import com.emc.documentum.xml.dds.persistence.filesystem.internal.FileSystemContainerUtil;
import com.emc.documentum.xml.dds.persistence.filesystem.internal.FileSystemLocationUtil;
import com.emc.documentum.xml.dds.persistence.filesystem.internal.FileSystemXMLNode;
import com.emc.documentum.xml.dds.persistence.internal.AbstractContainer;
import com.emc.documentum.xml.dds.persistence.internal.PersistenceUtil;
import com.emc.documentum.xml.dds.persistence.metadata.DocumentumMetadata;
import com.emc.documentum.xml.dds.serialization.Serializer;
import com.emc.documentum.xml.dds.serialization.exception.DeserializationException;
import com.emc.documentum.xml.dds.serialization.exception.SerializationException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.xerces.parsers.DOMParser;
import org.xml.sax.InputSource;

public class FileSystemContainer
extends AbstractContainer {
    protected FileSystemContainer(Location location, String name) {
        super(location, name);
    }

    @Override
    public StoreType getStoreType() {
        return StoreType.FILESYSTEM;
    }

    @Override
    public XMLNode asXMLNode() {
        return new FileSystemXMLNode(this);
    }

    @Override
    public XMLNode getXMLNode(String xpointer) {
        return new FileSystemXMLNode(this, xpointer);
    }

    @Override
    public boolean exists(Session session) throws StoreSpecificException, DeadlockException {
        File file = new File(this.getPath());
        return file.exists() && !file.isDirectory();
    }

    @Override
    public void delete(Session session) throws ContainerNotFoundException, StoreSpecificException, DeadlockException, IllegalActionException {
        File file = new File(this.getPath());
        if (!file.exists()) {
            throw new ContainerNotFoundException("Delete Container failed : Container does not exist : " + this.getPath());
        }
        if (file.isDirectory()) {
            throw new ContainerNotFoundException("Delete Container failed : A directory already exists with the same name : " + this.getPath());
        }
        FileSystemLocationUtil.deleteFully(file);
    }

    @Override
    public void persist(Session session, ContentDescriptor contentDescriptor, Data<?> data, boolean replace) throws LocationNotFoundException, ContainerAlreadyExistsException, TypeConflictException, SerializationException, StoreSpecificException, DeadlockException {
        if (contentDescriptor instanceof ObjectContentDescriptor) {
            StringData transformedData = new StringData(((ObjectContentDescriptor)contentDescriptor).getSerializer().serialize(((ObjectData)data).content()));
            FileSystemContainerUtil.storeData(this, transformedData, replace);
        } else {
            FileSystemContainerUtil.storeData(this, data, replace);
        }
    }

    @Override
    public Data<?> retrieve(Session session, ContentDescriptor contentDescriptor) throws ContainerNotFoundException, InvalidContentException, TypeConflictException, StoreSpecificException, DeadlockException {
        File file = new File(this.getPath());
        if (file.isDirectory()) {
            throw new TypeConflictException("The Container points to a directory instead of a file : " + this.getPath());
        }
        if (!file.exists()) {
            throw new ContainerNotFoundException("The Container could not be found : " + this.getPath());
        }
        try {
            if (contentDescriptor == null) {
                return this.getTypedData(new InputStreamData(PersistenceUtil.getInputStreamCached(new FileInputStream(file))), "application/octet-stream");
            }
            if (contentDescriptor instanceof XMLContentDescriptor) {
                try {
                    DOMParser parser = new DOMParser();
                    FileInputStream fis = new FileInputStream(FileSystemContainerUtil.retrieveFile(this, false, false, false));
                    parser.parse(new InputSource(fis));
                    fis.close();
                }
                catch (Exception e) {
                    throw new InvalidContentException("Retrieve failed : XML content was expected but parse failed.", e);
                }
                return this.getTypedData(new InputStreamData(PersistenceUtil.getInputStreamCached(new FileInputStream(file))), "application/xml");
            }
            if (contentDescriptor instanceof ObjectContentDescriptor) {
                return this.getTypedData(new ObjectData(((ObjectContentDescriptor)contentDescriptor).getSerializer().deserialize(new FileInputStream(file))), "application/java-serialized-object");
            }
            return this.getTypedData(new InputStreamData(PersistenceUtil.getInputStreamCached(new FileInputStream(file))), "application/octet-stream");
        }
        catch (DeserializationException de) {
            throw new InvalidContentException("The content could not be deserialized properly :", de);
        }
        catch (IOException ioe) {
            throw new StoreSpecificException(ioe);
        }
    }

    private Data<?> getTypedData(AbstractData<?> data, String mimeType) {
        data.setMimeType(mimeType);
        return data;
    }

    @Override
    public Metadata getMetadata(Session session, MetadataScheme scheme) throws SchemeNotSupportedException, ContainerNotFoundException, StoreSpecificException, DeadlockException {
        switch (scheme) {
            case XDB: {
                throw new SchemeNotSupportedException("The XDB Metadata Scheme is only supported in XDB Stores.");
            }
            case DOCUMENTUM: {
                return PersistenceUtil.getDocumentumMetadata(session, this);
            }
        }
        return null;
    }

    @Override
    public void setMetadata(Session session, Metadata metadata) throws SchemeNotSupportedException, ContainerNotFoundException, TypeConflictException, StoreSpecificException, DeadlockException {
        switch (metadata.getScheme()) {
            case XDB: {
                throw new SchemeNotSupportedException("The XDB Metadata Scheme is only supported in XDB Stores.");
            }
            case DOCUMENTUM: {
                PersistenceUtil.setDocumentumMetadata(session, this, (DocumentumMetadata)metadata);
                return;
            }
        }
    }

    @Override
    public void move(Session session, Container target, boolean replace) throws LocationNotFoundException, ContainerNotFoundException, ContainerAlreadyExistsException, TypeConflictException, IllegalActionException, StoreSpecificException, DeadlockException {
        PersistenceUtil.move(session, this, session, target, replace);
    }

    @Override
    public void copy(Session session, Container target, boolean replace) throws LocationNotFoundException, ContainerNotFoundException, ContainerAlreadyExistsException, TypeConflictException, IllegalActionException, StoreSpecificException, DeadlockException {
        PersistenceUtil.copy(session, this, session, target, replace);
    }

}

