/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  com.xhive.core.interfaces.XhiveDatabaseIf
 *  com.xhive.core.interfaces.XhiveSessionIf
 *  com.xhive.dom.interfaces.XhiveLibraryIf
 *  com.xhive.dom.interfaces.XhiveNodeIf
 *  com.xhive.query.interfaces.XQueryResolverIf
 *  com.xhive.query.interfaces.XhiveXQueryQueryIf
 *  com.xhive.query.interfaces.XhiveXQueryResultIf
 */
package com.emc.documentum.xml.dds.xquery.xdb;

import com.emc.documentum.xml.dds.exception.DDSException;
import com.emc.documentum.xml.dds.persistence.Session;
import com.emc.documentum.xml.dds.persistence.XMLNode;
import com.emc.documentum.xml.dds.persistence.xdb.internal.XDBXMLUtil;
import com.emc.documentum.xml.dds.xquery.XQueryExecutor;
import com.emc.documentum.xml.dds.xquery.XQueryResultHandler;
import com.emc.documentum.xml.dds.xquery.xdb.internal.DDSXQueryUtils;
import com.xhive.core.interfaces.XhiveDatabaseIf;
import com.xhive.core.interfaces.XhiveSessionIf;
import com.xhive.dom.interfaces.XhiveLibraryIf;
import com.xhive.dom.interfaces.XhiveNodeIf;
import com.xhive.query.interfaces.XQueryResolverIf;
import com.xhive.query.interfaces.XhiveXQueryQueryIf;
import com.xhive.query.interfaces.XhiveXQueryResultIf;
import java.util.Map;

public class XDBXQueryExecutor
implements XQueryExecutor {
    @Override
    public Object execute(Session session, String query, XQueryResultHandler resultHandler) throws DDSException {
        return this.execute(session, query, null, null, resultHandler);
    }

    @Override
    public Object execute(Session session, String query, Map<String, String> variables, XQueryResultHandler resultHandler) throws DDSException {
        return this.execute(session, query, null, variables, resultHandler);
    }

    @Override
    public Object executeOn(Session session, String query, XMLNode xmlNode, XQueryResultHandler resultHandler) throws DDSException {
        return this.executeOn(session, query, xmlNode, null, resultHandler);
    }

    @Override
    public Object executeOn(Session session, String query, XMLNode xmlNode, Map<String, String> variables, XQueryResultHandler resultHandler) throws DDSException {
        return this.execute(session, query, xmlNode, variables, resultHandler);
    }

    private Object execute(Session session, String query, XMLNode context, Map<String, String> variables, XQueryResultHandler resultHandler) throws DDSException {
        XhiveSessionIf xSession = (XhiveSessionIf)session.getSession();
        try {
            XhiveLibraryIf contextNode = (XhiveLibraryIf) (context == null ? xSession.getDatabase().getRoot() : XDBXMLUtil.retrieveNode(xSession, context));
            XQueryResolverIf xqueryResolver = DDSXQueryUtils.getDefaultDDSXQueryResolver(null, session, null);
            XhiveXQueryQueryIf xqueryQuery = DDSXQueryUtils.newXQuery(xSession, query, variables, xqueryResolver);
            XhiveXQueryResultIf results = xqueryQuery.executeOn((XhiveNodeIf)contextNode);
            return resultHandler == null ? results : resultHandler.transformXQueryResult((Object)results);
        }
        catch (Exception e) {
            if (e instanceof DDSException) {
                throw (DDSException)e;
            }
            throw new DDSException(e);
        }
    }
}

