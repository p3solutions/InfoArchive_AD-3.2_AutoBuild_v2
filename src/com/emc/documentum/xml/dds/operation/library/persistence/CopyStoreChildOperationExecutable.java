/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.operation.library.persistence;

import com.emc.documentum.xml.dds.exception.DDSException;
import com.emc.documentum.xml.dds.operation.exception.OperationException;
import com.emc.documentum.xml.dds.operation.library.persistence.CopyStoreChildOperation;
import com.emc.documentum.xml.dds.operation.library.persistence.PersistenceOperationExecutable;
import com.emc.documentum.xml.dds.persistence.Container;
import com.emc.documentum.xml.dds.persistence.Location;
import com.emc.documentum.xml.dds.persistence.Session;
import com.emc.documentum.xml.dds.persistence.StoreChild;
import com.emc.documentum.xml.dds.persistence.internal.PersistenceUtil;
import java.util.List;
import java.util.Map;

public class CopyStoreChildOperationExecutable
extends PersistenceOperationExecutable<CopyStoreChildOperation, Object> {
    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    public Object run(Map<String, Session> sessionMap) throws DDSException {
        if (((CopyStoreChildOperation)this.getOperation()).getStoreAliases().size() == 1) {
            Session session = sessionMap.get(((CopyStoreChildOperation)this.getOperation()).getSourceStoreAlias());
            if (((CopyStoreChildOperation)this.getOperation()).getSource().isContainer() && ((CopyStoreChildOperation)this.getOperation()).getTarget().isContainer()) {
                ((Container)((CopyStoreChildOperation)this.getOperation()).getSource()).copy(session, (Container)((CopyStoreChildOperation)this.getOperation()).getTarget(), ((CopyStoreChildOperation)this.getOperation()).getReplace());
                return null;
            } else {
                if (!((CopyStoreChildOperation)this.getOperation()).getSource().isLocation() || !((CopyStoreChildOperation)this.getOperation()).getTarget().isLocation()) throw new OperationException("Copy source and target should both be Locations or Containers.");
                ((Location)((CopyStoreChildOperation)this.getOperation()).getSource()).copy(session, (Location)((CopyStoreChildOperation)this.getOperation()).getTarget(), ((CopyStoreChildOperation)this.getOperation()).getReplace());
            }
            return null;
        } else {
            StoreChild source = ((CopyStoreChildOperation)this.getOperation()).getSource();
            StoreChild target = ((CopyStoreChildOperation)this.getOperation()).getTarget();
            if (source instanceof Location && target instanceof Location) {
                PersistenceUtil.copy(sessionMap.get(((CopyStoreChildOperation)this.getOperation()).getSourceStoreAlias()), (Location)source, sessionMap.get(((CopyStoreChildOperation)this.getOperation()).getTargetStoreAlias()), (Location)target, ((CopyStoreChildOperation)this.getOperation()).getReplace());
                return null;
            } else {
                if (!(source instanceof Container) || !(target instanceof Container)) throw new OperationException("Copy source and target should both be Locations or Containers.");
                PersistenceUtil.copy(sessionMap.get(((CopyStoreChildOperation)this.getOperation()).getSourceStoreAlias()), (Container)source, sessionMap.get(((CopyStoreChildOperation)this.getOperation()).getTargetStoreAlias()), (Container)target, ((CopyStoreChildOperation)this.getOperation()).getReplace());
            }
        }
        return null;
    }
}

