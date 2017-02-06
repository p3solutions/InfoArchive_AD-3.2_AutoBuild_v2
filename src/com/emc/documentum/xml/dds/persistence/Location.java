/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.persistence;

import com.emc.documentum.xml.dds.persistence.Container;
import com.emc.documentum.xml.dds.persistence.LocationOptions;
import com.emc.documentum.xml.dds.persistence.Session;
import com.emc.documentum.xml.dds.persistence.StoreChild;
import com.emc.documentum.xml.dds.persistence.exception.DeadlockException;
import com.emc.documentum.xml.dds.persistence.exception.IllegalActionException;
import com.emc.documentum.xml.dds.persistence.exception.LocationAlreadyExistsException;
import com.emc.documentum.xml.dds.persistence.exception.LocationNotFoundException;
import com.emc.documentum.xml.dds.persistence.exception.StoreSpecificException;
import com.emc.documentum.xml.dds.persistence.exception.TypeConflictException;
import java.util.Collection;
import java.util.List;

public interface Location
extends StoreChild {
    public Location getParent();

    public Location getChildLocation(String var1);

    public Container getChildContainer(String var1);

    public Location getDescendantLocation(String var1);

    public Location getDescendantLocation(List<String> var1);

    public Container getDescendantContainer(String var1, String var2);

    public String getPath(boolean var1);

    public List<String> getPathComponents();

    public boolean isRoot();

    public Location deepCopy();

    @Override
    public boolean exists(Session var1) throws StoreSpecificException, DeadlockException;

    @Override
    public void delete(Session var1) throws LocationNotFoundException, StoreSpecificException, DeadlockException, IllegalActionException;

    public Collection<StoreChild> listChildren(Session var1, boolean var2, boolean var3, boolean var4) throws LocationNotFoundException, StoreSpecificException, DeadlockException;

    public void create(Session var1, LocationOptions var2, boolean var3) throws LocationNotFoundException, LocationAlreadyExistsException, TypeConflictException, StoreSpecificException, DeadlockException;

    public LocationOptions getOptions(Session var1) throws LocationNotFoundException, StoreSpecificException, DeadlockException;

    public void move(Session var1, Location var2, boolean var3) throws LocationNotFoundException, LocationAlreadyExistsException, IllegalActionException, TypeConflictException, StoreSpecificException, DeadlockException;

    public void copy(Session var1, Location var2, boolean var3) throws LocationNotFoundException, LocationAlreadyExistsException, IllegalActionException, TypeConflictException, StoreSpecificException, DeadlockException;
}

