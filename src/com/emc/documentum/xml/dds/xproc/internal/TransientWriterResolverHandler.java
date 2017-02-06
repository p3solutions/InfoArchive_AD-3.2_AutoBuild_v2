/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  com.emc.documentum.xml.xproc.io.ResolverHandler
 *  com.emc.documentum.xml.xproc.io.Source
 *  com.emc.documentum.xml.xproc.io.Target
 *  com.emc.documentum.xml.xproc.io.WriterHandler
 *  com.emc.documentum.xml.xproc.io.XMLTarget
 *  com.emc.documentum.xml.xproc.pipeline.model.ExtensionContext
 */
package com.emc.documentum.xml.dds.xproc.internal;

import com.emc.documentum.xml.dds.util.internal.DistributedByteArray;
import com.emc.documentum.xml.dds.util.internal.StringUtils;
import com.emc.documentum.xml.xproc.io.ResolverHandler;
import com.emc.documentum.xml.xproc.io.Source;
import com.emc.documentum.xml.xproc.io.Target;
import com.emc.documentum.xml.xproc.io.WriterHandler;
import com.emc.documentum.xml.xproc.io.XMLTarget;
import com.emc.documentum.xml.xproc.pipeline.model.ExtensionContext;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import javax.xml.namespace.QName;

public class TransientWriterResolverHandler
implements ResolverHandler,
WriterHandler {
    public static final String SCHEME_TRANSIENT = "transient";
    private static final String SCHEME_TRANSIENT_DELIMITER = "transient:";
    public static final QName TRANSIENT_HANDLER_INTERNAL_ATTR = new QName("http://www.emc.com/documentum/xml/dds", "__transient_handler__");
    private final TransientDataRepository transientDataRepository;

    public TransientWriterResolverHandler(boolean releaseOnResolve, boolean overwrite) {
        this.transientDataRepository = new TransientDataRepository(releaseOnResolve, overwrite);
    }

    public Source getSource(String publicID, String systemID, ExtensionContext extensionContext) throws Exception {
        if (StringUtils.isEmpty(systemID) || !systemID.startsWith("transient:")) {
            return null;
        }
        Source source = this.transientDataRepository.getTransientSource(systemID);
        if (source == null) {
            throw new IOException("Transient source not found: " + systemID);
        }
        return source;
    }

    public Target getTarget(String publicID, String systemID, ExtensionContext extensionContext) throws Exception {
        if (StringUtils.isEmpty(systemID) || !systemID.startsWith("transient:")) {
            return null;
        }
        return this.transientDataRepository.newTransientTarget(publicID, systemID);
    }

    public XMLTarget getXMLTarget(String publicID, String systemID, ExtensionContext extensionContext) throws Exception {
        return (XMLTarget)this.getTarget(publicID, systemID, extensionContext);
    }

    public void release() {
        this.transientDataRepository.release();
    }

    public void release(String uri) {
        if (uri != null) {
            this.transientDataRepository.release(uri);
        }
    }

    private static class TransientTarget
    implements XMLTarget {
        private final DistributedByteArray dba = new DistributedByteArray();
        private final String publicID;
        private final String systemID;

        public TransientTarget(String publicID, String systemID) {
            this.publicID = publicID;
            this.systemID = systemID;
        }

        public OutputStream getOutputStream() {
            return this.dba.getOutputStream();
        }

        public InputStream getInputStream() {
            return this.dba.getInputStream();
        }

        public void close() throws IOException {
        }

        public String getPublicID() {
            return this.publicID;
        }

        public String getSystemID() {
            return this.systemID;
        }
    }

    private static class TransientDataRepository {
        private final Map<String, TransientTarget> transientData = new HashMap<String, TransientTarget>();
        private final boolean releaseOnResolve;
        private final boolean overwrite;

        public TransientDataRepository(boolean releaseOnResolve, boolean overwrite) {
            this.releaseOnResolve = releaseOnResolve;
            this.overwrite = overwrite;
        }

        public TransientTarget newTransientTarget(String publicID, String systemID) throws IOException {
            if (!this.overwrite && this.transientData.containsKey(systemID)) {
                throw new IOException("Transient target already exists and overwrite disabled: " + systemID);
            }
            TransientTarget target = new TransientTarget(publicID, systemID);
            this.transientData.put(systemID, target);
            return target;
        }

        public Source getTransientSource(String systemID) {
            TransientTarget target = this.transientData.get(systemID);
            if (target == null) {
                return null;
            }
            Source source = new Source(target.getInputStream(), target.getPublicID(), target.getSystemID());
            if (this.releaseOnResolve) {
                this.release(systemID);
            }
            return source;
        }

        public void release() {
            this.transientData.clear();
        }

        public void release(String systemID) {
            this.transientData.remove(systemID);
        }
    }

}

