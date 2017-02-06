/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  com.emc.documentum.fs.datamodel.core.ObjectId
 *  com.xhive.core.interfaces.XhiveDatabaseIf
 *  com.xhive.core.interfaces.XhiveSessionIf
 *  com.xhive.dom.interfaces.XhiveLibraryIf
 *  com.xhive.dom.interfaces.XhiveNodeIf
 *  com.xhive.query.interfaces.XhiveXQueryValueIf
 *  com.xhive.util.interfaces.IterableIterator
 */
package com.emc.documentum.xml.dds.internal.webservice.search.metadata;

import com.emc.documentum.fs.datamodel.core.ObjectId;
import com.emc.documentum.xml.dds.exception.DDSException;
import com.emc.documentum.xml.dds.internal.webservice.search.metadata.MetadataField;
import com.emc.documentum.xml.dds.internal.webservice.search.metadata.MetadataFieldResult;
import com.emc.documentum.xml.dds.internal.webservice.search.metadata.MetadataFieldValue;
import com.emc.documentum.xml.dds.internal.webservice.search.metadata.ResultValue;
import com.xhive.core.interfaces.XhiveDatabaseIf;
import com.xhive.core.interfaces.XhiveSessionIf;
import com.xhive.dom.interfaces.XhiveLibraryIf;
import com.xhive.dom.interfaces.XhiveNodeIf;
import com.xhive.query.interfaces.XhiveXQueryValueIf;
import com.xhive.util.interfaces.IterableIterator;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;

public class MetadataSearchResultHandler
implements com.emc.documentum.xml.dds.fs.search.metadata.MetadataSearchResultHandler<IterableIterator<? extends XhiveXQueryValueIf>, List<ResultValue>> {
    private ResultValue currentValue;
    private final List<ResultValue> result = new ArrayList<ResultValue>();

    @Override
    public List<ResultValue> transformSearchResult(IterableIterator<? extends XhiveXQueryValueIf> input) {
        return this.transformXDBXQueryResult(input);
    }

    private List<ResultValue> transformXDBXQueryResult(XhiveXQueryValueIf xhiveXQueryValue) {
        if (xhiveXQueryValue.isNode()) {
            XhiveNodeIf node = xhiveXQueryValue.asNode();
            if (node.getNodeType() == 1 && "results".equals(node.getNodeName())) {
                this.currentValue = new ResultValue();
                this.result.add(this.currentValue);
                NodeList childNodes = ((Element)node).getChildNodes();
                for (int i = 0; i < childNodes.getLength(); ++i) {
                    Node child = childNodes.item(i);
                    if (!"result".equals(child.getNodeName())) continue;
                    NodeList grandChildNodes = ((Element)child).getChildNodes();
                    String name = null;
                    String value = null;
                    for (int j = 0; j < grandChildNodes.getLength(); ++j) {
                        Node grandChild = grandChildNodes.item(j);
                        if ("name".equals(grandChild.getNodeName())) {
                            name = ((Element)grandChild).getTextContent();
                        }
                        if (!"value".equals(grandChild.getNodeName())) continue;
                        value = ((Element)grandChild).getTextContent();
                    }
                    MetadataField metadataField = new MetadataField();
                    metadataField.setName(name);
                    MetadataFieldValue metadataFieldValue = new MetadataFieldValue();
                    metadataFieldValue.setStringValue(value);
                    MetadataFieldResult metadataFieldResult = new MetadataFieldResult(metadataField, metadataFieldValue);
                    this.currentValue.getMetadataFieldResults().add(metadataFieldResult);
                }
            } else if (node.getNodeType() == 9) {
                this.currentValue.setXmlValue(this.nodeToString(node));
            }
        } else {
            String uri = xhiveXQueryValue.asString();
            this.currentValue.setObjectId(new ObjectId(uri));
        }
        return this.result;
    }

    private List<ResultValue> transformXDBXQueryResult(IterableIterator<? extends XhiveXQueryValueIf> iterator) {
        for (XhiveXQueryValueIf value : iterator) {
            this.transformXDBXQueryResult(value);
        }
        return this.result;
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
}

