/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.operation.library.xmlpersistence;

import com.emc.documentum.xml.dds.operation.framework.NodeTransformer;
import com.emc.documentum.xml.dds.operation.framework.Operation;
import com.emc.documentum.xml.dds.operation.library.basic.WrapperOperation;
import com.emc.documentum.xml.dds.operation.library.xmlpersistence.TransformNodesOperationExecutable;

public class TransformNodesOperation
extends WrapperOperation {
    private final NodeTransformer<?> nodeTransformer;

    public TransformNodesOperation(Operation<?> operation, NodeTransformer<?> nodeTransformer) {
        super(operation);
        this.nodeTransformer = nodeTransformer;
    }

    public NodeTransformer<?> getNodeTransformer() {
        return this.nodeTransformer;
    }

    @Override
    public String getExecutableClassName() {
        return TransformNodesOperationExecutable.class.getName();
    }
}

