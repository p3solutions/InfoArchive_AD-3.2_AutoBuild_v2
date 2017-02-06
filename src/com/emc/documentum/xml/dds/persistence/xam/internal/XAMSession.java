/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.snia.xam.ConnectException
 *  org.snia.xam.InvalidXRIException
 *  org.snia.xam.VIMLoadException
 *  org.snia.xam.XAMException
 *  org.snia.xam.XAMLibrary
 *  org.snia.xam.XAMLibraryObj
 *  org.snia.xam.XSystem
 */
package com.emc.documentum.xml.dds.persistence.xam.internal;

import com.emc.documentum.xml.dds.DDS;
import com.emc.documentum.xml.dds.logging.LogCenter;
import com.emc.documentum.xml.dds.operation.exception.BeginFailedException;
import com.emc.documentum.xml.dds.operation.exception.CommitFailedException;
import com.emc.documentum.xml.dds.operation.exception.RollbackFailedException;
import com.emc.documentum.xml.dds.persistence.Session;
import com.emc.documentum.xml.dds.persistence.Store;
import com.emc.documentum.xml.dds.persistence.StoreType;
import com.emc.documentum.xml.dds.persistence.StoreUser;
import com.emc.documentum.xml.dds.persistence.xam.internal.CenteraStoreUser;
import com.emc.documentum.xml.dds.persistence.xam.internal.XAMStore;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.snia.xam.ConnectException;
import org.snia.xam.InvalidXRIException;
import org.snia.xam.VIMLoadException;
import org.snia.xam.XAMException;
import org.snia.xam.XAMLibrary;
import org.snia.xam.XAMLibraryObj;
import org.snia.xam.XSystem;

public class XAMSession
implements Session {
    private static final Map<String, File> PEAFILECACHE = new HashMap<String, File>();
    private static final Map<String, XSystem> CONNSTRINGCACHE = new HashMap<String, XSystem>();
    private final XAMStore store;
    private final CenteraStoreUser storeUser;
    private XSystem xSystem;

    public static final void disconnectSessions() {
        for (XSystem xSystem : CONNSTRINGCACHE.values()) {
            try {
                xSystem.close();
            }
            catch (XAMException xe) {
                LogCenter.exception("Could not disconnect properly from XAM XSet : ", (Throwable)xe);
            }
        }
        CONNSTRINGCACHE.clear();
    }

    public XAMSession(CenteraStoreUser storeUser, XAMStore store) {
        this.storeUser = storeUser;
        this.store = store;
    }

    public XSystem getXSystem() {
        return this.xSystem;
    }

    @Override
    public Object getSession() {
        return null;
    }

    @Override
    public StoreUser getUser() {
        return this.storeUser;
    }

    @Override
    public Store getStore() {
        return this.store;
    }

    @Override
    public StoreType getStoreType() {
        return StoreType.XAM;
    }

    @Override
    public void begin() throws BeginFailedException {
        StringBuilder fullConnectionString = new StringBuilder(this.store.getConnectionString());
        if (this.storeUser.getId() != null) {
            fullConnectionString.append("?name=").append(this.storeUser.getId());
            fullConnectionString.append(" & secret=").append(this.storeUser.getPassword());
        } else if (this.storeUser.getPEAFileName() != null) {
            try {
                File tempFile = PEAFILECACHE.get(this.storeUser.getPEAFileName());
                if (tempFile == null) {
                    int b;
                    URL url = Thread.currentThread().getContextClassLoader().getResource(this.storeUser.getPEAFileName());
                    if (url == null) {
                        throw new BeginFailedException("Could not locate configured PEA file, please ensure that it is on the classpath : " + this.storeUser.getPEAFileName());
                    }
                    tempFile = File.createTempFile("centera_", ".pea");
                    tempFile.deleteOnExit();
                    BufferedInputStream bis = new BufferedInputStream(url.openStream());
                    BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(tempFile));
                    while ((b = bis.read()) != -1) {
                        bos.write(b);
                    }
                    bos.close();
                    bis.close();
                    PEAFILECACHE.put(this.storeUser.getPEAFileName(), tempFile);
                }
                fullConnectionString.append("?").append(tempFile.getAbsolutePath());
            }
            catch (IOException ioe) {
                throw new BeginFailedException("Could not connect to the XAM Store, IO error when trying to create PEA file.", ioe);
            }
        } else {
            throw new BeginFailedException("Could not connect to the XAM Store, no authentication data available");
        }
        XSystem xSys = CONNSTRINGCACHE.get(fullConnectionString.toString());
        if (xSys == null) {
            System.out.println("Connecting to Centera with connection string : " + fullConnectionString.toString());
            try {
                XAMLibrary library = XAMLibraryObj.getLibrary();
                if (library.containsField("com.emc.centera.application.name")) {
                    library.setProperty("com.emc.centera.application.name", "DDS - " + DDS.getApplication().getName());
                } else {
                    library.createProperty("com.emc.centera.application.name", false, "DDS - " + DDS.getApplication().getName());
                }
                if (library.containsField("com.emc.centera.application.version")) {
                    library.setProperty("com.emc.centera.application.version", "1.0");
                } else {
                    library.createProperty("com.emc.centera.application.version", false, "1.0");
                }
                this.xSystem = library.connect(fullConnectionString.toString());
                CONNSTRINGCACHE.put(fullConnectionString.toString(), this.xSystem);
            }
            catch (InvalidXRIException ixe) {
                throw new BeginFailedException("Could not connect to the XAM Store, Invalid XRI. Connnection String = " + fullConnectionString.toString(), (Throwable)ixe);
            }
            catch (ConnectException ce) {
                throw new BeginFailedException("Could not connect to the XAM Store, Connection could not be established. Connnection String = " + fullConnectionString.toString(), (Throwable)ce);
            }
            catch (VIMLoadException vle) {
                throw new BeginFailedException("Could not connect to the XAM Store, VIM could not be loaded. Connnection String = " + fullConnectionString.toString(), (Throwable)vle);
            }
            catch (XAMException xe) {
                throw new BeginFailedException("Could not connect to the XAM Store, a XAM Exception occurred. Connnection String = " + fullConnectionString.toString(), (Throwable)xe);
            }
        } else {
            this.xSystem = xSys;
        }
    }

    @Override
    public void commit() throws CommitFailedException {
    }

    @Override
    public void rollback() throws RollbackFailedException {
    }

    @Override
    public boolean isOpen() {
        return this.xSystem != null;
    }

    @Override
    public boolean isReadOnly() {
        return false;
    }
}

