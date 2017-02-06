/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  com.xhive.core.interfaces.XhiveSessionIf
 *  com.xhive.dom.interfaces.XhiveLibraryChildIf
 *  com.xhive.index.interfaces.XhiveIndexIf
 *  com.xhive.index.interfaces.XhiveIndexKeyIteratorIf
 *  com.xhive.index.interfaces.XhiveIndexListIf
 */
package com.emc.documentum.xml.dds.operation.library.index.xdb;

import com.emc.documentum.xml.dds.exception.DDSException;
import com.emc.documentum.xml.dds.operation.exception.RollbackFailedException;
import com.emc.documentum.xml.dds.operation.exception.RollbackNotAvailableException;
import com.emc.documentum.xml.dds.operation.library.basic.AbstractOperationExecutable;
import com.emc.documentum.xml.dds.operation.library.index.xdb.GetKeysOperation;
import com.emc.documentum.xml.dds.persistence.Session;
import com.emc.documentum.xml.dds.persistence.XMLNode;
import com.emc.documentum.xml.dds.persistence.xdb.internal.XDBXMLUtil;
import com.xhive.core.interfaces.XhiveSessionIf;
import com.xhive.dom.interfaces.XhiveLibraryChildIf;
import com.xhive.index.interfaces.XhiveIndexIf;
import com.xhive.index.interfaces.XhiveIndexKeyIteratorIf;
import com.xhive.index.interfaces.XhiveIndexListIf;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GetKeysOperationExecutable
extends AbstractOperationExecutable<GetKeysOperation, List<String>> {
    @Override
    public List<String> run(Map<String, Session> sessionMap) throws DDSException {
        XMLNode xmlNode;
        ArrayList<String> result = new ArrayList<String>();
        XhiveSessionIf session = (XhiveSessionIf)sessionMap.get(((GetKeysOperation)this.getOperation()).getStoreAlias()).getSession();
        XhiveLibraryChildIf node = (XhiveLibraryChildIf)XDBXMLUtil.retrieveNode(session, xmlNode = ((GetKeysOperation)this.getOperation()).getXMLNode());
        if (node == null) {
            return result;
        }
        XhiveIndexListIf indexList = node.getIndexList();
        XhiveIndexIf index = indexList.getIndex(((GetKeysOperation)this.getOperation()).getIndexName());
        if (index != null) {
            for (Object obj : index.getKeys()) {
                result.add(String.valueOf(obj));
            }
        }
        return result;
    }

    @Override
    public boolean canRollback() {
        return false;
    }

    @Override
    public void rollback() throws RollbackNotAvailableException, RollbackFailedException {
    }
}

