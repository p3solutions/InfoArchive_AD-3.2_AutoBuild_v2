/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  com.xhive.core.interfaces.XhiveDatabaseIf
 *  com.xhive.core.interfaces.XhiveSessionIf
 *  com.xhive.dom.interfaces.XhiveBlobNodeIf
 *  com.xhive.dom.interfaces.XhiveLibraryIf
 *  com.xhive.dom.interfaces.XhiveNodeIf
 *  com.xhive.query.interfaces.XhiveXQueryValueIf
 *  com.xhive.util.interfaces.IterableIterator
 *  org.apache.commons.codec.binary.Base64
 */
package com.emc.documentum.xml.dds.internal.webservice.xquery;

import com.emc.documentum.xml.dds.exception.DDSException;
import com.emc.documentum.xml.dds.internal.webservice.xquery.AttributeValue;
import com.emc.documentum.xml.dds.internal.webservice.xquery.BlobNodeValue;
import com.emc.documentum.xml.dds.internal.webservice.xquery.CDataValue;
import com.emc.documentum.xml.dds.internal.webservice.xquery.CommentValue;
import com.emc.documentum.xml.dds.internal.webservice.xquery.DocumentValue;
import com.emc.documentum.xml.dds.internal.webservice.xquery.ElementValue;
import com.emc.documentum.xml.dds.internal.webservice.xquery.ProcessingInstructionValue;
import com.emc.documentum.xml.dds.internal.webservice.xquery.ResultValue;
import com.emc.documentum.xml.dds.internal.webservice.xquery.SimpleValue;
import com.emc.documentum.xml.dds.internal.webservice.xquery.TextValue;
import com.xhive.core.interfaces.XhiveDatabaseIf;
import com.xhive.core.interfaces.XhiveSessionIf;
import com.xhive.dom.interfaces.XhiveBlobNodeIf;
import com.xhive.dom.interfaces.XhiveLibraryIf;
import com.xhive.dom.interfaces.XhiveNodeIf;
import com.xhive.query.interfaces.XhiveXQueryValueIf;
import com.xhive.util.interfaces.IterableIterator;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.codec.binary.Base64;
import org.w3c.dom.Node;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;

public class XQueryResultHandler
implements com.emc.documentum.xml.dds.fs.xquery.XQueryResultHandler<IterableIterator<? extends XhiveXQueryValueIf>, List<ResultValue>> {
    @Override
    public List<ResultValue> transformXQueryResult(IterableIterator<? extends XhiveXQueryValueIf> input) {
        ArrayList<ResultValue> list = new ArrayList<ResultValue>();
        for (XhiveXQueryValueIf value : input) {
            list.add(this.serialize(value));
        }
        return list;
    }

    private ResultValue serialize(XhiveXQueryValueIf value) {
        if (value.isNode()) {
            XhiveNodeIf node = value.asNode();
            switch (node.getNodeType()) {
                case 1: {
                    return new ElementValue(value.getXQueryTypeName(), node.getLocalName(), node.getNamespaceURI(), this.nodeToString(node));
                }
                case 2: {
                    return new AttributeValue(value.getXQueryTypeName(), node.getLocalName(), node.getNamespaceURI(), node.getNodeValue());
                }
                case 9: {
                    return new DocumentValue(value.getXQueryTypeName(), this.nodeToString(node));
                }
                case 203: {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    try {
                        InputStream is = ((XhiveBlobNodeIf)node).getContents();
                        this.copyInputStream(is, baos);
                        byte[] bytes = Base64.encodeBase64((byte[])baos.toByteArray());
                        return new BlobNodeValue(value.getXQueryTypeName(), bytes);
                    }
                    catch (IOException ioe) {
                        return null;
                    }
                }
                case 3: {
                    return new TextValue(value.getXQueryTypeName(), node.getNodeValue());
                }
                case 4: {
                    return new CDataValue(value.getXQueryTypeName(), node.getNodeValue());
                }
                case 8: {
                    return new CommentValue(value.getXQueryTypeName(), node.getNodeValue());
                }
                case 7: {
                    return new ProcessingInstructionValue(value.getXQueryTypeName(), ((ProcessingInstruction)node).getTarget(), ((ProcessingInstruction)node).getData());
                }
            }
            return null;
        }
        return new SimpleValue(value.getXQueryTypeName(), value.asString());
    }

    private String nodeToString(XhiveNodeIf node) {
        XhiveLibraryIf dbRoot = node.getSession().getDatabase().getRoot();
        StringWriter sw = new StringWriter();
        LSOutput output = dbRoot.createLSOutput();
        output.setCharacterStream(sw);
        LSSerializer serializer = dbRoot.createLSSerializer();
        serializer.write((Node)node, output);
        return sw.toString();
    }

    private void copyInputStream(InputStream is, OutputStream os) throws IOException {
        byte[] buffer = new byte[4096];
        int len = 0;
        while (len > -1) {
            len = is.read(buffer);
            if (len <= 0) continue;
            os.write(buffer, 0, len);
        }
    }
}

