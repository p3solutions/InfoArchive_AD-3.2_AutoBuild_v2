/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  com.xhive.core.interfaces.XhiveDatabaseIf
 *  com.xhive.core.interfaces.XhiveSessionIf
 *  com.xhive.dom.interfaces.XhiveBlobNodeIf
 *  com.xhive.dom.interfaces.XhiveDocumentIf
 *  com.xhive.dom.interfaces.XhiveLibraryChildIf
 *  com.xhive.dom.interfaces.XhiveLibraryIf
 *  com.xhive.dom.interfaces.XhiveNodeIf
 *  com.xhive.error.XhiveDeadlockException
 *  com.xhive.error.XhiveException
 */
package com.emc.documentum.xml.dds.persistence.xdb.internal;

import com.emc.documentum.xml.dds.persistence.Location;
import com.emc.documentum.xml.dds.persistence.exception.DeadlockException;
import com.emc.documentum.xml.dds.persistence.exception.LocationAlreadyExistsException;
import com.emc.documentum.xml.dds.persistence.exception.LocationNotFoundException;
import com.emc.documentum.xml.dds.persistence.exception.StoreSpecificException;
import com.emc.documentum.xml.dds.persistence.exception.TypeConflictException;
import com.emc.documentum.xml.dds.persistence.xdb.XDBLibraryOptions;
import com.emc.documentum.xml.dds.persistence.xdb.internal.XDBLocation;
import com.xhive.core.interfaces.XhiveDatabaseIf;
import com.xhive.core.interfaces.XhiveSessionIf;
import com.xhive.dom.interfaces.XhiveBlobNodeIf;
import com.xhive.dom.interfaces.XhiveDocumentIf;
import com.xhive.dom.interfaces.XhiveLibraryChildIf;
import com.xhive.dom.interfaces.XhiveLibraryIf;
import com.xhive.dom.interfaces.XhiveNodeIf;
import com.xhive.error.XhiveDeadlockException;
import com.xhive.error.XhiveException;
import java.util.List;
import org.w3c.dom.Node;

public final class XDBLocationUtil {
    private XDBLocationUtil() {
    }

    public static XhiveLibraryIf retrieveLibrary(XhiveSessionIf session, XDBLocation libraryLocation, XDBLibraryOptions options, boolean create, boolean createPath, boolean checkOptions) throws TypeConflictException, LocationNotFoundException, LocationAlreadyExistsException, StoreSpecificException, DeadlockException {
        try {
            XhiveLibraryIf root = session.getDatabase().getRoot();
            if (libraryLocation.isRoot()) {
                return root;
            }
            XhiveLibraryChildIf libraryChild = root.getByPath(libraryLocation.getPath(false));
            if (libraryChild == null) {
                if (!create) {
                    throw new LocationNotFoundException("Path does not exist :" + libraryLocation.getPath(false));
                }
                XhiveLibraryIf parentLibrary = XDBLocationUtil.retrieveLibrary(session, (XDBLocation)libraryLocation.getParent(), options, createPath, createPath, false);
                if (parentLibrary != null && (createPath || libraryLocation.getPathComponents().size() == 1)) {
                    return XDBLocationUtil.createLibrary(session, parentLibrary, libraryLocation, options);
                }
                throw new LocationNotFoundException("Path does not exist : " + libraryLocation.getPath(false));
            }
            if (!(libraryChild instanceof XhiveLibraryIf)) {
                throw new TypeConflictException("LibraryChild exists with the same name : " + libraryLocation.getPath(false));
            }
            XhiveLibraryIf library = (XhiveLibraryIf)libraryChild;
            if (checkOptions) {
                if (options == null || library.getOptions() == options.getLibraryOptions()) {
                    return library;
                }
                throw new LocationAlreadyExistsException("Options for retrieved Library do not match : " + libraryLocation.getPath(false));
            }
            return library;
        }
        catch (XhiveDeadlockException xde) {
            throw new DeadlockException((Throwable)xde);
        }
        catch (XhiveException xe) {
            throw new StoreSpecificException((Throwable)xe);
        }
    }

    public static XhiveLibraryIf createLibrary(XhiveSessionIf session, XhiveLibraryIf parentLibrary, XDBLocation libraryLocation, XDBLibraryOptions options) throws TypeConflictException, LocationAlreadyExistsException, StoreSpecificException, DeadlockException {
        try {
            XhiveLibraryChildIf libraryChild = parentLibrary.getByPath(libraryLocation.getName());
            if (libraryChild == null) {
                XhiveLibraryIf newLibrary = null;
                try {
                    newLibrary = parentLibrary.createLibrary(options == null ? 128 : options.getLibraryOptions());
                    newLibrary.setName(libraryLocation.getName());
                    parentLibrary.appendChild((Node)newLibrary);
                    return newLibrary;
                }
                catch (XhiveDeadlockException xde) {
                    throw new DeadlockException((Throwable)xde);
                }
                catch (XhiveException xe) {
                    if (xe.getErrorCode() == 101) {
                        throw new DeadlockException((Throwable)xe);
                    }
                    if (xe.getErrorCode() == 106) {
                        newLibrary = (XhiveLibraryIf)parentLibrary.get(libraryLocation.getName());
                        if (options != null && newLibrary.getOptions() != options.getLibraryOptions()) {
                            throw new LocationAlreadyExistsException("Options for retrieved Library do not match : " + libraryLocation.getPath(false));
                        }
                        return newLibrary;
                    }
                    throw new StoreSpecificException("XDB Exception when trying to create Library", (Throwable)xe);
                }
            }
            if (!(libraryChild instanceof XhiveLibraryIf)) {
                throw new TypeConflictException("LibraryChild exists with the same name : " + libraryLocation.getPath(false));
            }
            XhiveLibraryIf library = (XhiveLibraryIf)libraryChild;
            if (options != null && library.getOptions() != options.getLibraryOptions()) {
                throw new LocationAlreadyExistsException("Options for retrieved Library do not match : " + libraryLocation.getPath(false));
            }
            return library;
        }
        catch (XhiveDeadlockException xde) {
            throw new DeadlockException((Throwable)xde);
        }
        catch (XhiveException xe) {
            throw new StoreSpecificException((Throwable)xe);
        }
    }

    public static int getLibraryOptions(XhiveSessionIf session, XDBLocation libraryLocation) throws LocationNotFoundException, StoreSpecificException, DeadlockException {
        try {
            XhiveLibraryIf library = XDBLocationUtil.retrieveLibrary(session, libraryLocation, null, false, false, false);
            return library.getOptions();
        }
        catch (LocationAlreadyExistsException laee) {
            throw new StoreSpecificException("Internal error.");
        }
        catch (TypeConflictException tce) {
            throw new LocationNotFoundException(tce);
        }
    }

    public static XhiveLibraryIf createLocation(XhiveSessionIf session, Location child, XDBLibraryOptions options, boolean createPath) throws TypeConflictException, LocationNotFoundException {
        XhiveLibraryIf parentLibrary;
        Location parent = child.getParent();
        XhiveLibraryIf root = session.getDatabase().getRoot();
        parentLibrary = (XhiveLibraryIf) (parent.isRoot() ? root : root.getByPath(parent.getPath()));
        if (parentLibrary instanceof XhiveDocumentIf || parentLibrary instanceof XhiveBlobNodeIf) {
            throw new TypeConflictException("Create Location failed : Path contains a Container : " + parent.getPath());
        }
        if (parentLibrary == null) {
            if (!createPath) {
                throw new LocationNotFoundException("Create Location failed : Path does not exist : " + parent.getPath());
            }
            parentLibrary = XDBLocationUtil.createLocation(session, parent, options, createPath);
        }
        XhiveLibraryIf childLibrary = parentLibrary.createLibrary(options == null ? 128 : options.getLibraryOptions());
        childLibrary.setName(child.getName());
        parentLibrary.appendChild((Node)childLibrary);
        return childLibrary;
    }

    public static TYPES checkPath(XhiveSessionIf session, String path) {
        XhiveLibraryIf root = session.getDatabase().getRoot();
        XhiveLibraryChildIf child = root.getByPath(path);
        if (child == null) {
            return TYPES.NOTHING;
        }
        if (child instanceof XhiveLibraryIf) {
            return TYPES.LOCATION;
        }
        return TYPES.CONTAINER;
    }

    public static enum TYPES {
        NOTHING,
        LOCATION,
        CONTAINER;
        

        private TYPES() {
        }
    }

}

