/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.fs.search.metadata;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

@XmlType(name="LogicalOperator", namespace="http://metadata.search.datamodel.dds.xml.documentum.emc.com/", propOrder={"AND", "OR"})
@XmlEnum
public enum LogicalOperator {
    AND,
    OR;
    

    private LogicalOperator() {
    }
}

