/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  com.xhive.core.interfaces.XhiveSessionIf
 *  com.xhive.dom.interfaces.XhiveLibraryChildIf
 *  com.xhive.index.interfaces.XhiveIndexIf
 *  com.xhive.index.interfaces.XhiveIndexListIf
 */
package com.emc.documentum.xml.dds.operation.library.index.xdb;

import com.emc.documentum.xml.dds.exception.DDSException;
import com.emc.documentum.xml.dds.operation.exception.RollbackFailedException;
import com.emc.documentum.xml.dds.operation.exception.RollbackNotAvailableException;
import com.emc.documentum.xml.dds.operation.library.basic.AbstractOperationExecutable;
import com.emc.documentum.xml.dds.operation.library.index.xdb.RemoveIndexesOperation;
import com.emc.documentum.xml.dds.persistence.Session;
import com.emc.documentum.xml.dds.persistence.XMLNode;
import com.emc.documentum.xml.dds.persistence.xdb.internal.XDBXMLUtil;
import com.xhive.core.interfaces.XhiveSessionIf;
import com.xhive.dom.interfaces.XhiveLibraryChildIf;
import com.xhive.index.interfaces.XhiveIndexIf;
import com.xhive.index.interfaces.XhiveIndexListIf;
import java.util.List;
import java.util.Map;

public class RemoveIndexesOperationExecutable
extends AbstractOperationExecutable<RemoveIndexesOperation, Object> {
    @Override
    public Object run(Map<String, Session> sessionMap) throws DDSException {
        XhiveSessionIf session = (XhiveSessionIf)sessionMap.get(((RemoveIndexesOperation)this.getOperation()).getStoreAlias()).getSession();
        XMLNode xmlNode = ((RemoveIndexesOperation)this.getOperation()).getXMLNode();
        XhiveLibraryChildIf node = (XhiveLibraryChildIf)XDBXMLUtil.retrieveNode(session, xmlNode);
        XhiveIndexListIf indexList = node.getIndexList();
        for (String indexName : ((RemoveIndexesOperation)this.getOperation()).getIndexNames()) {
            XhiveIndexIf index = indexList.getIndex(indexName);
            if (index == null) continue;
            indexList.removeIndex(index);
        }
        return null;
    }

    @Override
    public boolean canRollback() {
        return false;
    }

    @Override
    public void rollback() throws RollbackNotAvailableException, RollbackFailedException {
    }
}

