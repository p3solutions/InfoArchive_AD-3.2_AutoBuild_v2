/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.operation.library.xmlpersistence;

import com.emc.documentum.xml.dds.exception.DDSException;
import com.emc.documentum.xml.dds.operation.framework.NodeTransformer;
import com.emc.documentum.xml.dds.operation.library.basic.WrapperOperationExecutable;
import com.emc.documentum.xml.dds.operation.library.xmlpersistence.TransformNodesOperation;
import com.emc.documentum.xml.dds.persistence.Session;
import com.emc.documentum.xml.dds.persistence.XMLNode;
import java.util.List;
import java.util.Map;

public class TransformNodesOperationExecutable
extends WrapperOperationExecutable {
    @Override
    public Object run(Map<String, Session> sessionMap) throws DDSException {
        Object result = super.run(sessionMap);
        if (result instanceof XMLNode) {
            return ((TransformNodesOperation)this.getOperation()).getNodeTransformer().transformNode((XMLNode)result);
        }
        if (result instanceof List) {
            return ((TransformNodesOperation)this.getOperation()).getNodeTransformer().transformNodes((List)result);
        }
        return null;
    }
}

