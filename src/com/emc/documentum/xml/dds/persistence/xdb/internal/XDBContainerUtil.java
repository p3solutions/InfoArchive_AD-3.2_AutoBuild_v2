/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  com.xhive.core.interfaces.XhiveSessionIf
 *  com.xhive.dom.interfaces.XhiveBlobNodeIf
 *  com.xhive.dom.interfaces.XhiveDocumentIf
 *  com.xhive.dom.interfaces.XhiveLSParserIf
 *  com.xhive.dom.interfaces.XhiveLibraryChildIf
 *  com.xhive.dom.interfaces.XhiveLibraryIf
 *  com.xhive.dom.interfaces.XhiveNodeIf
 *  com.xhive.error.XhiveDeadlockException
 *  com.xhive.error.XhiveException
 */
package com.emc.documentum.xml.dds.persistence.xdb.internal;

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
import com.emc.documentum.xml.dds.persistence.exception.InvalidContentException;
import com.emc.documentum.xml.dds.persistence.exception.LocationAlreadyExistsException;
import com.emc.documentum.xml.dds.persistence.exception.LocationNotFoundException;
import com.emc.documentum.xml.dds.persistence.exception.StoreSpecificException;
import com.emc.documentum.xml.dds.persistence.exception.TypeConflictException;
import com.emc.documentum.xml.dds.persistence.xdb.internal.XDBContainer;
import com.emc.documentum.xml.dds.persistence.xdb.internal.XDBLocation;
import com.emc.documentum.xml.dds.persistence.xdb.internal.XDBLocationUtil;
import com.emc.documentum.xml.dds.util.internal.DistributedByteArray;
import com.emc.documentum.xml.dds.util.internal.ImportSettings;
import com.emc.documentum.xml.dds.util.internal.LSParserConfiguration;
import com.xhive.core.interfaces.XhiveSessionIf;
import com.xhive.dom.interfaces.XhiveBlobNodeIf;
import com.xhive.dom.interfaces.XhiveDocumentIf;
import com.xhive.dom.interfaces.XhiveLSParserIf;
import com.xhive.dom.interfaces.XhiveLibraryChildIf;
import com.xhive.dom.interfaces.XhiveLibraryIf;
import com.xhive.dom.interfaces.XhiveNodeIf;
import com.xhive.error.XhiveDeadlockException;
import com.xhive.error.XhiveException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.Writer;
import java.util.Map;
import java.util.Set;
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Node;
import org.w3c.dom.ls.LSInput;

public final class XDBContainerUtil {
    private XDBContainerUtil() {
    }

    public static XhiveLibraryChildIf retrieveLibraryChild(XhiveSessionIf session, XDBContainer container, ContentDescriptor content) throws LocationNotFoundException, ContainerNotFoundException, InvalidContentException, TypeConflictException, StoreSpecificException, DeadlockException {
        try {
            return XDBContainerUtil.retrieveLibraryChild(session, container, content, false, false, false);
        }
        catch (ContainerAlreadyExistsException caee) {
            throw new StoreSpecificException(caee);
        }
    }

    public static XhiveLibraryChildIf retrieveLibraryChild(XhiveSessionIf session, XDBContainer container, ContentDescriptor content, boolean createPath, boolean create, boolean replace) throws LocationNotFoundException, ContainerNotFoundException, ContainerAlreadyExistsException, InvalidContentException, TypeConflictException, StoreSpecificException, DeadlockException {
        try {
            XhiveLibraryIf library = XDBLocationUtil.retrieveLibrary(session, (XDBLocation)container.getLocation(), null, createPath, createPath, false);
            if (library == null) {
                throw new LocationNotFoundException("Could not retrieve parent Library : " + container.getLocation().getPath(false));
            }
            XhiveLibraryChildIf libraryChild = library.get(container.getName());
            if (libraryChild instanceof XhiveLibraryIf) {
                throw new TypeConflictException("Location exists at the specified path : " + container.getPath());
            }
            if (libraryChild == null) {
                if (create) {
                    if (content != null && content.isXML()) {
                        XhiveDocumentIf document = library.createDocument(null, null, null);
                        document.setName(container.getName());
                        library.appendChild((Node)document);
                        return document;
                    }
                    XhiveBlobNodeIf blob = library.createBlob();
                    blob.setName(container.getName());
                    library.appendChild((Node)blob);
                    return blob;
                }
                throw new ContainerNotFoundException("Could not retrieve Container : " + container.getPath());
            }
            if (content != null) {
                if (content.isXML() && libraryChild instanceof XhiveBlobNodeIf) {
                    throw new InvalidContentException("Trying to read binary data as XML : " + container.getPath());
                }
                if (content.isXML() && !(libraryChild instanceof XhiveDocumentIf)) {
                    throw new TypeConflictException("A library child already exists at this location which is not the correct type : " + container.getPath());
                }
            }
            if (create) {
                if (!replace) {
                    throw new ContainerAlreadyExistsException("Container already exists : " + container.getPath());
                }
                library.removeChild((Node)libraryChild);
                libraryChild = content != null && content.isXML() ? library.createDocument(null, null, null) : library.createBlob();
                libraryChild.setName(container.getName());
                library.appendChild((Node)libraryChild);
            }
            return libraryChild;
        }
        catch (LocationAlreadyExistsException laee) {
            throw new StoreSpecificException("Internal error.");
        }
        catch (XhiveDeadlockException xde) {
            throw new DeadlockException((Throwable)xde);
        }
        catch (XhiveException xe) {
            throw new StoreSpecificException((Throwable)xe);
        }
    }

    public static XhiveDocumentIf storeXML(XhiveSessionIf session, XDBContainer container, ContentDescriptor content, Data<?> data, boolean replace, ImportSettings importSettings, String resourceResolver) throws LocationNotFoundException, ContainerAlreadyExistsException, TypeConflictException, StoreSpecificException, DeadlockException {
        try {
            XhiveDocumentIf document = (XhiveDocumentIf)XDBContainerUtil.retrieveLibraryChild(session, container, content, false, true, replace);
            XhiveLibraryIf parentLibrary = document.getOwnerLibrary();
            XhiveLSParserIf parser = parentLibrary.createLSParser();
            XDBContainerUtil.setParseOptions(parser.getDomConfig(), importSettings);
            LSInput lsInput = parentLibrary.createLSInput();
            if (resourceResolver != null) {
                lsInput.setBaseURI(resourceResolver);
            }
            XDBContainerUtil.setInput(lsInput, data);
            lsInput.setSystemId("");
            parser.parseWithContext(lsInput, (Node)document, (short) 5);
            return document;
        }
        catch (ContainerNotFoundException cnfe) {
            throw new StoreSpecificException("Internal error.", cnfe);
        }
        catch (XhiveDeadlockException xde) {
            throw new DeadlockException((Throwable)xde);
        }
        catch (XhiveException xe) {
            throw new StoreSpecificException((Throwable)xe);
        }
        catch (LocationNotFoundException lnfe) {
            throw lnfe;
        }
        catch (ContainerAlreadyExistsException caee) {
            throw caee;
        }
        catch (TypeConflictException tce) {
            throw tce;
        }
        catch (Exception e) {
            throw new StoreSpecificException("Internal error.", e);
        }
        catch (Throwable t) {
            throw new StoreSpecificException("Internal error.", t);
        }
    }

    public static XhiveBlobNodeIf storeNonXML(XhiveSessionIf session, XDBContainer container, ContentDescriptor content, Data<?> data, boolean replace) throws LocationNotFoundException, ContainerAlreadyExistsException, TypeConflictException, StoreSpecificException, DeadlockException {
        try {
            XhiveBlobNodeIf blob = (XhiveBlobNodeIf)XDBContainerUtil.retrieveLibraryChild(session, container, content, false, true, replace);
            XhiveLibraryIf parentLibrary = blob.getOwnerLibrary();
            parentLibrary.appendChild((Node)blob);
            XDBContainerUtil.setBlobContent(blob, data);
            return blob;
        }
        catch (ContainerNotFoundException cnfe) {
            throw new StoreSpecificException("Internal error.");
        }
        catch (XhiveDeadlockException xde) {
            throw new DeadlockException((Throwable)xde);
        }
        catch (XhiveException xe) {
            throw new StoreSpecificException((Throwable)xe);
        }
        catch (InvalidContentException ice) {
            throw new StoreSpecificException("Internal error.");
        }
    }

    private static void setParseOptions(DOMConfiguration targetConfig, ImportSettings importSettings) {
        if (importSettings != null) {
            LSParserConfiguration sourceConfig = importSettings.getDOMConfiguration();
            for (String key : sourceConfig.getSettings().keySet()) {
                targetConfig.setParameter(key, sourceConfig.getSettings().get(key));
            }
        }
    }

    private static void setInput(LSInput input, Data<?> data) {
        if (data instanceof ByteArrayData) {
            input.setByteStream(new ByteArrayInputStream(((ByteArrayData)data).content()));
        } else if (data instanceof InputStreamData) {
            input.setByteStream(((InputStreamData)data).content());
        } else if (data instanceof StringData) {
            input.setStringData(((StringData)data).content());
        }
    }

    private static void setBlobContent(XhiveBlobNodeIf blob, Data<?> data) {
        if (data instanceof ByteArrayData) {
            try {
                blob.setContents((InputStream)new ByteArrayInputStream(((ByteArrayData)data).content()));
            }
            catch (IOException ioe) {
                LogCenter.exception("XDBContainerUtil : Could not set Blob contents :", (Throwable)ioe);
            }
        } else if (data instanceof InputStreamData) {
            try {
                blob.setContents(((InputStreamData)data).content());
                ((InputStreamData)data).content().close();
            }
            catch (IOException ioe) {
                LogCenter.exception("XDBContainerUtil : Could not set Blob contents :", (Throwable)ioe);
            }
        } else if (data instanceof StringData) {
            try {
                StringReader reader = new StringReader(((StringData)data).content());
                DistributedByteArray dba = new DistributedByteArray();
                Writer writer = dba.getWriter();
                int offset = 0;
                char[] buf = new char[5096];
                int read = 0;
                while (read != -1) {
                    read = reader.read(buf, offset, buf.length);
                    if (read == -1) continue;
                    writer.write(buf, 0, read);
                }
                reader.close();
                blob.setContents(dba.getInputStream());
            }
            catch (IOException ioe) {
                LogCenter.exception("XDBContainerUtil : Could not set Blob contents :", (Throwable)ioe);
            }
        }
    }
}

