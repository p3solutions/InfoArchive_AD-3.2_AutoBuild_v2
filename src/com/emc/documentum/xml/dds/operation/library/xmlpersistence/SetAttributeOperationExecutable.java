/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.operation.library.xmlpersistence;

import com.emc.documentum.xml.dds.exception.DDSException;
import com.emc.documentum.xml.dds.operation.library.xmlpersistence.SetAttributeOperation;
import com.emc.documentum.xml.dds.operation.library.xmlpersistence.SingleNodeOperationExecutable;
import com.emc.documentum.xml.dds.persistence.Session;
import com.emc.documentum.xml.dds.persistence.XMLNode;
import java.util.Map;

public class SetAttributeOperationExecutable
extends SingleNodeOperationExecutable<SetAttributeOperation, Object> {
    @Override
    public Object run(Map<String, Session> sessionMap) throws DDSException {
        ((SetAttributeOperation)this.getOperation()).getXMLNode().setAttribute(sessionMap.get(((SetAttributeOperation)this.getOperation()).getStoreAlias()), ((SetAttributeOperation)this.getOperation()).getName(), ((SetAttributeOperation)this.getOperation()).getValue());
        return null;
    }
}

