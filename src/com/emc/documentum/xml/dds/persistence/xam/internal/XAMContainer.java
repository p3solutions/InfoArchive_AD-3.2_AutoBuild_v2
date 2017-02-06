/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.apache.xerces.parsers.DOMParser
 *  org.snia.xam.XAMException
 *  org.snia.xam.XSet
 *  org.snia.xam.XStream
 *  org.snia.xam.XSystem
 *  org.snia.xam.XUID
 *  org.snia.xam.toolkit.XAMXUID
 */
package com.emc.documentum.xml.dds.persistence.xam.internal;

import com.emc.documentum.xml.dds.exception.DDSException;
import com.emc.documentum.xml.dds.logging.LogCenter;
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
import com.emc.documentum.xml.dds.persistence.registry.vfs.xam.XAMContentRegistry;
import com.emc.documentum.xml.dds.persistence.xam.internal.XAMSession;
import com.emc.documentum.xml.dds.persistence.xam.internal.XAMStore;
import com.emc.documentum.xml.dds.persistence.xam.internal.XAMXMLNode;
import com.emc.documentum.xml.dds.serialization.Serializer;
import com.emc.documentum.xml.dds.serialization.exception.DeserializationException;
import com.emc.documentum.xml.dds.serialization.exception.SerializationException;
import com.emc.documentum.xml.dds.util.internal.DistributedByteArray;
import com.emc.documentum.xml.dds.util.internal.XMLUtils;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import org.apache.xerces.parsers.DOMParser;
import org.snia.xam.XAMException;
import org.snia.xam.XSet;
import org.snia.xam.XStream;
import org.snia.xam.XSystem;
import org.snia.xam.XUID;
import org.snia.xam.toolkit.XAMXUID;
import org.xml.sax.InputSource;

public class XAMContainer
extends AbstractContainer {
    private String xuid;

    protected XAMContainer(Location location, String name) {
        super(location, name);
    }

    public String getXUID() {
        if (this.xuid != null) {
            return this.xuid;
        }
        XAMContentRegistry cr = ((XAMStore)this.getStore()).getContentRegistry();
        try {
            return cr.getXUID(this);
        }
        catch (StoreSpecificException sse) {
            LogCenter.exception(this, (Throwable)sse);
            return null;
        }
    }

    public void setXUID(String xamid) {
        this.xuid = xamid;
    }

    @Override
    public StoreType getStoreType() {
        return StoreType.XAM;
    }

    @Override
    public XMLNode getXMLNode(String xpointer) {
        return new XAMXMLNode(this, xpointer);
    }

    @Override
    public XMLNode asXMLNode() {
        return new XAMXMLNode(this);
    }

    @Override
    public boolean exists(Session session) throws StoreSpecificException, DeadlockException {
        XAMContentRegistry cr = ((XAMStore)this.getStore()).getContentRegistry();
        return cr.existsContainer(this);
    }

    @Override
    public void delete(Session session) throws ContainerNotFoundException, StoreSpecificException, DeadlockException, IllegalActionException {
        XAMContentRegistry cr = ((XAMStore)this.getStore()).getContentRegistry();
        String id = cr.deleteContainer(this, true);
        if (id != null) {
            try {
                ((XAMSession)session).getXSystem().deleteXSet((XUID)new XAMXUID(id));
            }
            catch (XAMException xe) {
                LogCenter.exception(this, "XSet with XUID " + id + " was not deleted successfully.", (Throwable)xe);
            }
        }
    }

    @Override
    public void persist(Session session, ContentDescriptor contentDescriptor, Data<?> data, boolean replace) throws LocationNotFoundException, ContainerAlreadyExistsException, TypeConflictException, SerializationException, StoreSpecificException, DeadlockException {
        XAMContentRegistry cr = ((XAMStore)this.getStore()).getContentRegistry();
        String oldXUID = cr.createTempContainer(this, null, replace);
        if (oldXUID != null) {
            try {
                ((XAMSession)session).getXSystem().deleteXSet((XUID)new XAMXUID(oldXUID));
            }
            catch (XAMException xe) {
                LogCenter.exception(this, "XSet with XUID " + oldXUID + " was not deleted successfully when replaced by new Container.", (Throwable)xe);
            }
        }
        XSet xSet = null;
        XStream xStream = null;
        boolean setOpened = false;
        boolean streamOpened = false;
        boolean setWritten = false;
        BufferedInputStream bis = null;
        try {
            XSystem xSystem = ((XAMSession)session).getXSystem();
            xSet = xSystem.createXSet("unrestricted");
            setOpened = true;
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
            xStream = xSet.createXStream("com.emc.documentum.xml.dds.content", true, mimetype);
            streamOpened = true;
            if (contentDescriptor instanceof ObjectContentDescriptor) {
                bis = new BufferedInputStream(XMLUtils.getInputStream(((ObjectContentDescriptor)contentDescriptor).getSerializer().serialize(((ObjectData)data).content()), "UTF-8"));
            } else if (data instanceof StringData) {
                bis = new BufferedInputStream(XMLUtils.getInputStream(((StringData)data).content(), "UTF-8"));
            } else if (data instanceof ByteArrayData) {
                bis = new BufferedInputStream(new ByteArrayInputStream(((ByteArrayData)data).content()));
            } else if (data instanceof InputStreamData) {
                bis = new BufferedInputStream(((InputStreamData)data).content());
            } else {
                throw new DDSException("Unknown content type");
            }
            long offset = 0;
            byte[] buffer = new byte[16348];
            while (offset != -1) {
                int read = bis.read(buffer, 0, 16348);
                int written = 0;
                while (written < read) {
                    written = (int)((long)written + xStream.write(buffer, (long)written, (long)(read - written)));
                }
                offset = read == -1 ? -1 : offset + (long)read;
            }
            xStream.close();
            bis.close();
            streamOpened = false;
            this.xuid = xSet.commit().toString();
            xSet.close();
            setOpened = false;
            setWritten = false;
            HashMap<String, String> attributes = new HashMap<String, String>();
            attributes.put("xuid", this.xuid);
            cr.updateContainer(this, attributes, true);
        }
        catch (XAMException xe) {
            this.closeProperly(xSet, xStream, setOpened, streamOpened, bis);
            throw new StoreSpecificException("Persist failed, could not create XSet :", (Throwable)xe);
        }
        catch (IOException ioe) {
            this.closeProperly(xSet, xStream, setOpened, streamOpened, bis);
            throw new StoreSpecificException("Persist failed, could not create write stream :", ioe);
        }
        catch (DDSException de) {
            this.closeProperly(xSet, xStream, setOpened, streamOpened, bis);
            if (setWritten) {
                try {
                    ((XAMSession)session).getXSystem().deleteXSet((XUID)new XAMXUID(this.xuid));
                }
                catch (XAMException xe) {
                    LogCenter.exception(this, "XSet with XUID " + this.xuid + " was not deleted successfully.", (Throwable)xe);
                }
            }
            throw new StoreSpecificException("Problem updating Content Registry :", de);
        }
    }

    @Override
    public Data<?> retrieve(Session session, ContentDescriptor contentDescriptor) throws ContainerNotFoundException, TypeConflictException, InvalidContentException, StoreSpecificException, DeadlockException {
        XSet xSet = null;
        XStream xStream = null;
        boolean setOpened = false;
        boolean streamOpened = false;
        try {
            XAMXUID myXuid;
            if (this.xuid == null) {
                XAMContentRegistry cr = ((XAMStore)this.getStore()).getContentRegistry();
                if (cr.existsLocation(this.getLocation().getChildLocation(this.getName()))) {
                    throw new TypeConflictException("Retrieve failed : a Location exists with the same path.");
                }
                if (!cr.existsContainer(this)) {
                    throw new ContainerNotFoundException("Container not found : " + this.getPath());
                }
                myXuid = new XAMXUID(cr.getXUID(this));
            } else {
                myXuid = new XAMXUID(this.xuid);
            }
            XSystem xSystem = ((XAMSession)session).getXSystem();
            xSet = xSystem.openXSet((XUID)myXuid, "readonly");
            setOpened = true;
            xStream = xSet.openXStream("com.emc.documentum.xml.dds.content", "readonly");
            streamOpened = true;
            DistributedByteArray dba = new DistributedByteArray();
            OutputStream os = dba.getOutputStream();
            long offset = 0;
            byte[] buffer = new byte[16348];
            while (offset != -1) {
                int read = (int)xStream.read(buffer, 16348);
                if (read > 0) {
                    os.write(buffer, 0, read);
                }
                offset = read == -1 ? -1 : offset + (long)read;
            }
            xStream.close();
            streamOpened = false;
            InputStream is = dba.getInputStream();
            if (contentDescriptor instanceof ObjectContentDescriptor) {
                ObjectData result = new ObjectData(((ObjectContentDescriptor)contentDescriptor).getSerializer().deserialize(is));
                result.setMimeType(xSet.getFieldType("com.emc.documentum.xml.dds.content"));
                xSet.close();
                setOpened = false;
                return result;
            }
            InputStreamData result = new InputStreamData(is);
            if (contentDescriptor instanceof XMLContentDescriptor) {
                try {
                    DOMParser parser = new DOMParser();
                    parser.parse(new InputSource(is));
                    is.reset();
                }
                catch (Exception e) {
                    LogCenter.log("Parse failed, content was : " + dba.toString());
                    throw new InvalidContentException("Retrieve failed : XML content was expected but parse failed.", e);
                }
            }
            result.setMimeType(xSet.getFieldType("com.emc.documentum.xml.dds.content"));
            xSet.close();
            setOpened = false;
            return result;
        }
        catch (ContainerNotFoundException cnfe) {
            this.closeProperly(xSet, xStream, setOpened, streamOpened);
            throw cnfe;
        }
        catch (TypeConflictException tce) {
            this.closeProperly(xSet, xStream, setOpened, streamOpened);
            throw tce;
        }
        catch (InvalidContentException ice) {
            this.closeProperly(xSet, xStream, setOpened, streamOpened);
            throw ice;
        }
        catch (IOException ioe) {
            this.closeProperly(xSet, xStream, setOpened, streamOpened);
            throw new StoreSpecificException("Could not read stream : ", ioe);
        }
        catch (DeserializationException de) {
            this.closeProperly(xSet, xStream, setOpened, streamOpened);
            throw new InvalidContentException("The content could not be deserialized properly :", de);
        }
        catch (XAMException xe) {
            this.closeProperly(xSet, xStream, setOpened, streamOpened);
            throw new StoreSpecificException("XAM operation failed : ", (Throwable)xe);
        }
        catch (Exception e) {
            this.closeProperly(xSet, xStream, setOpened, streamOpened);
            throw new StoreSpecificException("Retrieve failed : Failed to access Content Registry.", e);
        }
    }

    private void closeProperly(XSet xSet, XStream xStream, boolean setOpened, boolean streamOpened, InputStream bis) {
        this.closeProperly(xSet, xStream, setOpened, streamOpened);
        try {
            bis.close();
        }
        catch (Exception e) {
            // empty catch block
        }
    }

    private void closeProperly(XSet xSet, XStream xStream, boolean setOpened, boolean streamOpened) {
        try {
            if (streamOpened) {
                xStream.close();
            }
        }
        catch (XAMException xe) {
            LogCenter.exception(this, "Close failed on opened XStream after exception.", (Throwable)xe);
        }
        try {
            if (setOpened) {
                xSet.close();
            }
        }
        catch (XAMException xe) {
            LogCenter.exception(this, "Close failed on opened XSet after exception.", (Throwable)xe);
        }
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
        XAMContentRegistry cr = ((XAMStore)this.getStore()).getContentRegistry();
        String obsoleteXuid = cr.moveContainer(this, target, replace);
        if (obsoleteXuid != null) {
            try {
                ((XAMSession)session).getXSystem().deleteXSet((XUID)new XAMXUID(obsoleteXuid));
            }
            catch (XAMException xe) {
                LogCenter.exception(this, "XSet with XUID " + obsoleteXuid + " was not deleted successfully.", (Throwable)xe);
            }
        }
    }

    @Override
    public void copy(Session session, Container target, boolean replace) throws LocationNotFoundException, ContainerNotFoundException, ContainerAlreadyExistsException, TypeConflictException, IllegalActionException, StoreSpecificException, DeadlockException {
        XAMContentRegistry cr = ((XAMStore)this.getStore()).getContentRegistry();
        String obsoleteXuid = cr.copyContainer(this, target, replace);
        if (obsoleteXuid != null) {
            try {
                ((XAMSession)session).getXSystem().deleteXSet((XUID)new XAMXUID(obsoleteXuid));
            }
            catch (XAMException xe) {
                LogCenter.exception(this, "XSet with XUID " + obsoleteXuid + " was not deleted successfully.", (Throwable)xe);
            }
        }
    }

}

