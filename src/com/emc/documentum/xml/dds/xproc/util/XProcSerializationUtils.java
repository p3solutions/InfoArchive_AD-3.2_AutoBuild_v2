/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  com.emc.documentum.xml.xproc.io.Source
 *  com.xhive.dom.interfaces.XhiveDocumentIf
 *  com.xhive.dom.interfaces.XhiveLibraryIf
 *  com.xhive.dom.interfaces.XhiveNodeIf
 */
package com.emc.documentum.xml.dds.xproc.util;

import com.emc.documentum.xml.dds.util.internal.DistributedByteArray;
import com.emc.documentum.xml.xproc.io.Source;
import com.xhive.dom.interfaces.XhiveDocumentIf;
import com.xhive.dom.interfaces.XhiveLibraryIf;
import com.xhive.dom.interfaces.XhiveNodeIf;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Node;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;

@Deprecated
public final class XProcSerializationUtils {
    public static InputStream serialize(Source source, String encoding) throws IOException {
        Objects.requireNonNull(source, "<null> source");
        Objects.requireNonNull(encoding, "<null> encoding");
        InputStream is = source.getInputStream();
        if (is != null) {
            return is;
        }
        Node node = source.getNode();
        if (node == null) {
            throw new IllegalArgumentException("<null> node, source: " + (Object)source);
        }
        return XProcSerializationUtils.serialize(node, encoding);
    }

    public static InputStream serialize(Node node, String encoding) throws IOException {
        DOMImplementationLS domImplLS;
        Objects.requireNonNull(node, "<null> node");
        Objects.requireNonNull(encoding, "<null> encoding");
        DistributedByteArray dba = new DistributedByteArray();
        if (node instanceof XhiveNodeIf) {
            if (node instanceof XhiveLibraryIf) {
                throw new IllegalArgumentException("Serialization of XhiveLibraryIf objects not supported");
            }
            domImplLS = (DOMImplementationLS)((Object)(node instanceof XhiveDocumentIf ? (XhiveDocumentIf)node : ((XhiveNodeIf)node).getOwnerDocument()).getImplementation());
        } else {
            try {
                domImplLS = XProcSerializationUtils.getDefaultDOMImplementationLS();
            }
            catch (ParserConfigurationException pce) {
                IOException ioe = new IOException();
                ioe.initCause(pce);
                throw ioe;
            }
        }
        LSSerializer lsSerializer = XProcSerializationUtils.newLSSerializer(domImplLS, false);
        LSOutput lsOutput = domImplLS.createLSOutput();
        lsOutput.setByteStream(dba.getOutputStream());
        lsOutput.setEncoding(encoding);
        lsSerializer.write(node, lsOutput);
        return dba.getInputStream();
    }

    private static LSSerializer newLSSerializer(DOMImplementationLS domImplLS, boolean omitXMLDeclaration) {
        LSSerializer lsSerializer = domImplLS.createLSSerializer();
        if (omitXMLDeclaration) {
            if (lsSerializer.getDomConfig().canSetParameter("xml-declaration", "false")) {
                lsSerializer.getDomConfig().setParameter("xml-declaration", "false");
            } else if (lsSerializer.getDomConfig().canSetParameter("xml-declaration", "no")) {
                lsSerializer.getDomConfig().setParameter("xml-declaration", "no");
            } else if (lsSerializer.getDomConfig().canSetParameter("xml-declaration", Boolean.FALSE)) {
                lsSerializer.getDomConfig().setParameter("xml-declaration", Boolean.FALSE);
            }
        }
        return lsSerializer;
    }

    private static DOMImplementationLS getDefaultDOMImplementationLS() throws ParserConfigurationException {
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = builderFactory.newDocumentBuilder();
        DOMImplementation domImpl = builder.getDOMImplementation();
        if (!domImpl.hasFeature("LS", "3.0")) {
            throw new ParserConfigurationException("DOM Load/Save not supported");
        }
        return (DOMImplementationLS)((Object)domImpl);
    }

    private XProcSerializationUtils() {
    }
}

