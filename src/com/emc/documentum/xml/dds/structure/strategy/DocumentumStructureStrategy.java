/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.structure.strategy;

import com.emc.documentum.xml.dds.structure.StructureStrategy;
import com.emc.documentum.xml.dds.structure.StructureStrategyType;
import com.emc.documentum.xml.dds.structure.strategy.DDSStructureStrategyType;

public class DocumentumStructureStrategy
implements StructureStrategy {
    @Override
    public StructureStrategyType getType() {
        return DDSStructureStrategyType.DOCUMENTUM;
    }
}

