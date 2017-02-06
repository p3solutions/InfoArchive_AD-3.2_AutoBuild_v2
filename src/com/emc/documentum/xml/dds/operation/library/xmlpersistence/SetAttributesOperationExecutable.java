/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.operation.library.xmlpersistence;

import com.emc.documentum.xml.dds.exception.DDSException;
import com.emc.documentum.xml.dds.operation.library.xmlpersistence.SetAttributesOperation;
import com.emc.documentum.xml.dds.operation.library.xmlpersistence.SingleNodeOperationExecutable;
import com.emc.documentum.xml.dds.persistence.Session;
import com.emc.documentum.xml.dds.persistence.XMLNode;
import java.util.Map;

public class SetAttributesOperationExecutable
extends SingleNodeOperationExecutable<SetAttributesOperation, Object> {
    @Override
    public Object run(Map<String, Session> sessionMap) throws DDSException {
        ((SetAttributesOperation)this.getOperation()).getXMLNode().setAttributes(sessionMap.get(((SetAttributesOperation)this.getOperation()).getStoreAlias()), ((SetAttributesOperation)this.getOperation()).getAttributes());
        return null;
    }
}

