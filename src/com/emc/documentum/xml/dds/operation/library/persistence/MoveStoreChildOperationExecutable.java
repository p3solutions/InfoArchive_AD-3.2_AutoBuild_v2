/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.operation.library.persistence;

import com.emc.documentum.xml.dds.exception.DDSException;
import com.emc.documentum.xml.dds.operation.exception.OperationException;
import com.emc.documentum.xml.dds.operation.library.persistence.MoveStoreChildOperation;
import com.emc.documentum.xml.dds.operation.library.persistence.PersistenceOperationExecutable;
import com.emc.documentum.xml.dds.persistence.Container;
import com.emc.documentum.xml.dds.persistence.Location;
import com.emc.documentum.xml.dds.persistence.Session;
import com.emc.documentum.xml.dds.persistence.StoreChild;
import com.emc.documentum.xml.dds.persistence.internal.PersistenceUtil;
import java.util.List;
import java.util.Map;

public class MoveStoreChildOperationExecutable
extends PersistenceOperationExecutable<MoveStoreChildOperation, Object> {
    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    public Object run(Map<String, Session> sessionMap) throws DDSException {
        if (((MoveStoreChildOperation)this.getOperation()).getStoreAliases().size() == 1) {
            Session session = sessionMap.get(((MoveStoreChildOperation)this.getOperation()).getSourceStoreAlias());
            if (((MoveStoreChildOperation)this.getOperation()).getSource().isContainer() && ((MoveStoreChildOperation)this.getOperation()).getTarget().isContainer()) {
                ((Container)((MoveStoreChildOperation)this.getOperation()).getSource()).move(session, (Container)((MoveStoreChildOperation)this.getOperation()).getTarget(), ((MoveStoreChildOperation)this.getOperation()).getReplace());
                return null;
            } else {
                if (!((MoveStoreChildOperation)this.getOperation()).getSource().isLocation() || !((MoveStoreChildOperation)this.getOperation()).getTarget().isLocation()) throw new OperationException("Move source and target should both be Locations or Containers.");
                ((Location)((MoveStoreChildOperation)this.getOperation()).getSource()).move(session, (Location)((MoveStoreChildOperation)this.getOperation()).getTarget(), ((MoveStoreChildOperation)this.getOperation()).getReplace());
            }
            return null;
        } else {
            StoreChild source = ((MoveStoreChildOperation)this.getOperation()).getSource();
            StoreChild target = ((MoveStoreChildOperation)this.getOperation()).getTarget();
            if (source instanceof Location && target instanceof Location) {
                PersistenceUtil.move(sessionMap.get(((MoveStoreChildOperation)this.getOperation()).getSourceStoreAlias()), (Location)source, sessionMap.get(((MoveStoreChildOperation)this.getOperation()).getTargetStoreAlias()), (Location)target, ((MoveStoreChildOperation)this.getOperation()).getReplace());
                return null;
            } else {
                if (!(source instanceof Container) || !(target instanceof Container)) throw new OperationException("Move source and target should both be Locations or Containers.");
                PersistenceUtil.move(sessionMap.get(((MoveStoreChildOperation)this.getOperation()).getSourceStoreAlias()), (Container)source, sessionMap.get(((MoveStoreChildOperation)this.getOperation()).getTargetStoreAlias()), (Container)target, ((MoveStoreChildOperation)this.getOperation()).getReplace());
            }
        }
        return null;
    }
}

