/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.persistence.registry;

import com.emc.documentum.xml.dds.exception.DDSException;
import com.emc.documentum.xml.dds.persistence.Container;
import com.emc.documentum.xml.dds.persistence.Location;
import com.emc.documentum.xml.dds.persistence.StoreChild;
import com.emc.documentum.xml.dds.persistence.exception.DeadlockException;
import com.emc.documentum.xml.dds.persistence.exception.IllegalActionException;
import com.emc.documentum.xml.dds.persistence.exception.LocationNotFoundException;
import com.emc.documentum.xml.dds.persistence.exception.StoreSpecificException;
import java.util.List;
import java.util.Map;

public interface ContentRegistry {
    public boolean existsLocation(Location var1) throws DDSException;

    public void createLocation(Location var1, Map<String, String> var2, boolean var3) throws DDSException;

    public void deleteLocation(Location var1) throws LocationNotFoundException, IllegalActionException, DeadlockException, StoreSpecificException;

    public void moveLocation(Location var1, Location var2) throws DDSException;

    public List<StoreChild> listChildren(Location var1, boolean var2, boolean var3) throws LocationNotFoundException, DeadlockException, StoreSpecificException;

    public String getAttribute(Location var1, String var2) throws DDSException;

    public Map<String, String> getAttributes(Location var1, List<String> var2) throws DDSException;

    public boolean existsContainer(Container var1) throws DDSException;

    public void createContainer(Container var1, Map<String, String> var2, boolean var3) throws DDSException;

    public void deleteContainer(Container var1) throws DDSException;

    public void moveContainer(Container var1, Container var2) throws DDSException;

    public String getAttribute(Container var1, String var2) throws DDSException;

    public Map<String, String> getAttributes(Container var1, List<String> var2) throws DDSException;
}

