/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  com.emc.documentum.xml.dds.gwt.client.rpc.persistence.SerializableXDBIndex
 *  com.emc.documentum.xml.dds.gwt.server.serialization.internal.XDBNodeSerializer
 *  com.xhive.core.interfaces.XhiveSessionIf
 *  com.xhive.dom.interfaces.XhiveLibraryChildIf
 *  com.xhive.index.interfaces.XhiveIndexIf
 *  com.xhive.index.interfaces.XhiveIndexListIf
 *  com.xhive.util.interfaces.IterableIterator
 */
package com.emc.documentum.xml.dds.operation.library.index.xdb;

import com.emc.documentum.xml.dds.exception.DDSException;
import com.emc.documentum.xml.dds.gwt.client.rpc.persistence.SerializableXDBIndex;
import com.emc.documentum.xml.dds.gwt.server.serialization.internal.XDBNodeSerializer;
import com.emc.documentum.xml.dds.operation.exception.RollbackFailedException;
import com.emc.documentum.xml.dds.operation.exception.RollbackNotAvailableException;
import com.emc.documentum.xml.dds.operation.library.basic.AbstractOperationExecutable;
import com.emc.documentum.xml.dds.operation.library.index.xdb.GetIndexListOperation;
import com.emc.documentum.xml.dds.persistence.Session;
import com.emc.documentum.xml.dds.persistence.StoreChild;
import com.emc.documentum.xml.dds.persistence.XMLNode;
import com.emc.documentum.xml.dds.persistence.xdb.internal.XDBXMLUtil;
import com.xhive.core.interfaces.XhiveSessionIf;
import com.xhive.dom.interfaces.XhiveLibraryChildIf;
import com.xhive.index.interfaces.XhiveIndexIf;
import com.xhive.index.interfaces.XhiveIndexListIf;
import com.xhive.util.interfaces.IterableIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GetIndexListOperationExecutable
extends AbstractOperationExecutable<GetIndexListOperation, List<SerializableXDBIndex>> {
    @Override
    public List<SerializableXDBIndex> run(Map<String, Session> sessionMap) throws DDSException {
        ArrayList<SerializableXDBIndex> result = new ArrayList<SerializableXDBIndex>();
        XhiveLibraryChildIf node = (XhiveLibraryChildIf)XDBXMLUtil.retrieveNode((XhiveSessionIf)sessionMap.get(((GetIndexListOperation)this.getOperation()).getStoreAlias()).getSession(), ((GetIndexListOperation)this.getOperation()).getStoreChild().asXMLNode());
        if (node == null) {
            return result;
        }
        for (XhiveIndexIf index : node.getIndexList().iterator()) {
            SerializableXDBIndex sIndex = XDBNodeSerializer.serialize((String)node.getFullPath(), (XhiveIndexIf)index);
            result.add(sIndex);
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

