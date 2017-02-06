/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  com.xhive.core.interfaces.XhiveDatabaseIf
 *  com.xhive.dom.interfaces.XhiveBlobNodeIf
 *  com.xhive.dom.interfaces.XhiveDOMImplementationIf
 *  com.xhive.dom.interfaces.XhiveDocumentIf
 *  com.xhive.dom.interfaces.XhiveLSParserIf
 *  com.xhive.dom.interfaces.XhiveLibraryChildIf
 *  com.xhive.dom.interfaces.XhiveLibraryIf
 *  com.xhive.dom.interfaces.XhiveNodeIf
 *  com.xhive.error.XhiveException
 */
package com.emc.documentum.xml.dds.util.internal;

import com.emc.documentum.xml.dds.util.internal.FilenameUtils;
import com.emc.documentum.xml.dds.util.internal.ImportSettings;
import com.emc.documentum.xml.dds.util.internal.LSParserConfiguration;
import com.xhive.core.interfaces.XhiveDatabaseIf;
import com.xhive.dom.interfaces.XhiveBlobNodeIf;
import com.xhive.dom.interfaces.XhiveDOMImplementationIf;
import com.xhive.dom.interfaces.XhiveDocumentIf;
import com.xhive.dom.interfaces.XhiveLSParserIf;
import com.xhive.dom.interfaces.XhiveLibraryChildIf;
import com.xhive.dom.interfaces.XhiveLibraryIf;
import com.xhive.dom.interfaces.XhiveNodeIf;
import com.xhive.error.XhiveException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSInput;

public final class LibraryChildUtils {
    private static final Logger LOG = Logger.getLogger(LibraryChildUtils.class.getName());

    private LibraryChildUtils() {
    }

    public static XhiveLibraryIf createLibraries(String libraryPath, XhiveLibraryIf root0) {
        return LibraryChildUtils.createLibraries(libraryPath, root0, 128);
    }

    public static XhiveLibraryIf createLibraries(String libraryPath, XhiveLibraryIf root0, int libraryOptions) {
        XhiveLibraryIf newLib = null;
        XhiveLibraryIf root = root0.getDatabase().getRoot();
        if (libraryPath == null) {
            return null;
        }
        String path = libraryPath.trim();
        if ("".equals(path)) {
            return null;
        }
        XhiveLibraryChildIf libChild = root.getByPath(path);
        if (libChild != null && !(libChild instanceof XhiveLibraryIf)) {
            throw new RuntimeException("Not a library: " + libChild.getFullPath());
        }
        newLib = (XhiveLibraryIf)libChild;
        if (newLib != null) {
            return newLib;
        }
        if (path.endsWith("/")) {
            path = FilenameUtils.getFullPathNoEndSeparator(path);
        }
        if ("".equals(FilenameUtils.getFullPathNoEndSeparator(path))) {
            int size = FilenameUtils.getPrefixLength(path);
            String name = size <= 0 ? path : path.substring(size);
            return LibraryChildUtils.createLibrary(root, name, libraryOptions);
        }
        XhiveLibraryIf parentLib = LibraryChildUtils.createLibraries(FilenameUtils.getFullPathNoEndSeparator(path), root, libraryOptions);
        return LibraryChildUtils.createLibrary(parentLib, FilenameUtils.getName(path), libraryOptions);
    }

    public static XhiveLibraryIf createLibrary(XhiveLibraryIf parentLib, String name, int libraryOptions) {
        XhiveLibraryChildIf libChild = parentLib.get(name);
        if (libChild != null && libChild instanceof XhiveLibraryIf) {
            return (XhiveLibraryIf)libChild;
        }
        XhiveLibraryIf newLib = null;
        try {
            newLib = parentLib.createLibrary(libraryOptions);
            newLib.setName(name);
            parentLib.appendChild((Node)newLib);
        }
        catch (XhiveException xe) {
            if (xe.getErrorCode() == 106) {
                newLib = (XhiveLibraryIf)parentLib.get(name);
                if (newLib.getOptions() != libraryOptions) {
                    throw xe;
                }
            }
            throw xe;
        }
        return newLib;
    }

    @Deprecated
    public static long addDocument(InputStream inputStream, String documentPath, XhiveLibraryIf root, ImportSettings importSettings) {
        int libraryOptions = importSettings == null ? 128 : importSettings.getLibraryOptions();
        String parentPath = FilenameUtils.getFullPathNoEndSeparator(documentPath);
        String name = FilenameUtils.getName(documentPath);
        XhiveLibraryIf parentLib = LibraryChildUtils.createLibraries(parentPath, root, libraryOptions);
        return LibraryChildUtils.insertDocument(name, parentLib, inputStream, importSettings, null);
    }

    public static void updateDocument(InputStream inputStream, XhiveDocumentIf docLib, ImportSettings importSettings) {
        XhiveLSParserIf parser0 = null;
        LSInput lsInput0 = null;
        DOMImplementation dom = docLib.getImplementation();
        if (!(dom instanceof XhiveDOMImplementationIf)) {
            return;
        }
        XhiveDOMImplementationIf xhDom = (XhiveDOMImplementationIf)dom;
        parser0 = xhDom.createLSParser((short) 1, null);
        lsInput0 = xhDom.createLSInput();
        LibraryChildUtils.setParseOptions(parser0.getDomConfig(), importSettings);
        lsInput0.setByteStream(inputStream);
        parser0.parseWithContext(lsInput0, (Node)docLib, (short) 5);
    }

    public static XhiveDocumentIf createDocumentLibrary(String parentPath, String name, XhiveLibraryIf root, ImportSettings importSettings) {
        int libraryOptions = importSettings == null ? 128 : importSettings.getLibraryOptions();
        XhiveLibraryIf appMetadataParentLib = LibraryChildUtils.createLibraries(parentPath, root, libraryOptions);
        XhiveDocumentIf appDataLib = appMetadataParentLib.createDocument(null, null, null);
        appDataLib.setName(name);
        appMetadataParentLib.appendChild((Node)appDataLib);
        return appDataLib;
    }

    public static long insertBlob(String name, XhiveLibraryIf parentLib, InputStream entryIS) throws IOException {
        XhiveBlobNodeIf blobLib = null;
        XhiveLibraryChildIf libChild = parentLib.get(name);
        if (libChild != null) {
            if (libChild instanceof XhiveBlobNodeIf) {
                blobLib = (XhiveBlobNodeIf)libChild;
            } else {
                parentLib.removeChild((Node)libChild);
            }
        }
        if (blobLib == null) {
            blobLib = parentLib.createBlob();
            blobLib.setName(name);
            parentLib.appendChild((Node)blobLib);
        }
        blobLib.setContents(entryIS);
        entryIS.close();
        return blobLib.getId();
    }

    public static ImportSettings createImportSettings(DOMImplementationLS implementation) {
        if (!(implementation instanceof XhiveLibraryIf)) {
            throw new IllegalArgumentException("Only the XDB implementation is supported");
        }
        return new ImportSettings(((XhiveLibraryIf)implementation).createLSParser().getDomConfig());
    }

    public static long insertDocument(String name, XhiveLibraryIf parentLib, InputStream entryIS, ImportSettings importSettings, String resourceResolver) {
        XhiveLibraryChildIf libChild = parentLib.get(name);
        XhiveDocumentIf request = null;
        if (libChild != null) {
            if (libChild instanceof XhiveDocumentIf) {
                request = (XhiveDocumentIf)libChild;
            } else {
                parentLib.removeChild((Node)libChild);
            }
        }
        if (request == null) {
            request = parentLib.createDocument(null, null, null);
            request.setName(name);
            parentLib.appendChild((Node)request);
        }
        XhiveLSParserIf parser = parentLib.createLSParser();
        LibraryChildUtils.setParseOptions(parser.getDomConfig(), importSettings);
        LSInput lsInput = parentLib.createLSInput();
        if (resourceResolver != null) {
            lsInput.setBaseURI(resourceResolver);
        }
        lsInput.setByteStream(entryIS);
        lsInput.setSystemId("");
        parser.parseWithContext(lsInput, (Node)request, (short) 5);
        return request.getId();
    }

    private static void setParseOptions(DOMConfiguration targetConfig, ImportSettings importSettings) {
        if (importSettings != null) {
            LSParserConfiguration sourceConfig = importSettings.getDOMConfiguration();
            for (String key : sourceConfig.getSettings().keySet()) {
                targetConfig.setParameter(key, sourceConfig.getSettings().get(key));
            }
        }
    }

    public static Document createCopyDocument(DOMImplementation impl, Document original) {
        Element oElem = original.getDocumentElement();
        DocumentType oDocType = original.getDoctype();
        DocumentType cDocType = null;
        if (oDocType != null) {
            cDocType = impl.createDocumentType(oElem.getNodeName(), oDocType.getPublicId(), oDocType.getSystemId());
        }
        Document cDoc = impl.createDocument(oElem.getNamespaceURI(), oElem.getNodeName(), cDocType);
        cDoc.removeChild(cDoc.getDocumentElement());
        for (Node node = original.getFirstChild(); node != null; node = node.getNextSibling()) {
            if (node.getNodeType() == 10) continue;
            Node copyNode = cDoc.importNode(node, true);
            cDoc.appendChild(copyNode);
        }
        return cDoc;
    }

    public static XhiveBlobNodeIf createCopyBlobNode(XhiveLibraryIf parent, XhiveBlobNodeIf original) throws IOException {
        XhiveBlobNodeIf copyBlob = parent.createBlob();
        copyBlob.setContents(original.getContents());
        return copyBlob;
    }
}

