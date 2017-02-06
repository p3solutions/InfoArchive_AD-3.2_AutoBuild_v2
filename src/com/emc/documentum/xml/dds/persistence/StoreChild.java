/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.persistence;

import com.emc.documentum.xml.dds.persistence.Session;
import com.emc.documentum.xml.dds.persistence.Store;
import com.emc.documentum.xml.dds.persistence.StoreType;
import com.emc.documentum.xml.dds.persistence.XMLNode;
import com.emc.documentum.xml.dds.persistence.exception.AmbiguousXPointerException;
import com.emc.documentum.xml.dds.persistence.exception.DeadlockException;
import com.emc.documentum.xml.dds.persistence.exception.IllegalActionException;
import com.emc.documentum.xml.dds.persistence.exception.StoreChildNotFoundException;
import com.emc.documentum.xml.dds.persistence.exception.StoreSpecificException;
import com.emc.documentum.xml.dds.structure.Structure;

public interface StoreChild {
    public String getName();

    public Store getStore();

    public StoreType getStoreType();

    public String getPath();

    public String getCanonicalPath();

    public boolean isLocation();

    public boolean isContainer();

    public boolean isXMLNode();

    public XMLNode asXMLNode();

    public Structure getContext();

    public void setContext(Structure var1);

    public boolean exists(Session var1) throws AmbiguousXPointerException, StoreSpecificException, DeadlockException;

    public void delete(Session var1) throws StoreChildNotFoundException, AmbiguousXPointerException, StoreSpecificException, DeadlockException, IllegalActionException;
}

