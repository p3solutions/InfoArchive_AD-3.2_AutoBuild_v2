/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.apache.xerces.parsers.DOMParser
 *  org.apache.xml.serializer.dom3.LSSerializerImpl
 */
package com.emc.documentum.xml.dds.usi;

import com.emc.documentum.xml.dds.logging.LogCenter;
import com.emc.documentum.xml.dds.usi.Response;
import java.io.Reader;
import java.io.StringReader;
import org.apache.xerces.parsers.DOMParser;
import org.apache.xml.serializer.dom3.LSSerializerImpl;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

public class SimpleResponse
implements Response {
    private String xmlFragment;
    private Node xmlNode;

    public SimpleResponse(String xmlFragment) {
        this.xmlFragment = xmlFragment;
    }

    public SimpleResponse(Node xmlNode) {
        this.xmlNode = xmlNode;
    }

    @Override
    public Node asNode() {
        if (this.xmlNode != null) {
            return this.xmlNode;
        }
        if (this.xmlFragment != null) {
            try {
                DOMParser parser = new DOMParser();
                parser.parse(new InputSource(new StringReader(this.xmlFragment)));
                this.xmlNode = parser.getDocument().getDocumentElement();
            }
            catch (Exception e) {
                LogCenter.exception(this, "Failed to parse XML when interpreting Response as Node, fragment was :\n" + this.xmlFragment, e);
            }
        }
        return this.xmlNode;
    }

    @Override
    public String asString() {
        if (this.xmlFragment != null) {
            return this.xmlFragment;
        }
        if (this.xmlNode != null) {
            LSSerializerImpl serializer = new LSSerializerImpl();
            this.xmlFragment = serializer.writeToString(this.xmlNode);
        }
        return this.xmlFragment;
    }

    @Override
    public String getRequestId() {
        return this.lookup(this.asNode(), "request");
    }

    @Override
    public String getContext() {
        return this.lookup(this.asNode(), "context");
    }

    @Override
    public String getTime() {
        return this.lookup(this.asNode(), "time");
    }

    private String lookup(Node parentNode, String tagName) {
        for (Node node = parentNode.getFirstChild(); node != null; node = node.getNextSibling()) {
            if (!(node instanceof Element) || !tagName.equals(((Element)node).getTagName())) continue;
            return node.getTextContent();
        }
        return null;
    }
}

