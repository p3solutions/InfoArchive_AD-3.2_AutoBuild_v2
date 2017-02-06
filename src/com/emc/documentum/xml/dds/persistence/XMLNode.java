/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.persistence;

import com.emc.documentum.xml.dds.persistence.Container;
import com.emc.documentum.xml.dds.persistence.Location;
import com.emc.documentum.xml.dds.persistence.Session;
import com.emc.documentum.xml.dds.persistence.StoreChild;
import com.emc.documentum.xml.dds.persistence.exception.AmbiguousXPointerException;
import com.emc.documentum.xml.dds.persistence.exception.DeadlockException;
import com.emc.documentum.xml.dds.persistence.exception.NodeNotFoundException;
import com.emc.documentum.xml.dds.persistence.exception.StoreSpecificException;
import java.util.List;
import java.util.Map;
import org.w3c.dom.Node;

public interface XMLNode
extends StoreChild {
    public Container getContainer();

    public String getXPointer();

    public Node asNode();

    public int getChildCount(Session var1) throws NodeNotFoundException, AmbiguousXPointerException, DeadlockException, StoreSpecificException;

    public List<XMLNode> getChildren(Session var1) throws NodeNotFoundException, AmbiguousXPointerException, DeadlockException, StoreSpecificException;

    public List<XMLNode> getChildren(Session var1, short var2) throws NodeNotFoundException, AmbiguousXPointerException, DeadlockException, StoreSpecificException;

    public List<XMLNode> getChildrenRange(Session var1, int var2, int var3) throws NodeNotFoundException, AmbiguousXPointerException, DeadlockException, StoreSpecificException;

    public XMLNode getNode(Session var1) throws NodeNotFoundException, AmbiguousXPointerException, DeadlockException, StoreSpecificException;

    public XMLNode insert(Session var1, String var2, boolean var3) throws NodeNotFoundException, AmbiguousXPointerException, DeadlockException, StoreSpecificException;

    public XMLNode move(Session var1, XMLNode var2, boolean var3) throws NodeNotFoundException, AmbiguousXPointerException, DeadlockException, StoreSpecificException;

    public XMLNode copy(Session var1, XMLNode var2, boolean var3) throws NodeNotFoundException, AmbiguousXPointerException, DeadlockException, StoreSpecificException;

    public void setAttribute(Session var1, String var2, String var3) throws NodeNotFoundException, AmbiguousXPointerException, DeadlockException, StoreSpecificException;

    public void setAttributes(Session var1, Map<String, String> var2) throws NodeNotFoundException, AmbiguousXPointerException, DeadlockException, StoreSpecificException;

    public boolean representsLocation();

    public boolean representsContainer();

    public boolean representsNode();

    public Location asLocation();

    public Container asContainer();
}

