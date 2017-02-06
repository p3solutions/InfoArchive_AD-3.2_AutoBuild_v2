/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.uri;

import com.emc.documentum.xml.dds.application.Application;
import com.emc.documentum.xml.dds.persistence.Container;
import com.emc.documentum.xml.dds.persistence.Location;
import com.emc.documentum.xml.dds.persistence.StoreChild;
import com.emc.documentum.xml.dds.uri.DDSURI;
import com.emc.documentum.xml.dds.uri.URITarget;
import com.emc.documentum.xml.dds.uri.exception.DDSURIException;
import com.emc.documentum.xml.dds.user.User;

public interface URIResolver {
    public void setApplication(Application var1);

    public URITarget resolveURI(DDSURI var1, User var2) throws DDSURIException;

    public URITarget resolveURI(String var1, User var2) throws DDSURIException;

    public DDSURI generateURI(StoreChild var1) throws DDSURIException;

    public DDSURI generateURI(Location var1, Container var2) throws DDSURIException;
}

