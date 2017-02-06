/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  com.xhive.core.interfaces.XhiveSessionIf
 *  com.xhive.dom.interfaces.XhiveLibraryChildIf
 *  com.xhive.dom.interfaces.XhiveLibraryIf
 *  com.xhive.query.interfaces.XQueryResolverIf
 *  com.xhive.query.interfaces.XhivePreparedQueryIf
 *  com.xhive.query.interfaces.XhiveXQueryCompilerIf
 *  com.xhive.query.interfaces.XhiveXQueryExtensionFunctionIf
 *  com.xhive.query.interfaces.XhiveXQueryQueryIf
 */
package com.emc.documentum.xml.dds.xquery.xdb.internal;

import com.emc.documentum.xml.dds.DDS;
import com.emc.documentum.xml.dds.application.Application;
import com.emc.documentum.xml.dds.persistence.Container;
import com.emc.documentum.xml.dds.persistence.Location;
import com.emc.documentum.xml.dds.persistence.Session;
import com.emc.documentum.xml.dds.persistence.StoreChild;
import com.emc.documentum.xml.dds.persistence.XMLNode;
import com.emc.documentum.xml.dds.persistence.xdb.internal.XDBXMLUtil;
import com.emc.documentum.xml.dds.uri.DDSURI;
import com.emc.documentum.xml.dds.uri.URIResolver;
import com.emc.documentum.xml.dds.uri.exception.DDSURIException;
import com.emc.documentum.xml.dds.user.User;
import com.emc.documentum.xml.dds.xquery.xdb.internal.DDSMetadata;
import com.emc.documentum.xml.dds.xquery.xdb.internal.DDSURIExtensionFunction;
import com.emc.documentum.xml.dds.xquery.xdb.internal.DDSXQueryResolver;
import com.xhive.core.interfaces.XhiveSessionIf;
import com.xhive.dom.interfaces.XhiveLibraryChildIf;
import com.xhive.dom.interfaces.XhiveLibraryIf;
import com.xhive.query.interfaces.XQueryResolverIf;
import com.xhive.query.interfaces.XhivePreparedQueryIf;
import com.xhive.query.interfaces.XhiveXQueryCompilerIf;
import com.xhive.query.interfaces.XhiveXQueryExtensionFunctionIf;
import com.xhive.query.interfaces.XhiveXQueryQueryIf;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.xml.namespace.QName;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

public final class DDSXQueryUtils {
    public static final QName XQUERY_FUNCTION_GENERATE_URI = new QName("http://www.emc.com/documentum/xml/dds", "generate-uri");
    public static final QName XQUERY_FUNCTION_METADATA = new QName("http://www.emc.com/documentum/xml/dds", "metadata");
    private static final String PATH_DATA_FRAGMENT = "/DATA/";
    private static final String PATH_COLLECTION_FRAGMENT = "/Collection";
    private static final String PATH_COLLECTION_METADATA_FRAGMENT = "/CollectionMetadata";

    private DDSXQueryUtils() {
    }

    public static XhiveXQueryQueryIf newXQuery(XhiveSessionIf session, String queryStr, Map<String, String> variables, XQueryResolverIf resolver) {
        XhiveXQueryCompilerIf compiler = session.getXQueryCompiler();
        compiler.setResolver(resolver);
        DDSXQueryUtils.registerXQueryFunctions(compiler);
        XhivePreparedQueryIf preparedQuery = compiler.prepareQuery(queryStr);
        XhiveXQueryQueryIf query = preparedQuery.createQuery(session);
        query.setResolver(resolver);
        DDSXQueryUtils.setVariables(query, variables);
        return query;
    }

    public static XQueryResolverIf getDefaultDDSXQueryResolver(User user, Session session, List<StoreChild> defaultCollection) {
        return new DDSXQueryResolver(DDS.getApplication(), user, session, defaultCollection);
    }

    private static void registerXQueryFunctions(XhiveXQueryCompilerIf compiler) {
        compiler.setFunction(XQUERY_FUNCTION_GENERATE_URI, 1, (XhiveXQueryExtensionFunctionIf)new DDSURIExtensionFunction(DDS.getApplication()));
        compiler.setFunction(XQUERY_FUNCTION_METADATA, 2, (XhiveXQueryExtensionFunctionIf)new DDSMetadata());
    }

    private static void setVariables(XhiveXQueryQueryIf query, Map<String, String> variableMap) {
        if (variableMap != null) {
            for (Map.Entry<String, String> entry : variableMap.entrySet()) {
                query.setVariable(entry.getKey(), (Object)entry.getValue());
            }
        }
    }

    public static String generateURI(Application application, Node node) {
        try {
            Location location = null;
            Container container = null;
            String xPointer = null;
            String path = XDBXMLUtil.generatePath(node);
            if (node instanceof XhiveLibraryIf) {
                location = XDBXMLUtil.findStore(node, application).getLocation(path);
            } else if (node instanceof XhiveLibraryChildIf) {
                String locPath = path.substring(0, path.lastIndexOf("/"));
                String cntPath = path.substring(path.lastIndexOf("/") + 1);
                container = XDBXMLUtil.findStore(node, application).getContainer(locPath, cntPath);
            } else if (path != null) {
                String subPath = path.substring(0, path.lastIndexOf("#"));
                String locPath = subPath.substring(0, subPath.lastIndexOf("/"));
                String cntPath = subPath.substring(subPath.lastIndexOf("/") + 1);
                xPointer = path.substring(path.lastIndexOf("#") + 1);
                container = XDBXMLUtil.findStore(node, application).getContainer(locPath, cntPath);
            }
            DDSURI uri = application.getDefaultURIResolver().generateURI(XDBXMLUtil.findStore(node, application).getXMLNode(location, container, xPointer, node));
            return uri.toString();
        }
        catch (DDSURIException due) {
            throw new RuntimeException(due);
        }
    }

    public static Node getMetadata(Application application, Node node, String metadataScheme) {
        XhiveLibraryChildIf libChild;
        if ("XDB".equals(metadataScheme)) {
            throw new RuntimeException("To access xDB metadata, use external function xhive:metadata");
        }
        if (!"DOCUMENTUM".equals(metadataScheme)) {
            throw new RuntimeException("Metadata scheme : " + metadataScheme + " not supported");
        }
        if (node instanceof XhiveLibraryIf) {
            throw new RuntimeException("Location metadata not supported");
        }
        XhiveLibraryChildIf xhiveLibraryChildIf = libChild = node instanceof XhiveLibraryChildIf ? (XhiveLibraryChildIf)node : (XhiveLibraryChildIf)node.getOwnerDocument();
        if (libChild != null) {
            String path = libChild.getFullPath();
            if (path.startsWith("/DATA/") && path.contains("/Collection/")) {
                String metadataPath = path.replace("/Collection", "/CollectionMetadata");
                return libChild.getByPath(metadataPath);
            }
            throw new RuntimeException("Documentum type metadata is only supported for Containers of a Dataset");
        }
        return null;
    }
}

