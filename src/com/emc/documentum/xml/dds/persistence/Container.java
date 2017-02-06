/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.persistence;

import com.emc.documentum.xml.dds.persistence.ContentDescriptor;
import com.emc.documentum.xml.dds.persistence.Data;
import com.emc.documentum.xml.dds.persistence.Location;
import com.emc.documentum.xml.dds.persistence.Metadata;
import com.emc.documentum.xml.dds.persistence.MetadataScheme;
import com.emc.documentum.xml.dds.persistence.Session;
import com.emc.documentum.xml.dds.persistence.StoreChild;
import com.emc.documentum.xml.dds.persistence.XMLNode;
import com.emc.documentum.xml.dds.persistence.exception.ContainerAlreadyExistsException;
import com.emc.documentum.xml.dds.persistence.exception.ContainerNotFoundException;
import com.emc.documentum.xml.dds.persistence.exception.DeadlockException;
import com.emc.documentum.xml.dds.persistence.exception.IllegalActionException;
import com.emc.documentum.xml.dds.persistence.exception.InvalidContentException;
import com.emc.documentum.xml.dds.persistence.exception.LocationNotFoundException;
import com.emc.documentum.xml.dds.persistence.exception.SchemeNotSupportedException;
import com.emc.documentum.xml.dds.persistence.exception.StoreSpecificException;
import com.emc.documentum.xml.dds.persistence.exception.TypeConflictException;
import com.emc.documentum.xml.dds.serialization.exception.SerializationException;

public interface Container
extends StoreChild {
    public Location getLocation();

    public XMLNode getXMLNode(String var1);

    @Override
    public boolean exists(Session var1) throws StoreSpecificException, DeadlockException;

    @Override
    public void delete(Session var1) throws ContainerNotFoundException, IllegalActionException, StoreSpecificException, DeadlockException;

    public void persist(Session var1, ContentDescriptor var2, Data<?> var3, boolean var4) throws LocationNotFoundException, ContainerAlreadyExistsException, SerializationException, StoreSpecificException, TypeConflictException, DeadlockException;

    public Data<?> retrieve(Session var1, ContentDescriptor var2) throws ContainerNotFoundException, InvalidContentException, TypeConflictException, StoreSpecificException, DeadlockException;

    public Metadata getMetadata(Session var1, MetadataScheme var2) throws SchemeNotSupportedException, ContainerNotFoundException, StoreSpecificException, DeadlockException;

    public void setMetadata(Session var1, Metadata var2) throws SchemeNotSupportedException, ContainerNotFoundException, TypeConflictException, StoreSpecificException, DeadlockException;

    public void move(Session var1, Container var2, boolean var3) throws LocationNotFoundException, ContainerNotFoundException, ContainerAlreadyExistsException, TypeConflictException, IllegalActionException, StoreSpecificException, DeadlockException;

    public void copy(Session var1, Container var2, boolean var3) throws LocationNotFoundException, ContainerNotFoundException, ContainerAlreadyExistsException, TypeConflictException, IllegalActionException, StoreSpecificException, DeadlockException;
}

