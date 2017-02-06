/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  com.emc.documentum.xml.xproc.io.Resolver
 *  com.emc.documentum.xml.xproc.io.Source
 *  com.emc.documentum.xml.xproc.util.impl.CollectionUtil
 *  com.xhive.core.interfaces.XhiveDatabaseIf
 *  com.xhive.core.interfaces.XhiveSessionIf
 *  com.xhive.dom.interfaces.XhiveLibraryChildIf
 *  com.xhive.dom.interfaces.XhiveLibraryIf
 *  com.xhive.dom.interfaces.XhiveNodeIf
 *  com.xhive.error.xquery.XQueryStackTraceElement
 *  com.xhive.query.interfaces.AbstractXQueryResolver
 */
package com.emc.documentum.xml.dds.xproc.internal;

import com.emc.documentum.xml.dds.DDS;
import com.emc.documentum.xml.dds.application.Application;
import com.emc.documentum.xml.dds.util.internal.URIUtils;
import com.emc.documentum.xml.dds.xquery.xdb.internal.DDSXQueryUtils;
import com.emc.documentum.xml.xproc.io.Resolver;
import com.emc.documentum.xml.xproc.io.Source;
import com.emc.documentum.xml.xproc.util.impl.CollectionUtil;
import com.xhive.core.interfaces.XhiveDatabaseIf;
import com.xhive.core.interfaces.XhiveSessionIf;
import com.xhive.dom.interfaces.XhiveLibraryChildIf;
import com.xhive.dom.interfaces.XhiveLibraryIf;
import com.xhive.dom.interfaces.XhiveNodeIf;
import com.xhive.error.xquery.XQueryStackTraceElement;
import com.xhive.query.interfaces.AbstractXQueryResolver;
import java.io.InputStream;
import java.io.Reader;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import org.w3c.dom.Node;

public class DDSXProcXQueryResolver
extends AbstractXQueryResolver {
    private final Resolver resolver;
    private final List<Source> defaultCollection;

    public DDSXProcXQueryResolver(Resolver resolver, List<Source> defaultCollection) {
        Objects.requireNonNull(resolver, "<null> resolver");
        Objects.requireNonNull(defaultCollection, "<null> defaultCollection");
        this.resolver = resolver;
        this.defaultCollection = defaultCollection;
    }

    public Iterator<? extends javax.xml.transform.Source> resolveDocuments(XQueryStackTraceElement locationInfo, XhiveNodeIf queryContext, String uri) {
        Collection coll = uri == null ? this.defaultCollection : CollectionUtil.immutableCollection((Object)this.resolveSource(uri, queryContext));
        return new TraxSourceIterator(coll.iterator());
    }

    public javax.xml.transform.Source resolveModuleImport(XQueryStackTraceElement locationInfo, XhiveNodeIf queryContext, String namespaceUri, List<String> locationHints) {
        for (String hint : locationHints) {
            try {
                Source source = this.resolveSource(hint, queryContext);
                return DDSXProcXQueryResolver.xprocSourceToTraxSource(source);
            }
            catch (Exception e) {
                continue;
            }
        }
        return null;
    }

    public javax.xml.transform.Source resolveSchemaImport(XQueryStackTraceElement locationInfo, XhiveNodeIf queryContext, String namespaceUri, List<String> locationHints) {
        for (String hint : locationHints) {
            try {
                Source source = this.resolveSource(hint, queryContext);
                return DDSXProcXQueryResolver.xprocSourceToTraxSource(source);
            }
            catch (Exception e) {
                continue;
            }
        }
        return null;
    }

    private Source resolveSource(String uri, XhiveNodeIf queryContext) {
        String baseURI = null;
        if (queryContext instanceof XhiveLibraryChildIf) {
            Application application = DDS.getApplication();
            try {
                baseURI = DDSXQueryUtils.generateURI(application, (Node)queryContext);
            }
            catch (Exception e) {
                baseURI = ((XhiveLibraryChildIf)queryContext).getFullPath();
            }
        } else {
            baseURI = null;
        }
        try {
            String resolvedURI = URIUtils.resolve(uri, baseURI);
            if (resolvedURI != null && resolvedURI.startsWith("/")) {
                XhiveSessionIf session = queryContext.getSession();
                XhiveLibraryIf rootLib = session.getDatabase().getRoot();
                XhiveLibraryChildIf libChild = rootLib.getByPath(uri);
                if (libChild == null) {
                    throw new RuntimeException("Library child not found: " + uri);
                }
                return new Source((Node)libChild, uri);
            }
        }
        catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        return this.resolver.getSource(null, uri, baseURI);
    }

    private static javax.xml.transform.Source xprocSourceToTraxSource(Source source) {
        InputStream is = source.getInputStream();
        if (is != null) {
            return new StreamSource(is, source.getSystemID());
        }
        Reader reader = source.getReader();
        if (reader != null) {
            return new StreamSource(reader, source.getSystemID());
        }
        Node node = source.getNode();
        if (node != null) {
            return new DOMSource(node, source.getSystemID());
        }
        throw new IllegalArgumentException("Invalid source object: " + (Object)source);
    }

    private static class TraxSourceIterator
    implements Iterator<javax.xml.transform.Source> {
        private final Iterator<Source> masterIt;

        public TraxSourceIterator(Iterator<Source> masterIt) {
            this.masterIt = masterIt;
        }

        @Override
        public boolean hasNext() {
            return this.masterIt.hasNext();
        }

        @Override
        public javax.xml.transform.Source next() {
            Source src = this.masterIt.next();
            return DDSXProcXQueryResolver.xprocSourceToTraxSource(src);
        }

        @Override
        public void remove() {
        }
    }

}

