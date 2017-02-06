/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  com.emc.esu.api.Acl
 *  com.emc.esu.api.EsuException
 *  com.emc.esu.api.Extent
 *  com.emc.esu.api.Identifier
 *  com.emc.esu.api.Metadata
 *  com.emc.esu.api.MetadataList
 *  com.emc.esu.api.MetadataTag
 *  com.emc.esu.api.MetadataTags
 *  com.emc.esu.api.ObjectId
 *  com.emc.esu.api.rest.EsuRestApi
 *  org.apache.xerces.parsers.DOMParser
 */
package com.emc.documentum.xml.dds.persistence.esu.internal;

import com.emc.documentum.xml.dds.exception.DDSException;
import com.emc.documentum.xml.dds.operation.exception.OperationException;
import com.emc.documentum.xml.dds.persistence.Container;
import com.emc.documentum.xml.dds.persistence.ContentDescriptor;
import com.emc.documentum.xml.dds.persistence.Data;
import com.emc.documentum.xml.dds.persistence.Location;
import com.emc.documentum.xml.dds.persistence.Metadata;
import com.emc.documentum.xml.dds.persistence.MetadataScheme;
import com.emc.documentum.xml.dds.persistence.Session;
import com.emc.documentum.xml.dds.persistence.Store;
import com.emc.documentum.xml.dds.persistence.StoreType;
import com.emc.documentum.xml.dds.persistence.XMLNode;
import com.emc.documentum.xml.dds.persistence.content.ObjectContentDescriptor;
import com.emc.documentum.xml.dds.persistence.content.XMLContentDescriptor;
import com.emc.documentum.xml.dds.persistence.data.ByteArrayData;
import com.emc.documentum.xml.dds.persistence.data.InputStreamData;
import com.emc.documentum.xml.dds.persistence.data.ObjectData;
import com.emc.documentum.xml.dds.persistence.data.StringData;
import com.emc.documentum.xml.dds.persistence.esu.internal.ESUSession;
import com.emc.documentum.xml.dds.persistence.esu.internal.ESUStore;
import com.emc.documentum.xml.dds.persistence.esu.internal.ESUXMLNode;
import com.emc.documentum.xml.dds.persistence.exception.ContainerAlreadyExistsException;
import com.emc.documentum.xml.dds.persistence.exception.ContainerNotFoundException;
import com.emc.documentum.xml.dds.persistence.exception.DeadlockException;
import com.emc.documentum.xml.dds.persistence.exception.IllegalActionException;
import com.emc.documentum.xml.dds.persistence.exception.InvalidContentException;
import com.emc.documentum.xml.dds.persistence.exception.LocationNotFoundException;
import com.emc.documentum.xml.dds.persistence.exception.SchemeNotSupportedException;
import com.emc.documentum.xml.dds.persistence.exception.StoreSpecificException;
import com.emc.documentum.xml.dds.persistence.exception.TypeConflictException;
import com.emc.documentum.xml.dds.persistence.internal.AbstractContainer;
import com.emc.documentum.xml.dds.persistence.internal.PersistenceUtil;
import com.emc.documentum.xml.dds.persistence.metadata.DocumentumMetadata;
import com.emc.documentum.xml.dds.persistence.registry.vfs.esu.ESUContentRegistry;
import com.emc.documentum.xml.dds.serialization.Serializer;
import com.emc.documentum.xml.dds.serialization.exception.DeserializationException;
import com.emc.documentum.xml.dds.serialization.exception.SerializationException;
import com.emc.documentum.xml.dds.util.internal.DistributedByteArray;
import com.emc.esu.api.Acl;
import com.emc.esu.api.EsuException;
import com.emc.esu.api.Extent;
import com.emc.esu.api.Identifier;
import com.emc.esu.api.MetadataList;
import com.emc.esu.api.MetadataTag;
import com.emc.esu.api.MetadataTags;
import com.emc.esu.api.ObjectId;
import com.emc.esu.api.rest.EsuRestApi;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import org.apache.xerces.parsers.DOMParser;
import org.xml.sax.InputSource;

public class ESUContainer
extends AbstractContainer {
    private String esuid;

    protected ESUContainer(Location location, String name) {
        super(location, name);
    }

    public void setESUID(String id) {
        this.esuid = id;
    }

    public String getESUID() {
        return this.esuid;
    }

    @Override
    public XMLNode getXMLNode(String xpointer) {
        return new ESUXMLNode(this, xpointer);
    }

    @Override
    public XMLNode asXMLNode() {
        return new ESUXMLNode(this);
    }

    @Override
    public StoreType getStoreType() {
        return StoreType.ESU;
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
    public void delete(Session session) throws ContainerNotFoundException, StoreSpecificException, DeadlockException, IllegalActionException {
        String id;
        ESUContentRegistry cr = ((ESUStore)this.getStore()).getContentRegistry();
        try {
            id = cr.getEsuObjectId(this);
        }
        catch (Exception e) {
            throw new ContainerNotFoundException(e);
        }
        try {
            ((ESUSession)session).getESU().deleteObject((Identifier)new ObjectId(id));
            cr.deleteContainer(this);
        }
        catch (Exception e) {
            throw new StoreSpecificException(e);
        }
    }

    @Override
    public boolean exists(Session session) throws StoreSpecificException, DeadlockException {
        try {
            return ((ESUStore)this.getStore()).getContentRegistry().existsContainer(this);
        }
        catch (OperationException oe) {
            throw new StoreSpecificException(oe);
        }
    }

    @Override
    public void copy(Session session, Container target, boolean replace) throws LocationNotFoundException, ContainerNotFoundException, ContainerAlreadyExistsException, TypeConflictException, IllegalActionException, StoreSpecificException, DeadlockException {
        PersistenceUtil.copy(session, this, session, target, replace);
    }

    @Override
    public void move(Session session, Container target, boolean replace) throws LocationNotFoundException, ContainerNotFoundException, ContainerAlreadyExistsException, TypeConflictException, IllegalActionException, StoreSpecificException, DeadlockException {
        PersistenceUtil.move(session, this, session, target, replace);
    }

    @Override
    public void persist(Session session, ContentDescriptor contentDescriptor, Data<?> data, boolean replace) throws LocationNotFoundException, ContainerAlreadyExistsException, SerializationException, StoreSpecificException, TypeConflictException, DeadlockException {
        boolean existsContainer;
        ESUContentRegistry cr = ((ESUStore)this.getStore()).getContentRegistry();
        try {
            existsContainer = cr.existsContainer(this);
        }
        catch (DDSException de) {
            throw new StoreSpecificException("Problem accessing Content Registry :", de);
        }
        if (existsContainer && !replace) {
            throw new ContainerAlreadyExistsException("Container already exists : " + this.getPath());
        }
        try {
            if (!cr.existsLocation(this.getLocation())) {
                throw new LocationNotFoundException("Persist failed : Parent Location doesn't exist : " + this.getLocation().getPath());
            }
            if (cr.existsLocation(this.getLocation().getChildLocation(this.getName()))) {
                throw new TypeConflictException("Persist failed : Target maps to a Location : " + this.getLocation().getPath());
            }
        }
        catch (OperationException oe) {
            throw new StoreSpecificException("Problem accessing Content Registry :", oe);
        }
        if (existsContainer && replace) {
            try {
                ((ESUSession)session).getESU().deleteObject((Identifier)new ObjectId(cr.getEsuObjectId(this)));
                cr.deleteContainer(this);
            }
            catch (EsuException ee) {
                throw new StoreSpecificException("ESU operation failed : ", (Throwable)ee);
            }
            catch (Exception e) {
                throw new StoreSpecificException("Could not delete existing container : " + this.getPath(), e);
            }
        }
        try {
            EsuRestApi esu = ((ESUSession)session).getESU();
            String mimetype = "application/octet-stream";
            if (contentDescriptor == null) {
                if (data.getMimeType() != null) {
                    mimetype = data.getMimeType();
                }
            } else if (contentDescriptor instanceof ObjectContentDescriptor) {
                mimetype = "application/java-serialized-object";
            } else if (contentDescriptor.isXML()) {
                mimetype = "application/xml";
            }
            com.emc.esu.api.Metadata mdContent = new com.emc.esu.api.Metadata("com.emc.documentum.xml.dds.content", mimetype, true);
            com.emc.esu.api.Metadata mdContainer = new com.emc.esu.api.Metadata("com.emc.documentum.xml.dds.container", this.getPath(), true);
            MetadataList ml = new MetadataList();
            ml.addMetadata(mdContent);
            ml.addMetadata(mdContainer);
            byte[] newData = null;
            if (contentDescriptor instanceof ObjectContentDescriptor) {
                StringData transformedData = new StringData(((ObjectContentDescriptor)contentDescriptor).getSerializer().serialize(((ObjectData)data).content()));
                newData = transformedData.content().getBytes("UTF-8");
            } else if (data instanceof StringData) {
                newData = ((StringData)data).content().getBytes("UTF-8");
            } else if (data instanceof ByteArrayData) {
                newData = ((ByteArrayData)data).content();
            } else if (data instanceof InputStreamData) {
                int readBytes;
                InputStream inputStream = ((InputStreamData)data).content();
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream(1024);
                byte[] bytes = new byte[512];
                while ((readBytes = inputStream.read(bytes)) > 0) {
                    outputStream.write(bytes, 0, readBytes);
                }
                newData = outputStream.toByteArray();
                inputStream.close();
                outputStream.close();
            }
            ObjectId newEsuObjectId = esu.createObject(null, ml, newData, mimetype);
            HashMap<String, String> attributes = new HashMap<String, String>();
            attributes.put("esuobjectid", newEsuObjectId.toString());
            cr.createContainer(this, attributes, true);
        }
        catch (EsuException ee) {
            throw new StoreSpecificException("Persist failed, could not create ESU object :", (Throwable)ee);
        }
        catch (DDSException de) {
            throw new StoreSpecificException("Problem updating Content Registry :", de);
        }
        catch (IOException ioe) {
            throw new StoreSpecificException("Persist failed, could not create write stream :", ioe);
        }
    }

    @Override
    public Data<?> retrieve(Session session, ContentDescriptor contentDescriptor) throws ContainerNotFoundException, TypeConflictException, InvalidContentException, StoreSpecificException, DeadlockException {
        ESUContentRegistry cr = ((ESUStore)this.getStore()).getContentRegistry();
        try {
            if (cr.existsLocation(this.getLocation().getChildLocation(this.getName()))) {
                throw new TypeConflictException("Retrieve failed : a Location exists with the same path.");
            }
            if (!cr.existsContainer(this)) {
                throw new ContainerNotFoundException("Container not found : " + this.getPath());
            }
            ObjectId myESUid = new ObjectId(cr.getEsuObjectId(this));
            EsuRestApi esu = ((ESUSession)session).getESU();
            String resultMimeType = this.getMetadataValue(esu, myESUid, "com.emc.documentum.xml.dds.content");
            byte[] content = esu.readObject((Identifier)myESUid, null, null);
            ByteArrayInputStream bis = new ByteArrayInputStream(content);
            if (contentDescriptor instanceof ObjectContentDescriptor) {
                ObjectData result = new ObjectData(((ObjectContentDescriptor)contentDescriptor).getSerializer().deserialize(bis));
                result.setMimeType(resultMimeType);
                return result;
            }
            DistributedByteArray dba = new DistributedByteArray();
            int len = 0;
            byte[] buffer = new byte[16348];
            buffer = new byte[16384];
            while (len != -1) {
                len = bis.read(buffer, 0, buffer.length);
                if (len <= 1) continue;
                dba.getOutputStream().write(buffer, 0, len);
            }
            InputStreamData result = new InputStreamData(dba.getInputStream());
            if (contentDescriptor instanceof XMLContentDescriptor) {
                try {
                    DOMParser parser = new DOMParser();
                    parser.parse(new InputSource(dba.getInputStream()));
                }
                catch (Exception e) {
                    throw new InvalidContentException("Retrieve failed : XML content was expected but parse failed.", e);
                }
            }
            result.setMimeType(resultMimeType);
            return result;
        }
        catch (IOException ioe) {
            throw new StoreSpecificException("Could not read stream : ", ioe);
        }
        catch (EsuException ee) {
            throw new StoreSpecificException("ESU operation failed : ", (Throwable)ee);
        }
        catch (OperationException oe) {
            throw new StoreSpecificException("Retrieve failed : Failed to access Content Registry.", oe);
        }
        catch (DeserializationException de) {
            throw new InvalidContentException("The content could not be deserialized properly :", de);
        }
    }

    public String getMetadataValue(EsuRestApi esu, ObjectId objectId, String metadataKey) throws StoreSpecificException {
        MetadataTags tags = new MetadataTags();
        tags.addTag(new MetadataTag(metadataKey, true));
        MetadataList mList = esu.getUserMetadata((Identifier)objectId, tags);
        if (mList.count() == 0) {
            throw new StoreSpecificException("No metadata found");
        }
        com.emc.esu.api.Metadata md = mList.getMetadata(metadataKey);
        if (md == null) {
            throw new StoreSpecificException("No metadata value found for name = 'com.emc.documentum.xml.dds.content'");
        }
        return md.getValue();
    }

}

