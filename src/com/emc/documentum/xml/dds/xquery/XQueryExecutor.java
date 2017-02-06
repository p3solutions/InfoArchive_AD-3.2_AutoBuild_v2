/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.xquery;

import com.emc.documentum.xml.dds.exception.DDSException;
import com.emc.documentum.xml.dds.persistence.Session;
import com.emc.documentum.xml.dds.persistence.XMLNode;
import com.emc.documentum.xml.dds.xquery.XQueryResultHandler;
import java.util.Map;

public interface XQueryExecutor {
    public Object execute(Session var1, String var2, XQueryResultHandler var3) throws DDSException;

    public Object execute(Session var1, String var2, Map<String, String> var3, XQueryResultHandler var4) throws DDSException;

    public Object executeOn(Session var1, String var2, XMLNode var3, XQueryResultHandler var4) throws DDSException;

    public Object executeOn(Session var1, String var2, XMLNode var3, Map<String, String> var4, XQueryResultHandler var5) throws DDSException;
}

