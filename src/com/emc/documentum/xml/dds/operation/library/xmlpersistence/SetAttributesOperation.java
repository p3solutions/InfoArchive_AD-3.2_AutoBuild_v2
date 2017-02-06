/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.operation.library.xmlpersistence;

import com.emc.documentum.xml.dds.operation.exception.OperationException;
import com.emc.documentum.xml.dds.operation.library.xmlpersistence.SetAttributesOperationExecutable;
import com.emc.documentum.xml.dds.operation.library.xmlpersistence.SingleNodeOperation;
import com.emc.documentum.xml.dds.persistence.XMLNode;
import java.util.Map;

public class SetAttributesOperation
extends SingleNodeOperation<Object> {
    private final Map<String, String> attributes;

    public SetAttributesOperation(XMLNode xmlNode, Map<String, String> attributes) throws OperationException {
        super(xmlNode);
        this.attributes = attributes;
    }

    public Map<String, String> getAttributes() {
        return this.attributes;
    }

    @Override
    public String getExecutableClassName() {
        return SetAttributesOperationExecutable.class.getName();
    }

    @Override
    public boolean isReadOnly(String storeAlias) {
        return false;
    }
}

