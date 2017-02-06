/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.operation.framework;

import com.emc.documentum.xml.dds.persistence.XMLNode;
import java.util.List;

public interface NodeTransformer<T> {
    public T transformNode(XMLNode var1);

    public List<T> transformNodes(List<XMLNode> var1);
}

