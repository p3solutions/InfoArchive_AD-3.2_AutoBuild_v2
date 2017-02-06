/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  com.xhive.core.interfaces.XhiveSessionIf
 *  com.xhive.dom.interfaces.XhiveBlobNodeIf
 *  com.xhive.dom.interfaces.XhiveLibraryChildIf
 *  com.xhive.dom.interfaces.XhiveNodeIf
 *  com.xhive.error.xquery.XQueryStackTraceElement
 *  com.xhive.query.interfaces.AbstractXQueryResolver
 */
package com.emc.documentum.xml.dds.xquery.xdb.internal;

import com.emc.documentum.xml.dds.DDS;
import com.emc.documentum.xml.dds.application.Application;
import com.emc.documentum.xml.dds.exception.DDSException;
import com.emc.documentum.xml.dds.operation.framework.Operation;
import com.emc.documentum.xml.dds.operation.library.persistence.RetrieveOperation;
import com.emc.documentum.xml.dds.persistence.Container;
import com.emc.documentum.xml.dds.persistence.ContentDescriptor;
import com.emc.documentum.xml.dds.persistence.Data;
import com.emc.documentum.xml.dds.persistence.Session;
import com.emc.documentum.xml.dds.persistence.StoreChild;
import com.emc.documentum.xml.dds.persistence.StoreType;
import com.emc.documentum.xml.dds.persistence.XMLNode;
import com.emc.documentum.xml.dds.persistence.data.ByteArrayData;
import com.emc.documentum.xml.dds.persistence.data.InputStreamData;
import com.emc.documentum.xml.dds.persistence.data.ObjectData;
import com.emc.documentum.xml.dds.persistence.data.StringData;
import com.emc.documentum.xml.dds.persistence.exception.AmbiguousXPointerException;
import com.emc.documentum.xml.dds.persistence.exception.ContainerNotFoundException;
import com.emc.documentum.xml.dds.persistence.exception.DeadlockException;
import com.emc.documentum.xml.dds.persistence.exception.InvalidContentException;
import com.emc.documentum.xml.dds.persistence.exception.LocationAlreadyExistsException;
import com.emc.documentum.xml.dds.persistence.exception.LocationNotFoundException;
import com.emc.documentum.xml.dds.persistence.exception.NodeNotFoundException;
import com.emc.documentum.xml.dds.persistence.exception.StoreSpecificException;
import com.emc.documentum.xml.dds.persistence.exception.TypeConflictException;
import com.emc.documentum.xml.dds.persistence.xdb.internal.XDBContainer;
import com.emc.documentum.xml.dds.persistence.xdb.internal.XDBContainerUtil;
import com.emc.documentum.xml.dds.persistence.xdb.internal.XDBLocation;
import com.emc.documentum.xml.dds.persistence.xdb.internal.XDBLocationUtil;
import com.emc.documentum.xml.dds.persistence.xdb.internal.XDBSession;
import com.emc.documentum.xml.dds.persistence.xdb.internal.XDBXMLUtil;
import com.emc.documentum.xml.dds.uri.DDSURI;
import com.emc.documentum.xml.dds.uri.URIResolver;
import com.emc.documentum.xml.dds.uri.URITarget;
import com.emc.documentum.xml.dds.user.User;
import com.emc.documentum.xml.dds.util.internal.URIUtils;
import com.emc.documentum.xml.dds.xquery.xdb.internal.DDSXQueryUtils;
import com.xhive.core.interfaces.XhiveSessionIf;
import com.xhive.dom.interfaces.XhiveBlobNodeIf;
import com.xhive.dom.interfaces.XhiveLibraryChildIf;
import com.xhive.dom.interfaces.XhiveNodeIf;
import com.xhive.error.xquery.XQueryStackTraceElement;
import com.xhive.query.interfaces.AbstractXQueryResolver;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import org.w3c.dom.Node;

public class DDSXQueryResolver
extends AbstractXQueryResolver {
    private final Application application;
    private final User user;
    private final Session session;
    private final List<StoreChild> defaultCollection;

    public DDSXQueryResolver(Application application, User user, Session session, List<StoreChild> defaultCollection) {
        Objects.requireNonNull(application, "<null> application");
        Objects.requireNonNull(session, "<null> session");
        this.application = application;
        this.user = user;
        this.session = session;
        this.defaultCollection = defaultCollection;
    }

    public Iterator<? extends Source> resolveDocuments(XQueryStackTraceElement locationInfo, XhiveNodeIf queryContext, String uri) {
        if (uri == null) {
            return this.defaultCollection == null ? null : new TraxSourceIterator(this.defaultCollection.iterator());
        }
        Source source = this.resolveSource(uri, queryContext);
        if (source == null) {
            return null;
        }
        return Collections.singletonList(source).iterator();
    }

    public Source resolveModuleImport(XQueryStackTraceElement locationInfo, XhiveNodeIf queryContext, String namespaceUri, List<String> locationHints) {
        for (String hint : locationHints) {
            Source source = this.resolveSource(hint, queryContext);
            if (source == null) continue;
            return source;
        }
        return null;
    }

    public Source resolveSchemaImport(XQueryStackTraceElement locationInfo, XhiveNodeIf queryContext, String namespaceUri, List<String> locationHints) {
        for (String hint : locationHints) {
            Source source = this.resolveSource(hint, queryContext);
            if (source == null) continue;
            return source;
        }
        return null;
    }

    private Source resolveSource(String uri, XhiveNodeIf queryContext) {
        try {
            char firstChar;
            String resolvedURI;
            char c = firstChar = uri == null || uri.isEmpty() ? '\u0000' : uri.charAt(0);
            if (firstChar == '/' || firstChar == '\\') {
                resolvedURI = URIUtils.resolve(uri, null);
            } else {
                String baseURI = null;
                if (queryContext instanceof XhiveLibraryChildIf) {
                    Application app = DDS.getApplication();
                    try {
                        baseURI = DDSXQueryUtils.generateURI(app, (Node)queryContext);
                    }
                    catch (Exception e) {
                        baseURI = ((XhiveLibraryChildIf)queryContext).getFullPath();
                    }
                } else {
                    baseURI = null;
                }
                resolvedURI = URIUtils.resolve(uri, baseURI);
            }
            if (resolvedURI == null || !resolvedURI.startsWith("dds:")) {
                return null;
            }
            DDSURI ddsURI = DDSURI.parseURI(resolvedURI);
            URITarget target = this.application.getDefaultURIResolver().resolveURI(ddsURI, this.user);
            StoreChild storeChild = target.getStoreChild();
            return this.getTraxSource(storeChild);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Source getTraxSource(StoreChild storeChild) throws DDSException {
        String baseURI;
        StoreType storeType = storeChild.getStoreType();
        DDSURI storeChildURI = this.application.getDefaultURIResolver().generateURI(storeChild);
        String string = baseURI = storeChildURI == null ? null : storeChildURI.toString();
        if (storeType == StoreType.XDB) {
            return this.getXDBSource((XDBSession)this.session, storeChild, baseURI);
        }
        if (storeChild.isContainer()) {
            RetrieveOperation retrieveOperation = new RetrieveOperation((Container)storeChild, null);
            Data data = (Data)this.application.execute(this.user, retrieveOperation);
            if (data instanceof InputStreamData) {
                InputStream is = ((InputStreamData)data).content();
                return new StreamSource(is, baseURI);
            }
            if (data instanceof ByteArrayData) {
                byte[] bytes = ((ByteArrayData)data).content();
                return new StreamSource(new ByteArrayInputStream(bytes), baseURI);
            }
            if (data instanceof StringData) {
                String strData = ((StringData)data).content();
                return new StreamSource(new StringReader(strData), baseURI);
            }
            if (data instanceof ObjectData) {
                try {
                    byte[] bytes = ((ObjectData)data).content().toString().getBytes("UTF-8");
                    return new StreamSource(new ByteArrayInputStream(bytes), baseURI);
                }
                catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }
            }
            throw new DDSException("Unsupported store child type: " + storeChild);
        }
        throw new DDSException("Store child not a container: " + storeChild);
    }

    private Source getXDBSource(XDBSession sess, StoreChild storeChild, String baseURI) throws LocationNotFoundException, LocationAlreadyExistsException, TypeConflictException, StoreSpecificException, DeadlockException, ContainerNotFoundException, InvalidContentException, NodeNotFoundException, AmbiguousXPointerException {
        XhiveNodeIf node = storeChild.isLocation() ? XDBLocationUtil.retrieveLibrary(sess.getSession(), (XDBLocation)storeChild, null, false, false, false) : (storeChild.isContainer() ? XDBContainerUtil.retrieveLibraryChild(sess.getSession(), (XDBContainer)storeChild, null) : XDBXMLUtil.retrieveNode(sess.getSession(), (XMLNode)storeChild));
        if (!(node instanceof XhiveBlobNodeIf)) {
            return new DOMSource((Node)node, baseURI);
        }
        XhiveBlobNodeIf blob = (XhiveBlobNodeIf)node;
        return new StreamSource(blob.getContents(), baseURI);
    }

    private class TraxSourceIterator
    implements Iterator<Source> {
        private final Iterator<StoreChild> masterIt;

        public TraxSourceIterator(Iterator<StoreChild> masterIt) {
            Objects.requireNonNull(masterIt, "<null> masterIt");
            this.masterIt = masterIt;
        }

        @Override
        public boolean hasNext() {
            return this.masterIt.hasNext();
        }

        @Override
        public Source next() {
            StoreChild storeChild = this.masterIt.next();
            try {
                return DDSXQueryResolver.this.getTraxSource(storeChild);
            }
            catch (DDSException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

}

