/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.fs.search.metadata;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

@XmlType(name="FieldOperator", namespace="http://metadata.search.datamodel.dds.xml.documentum.emc.com/", propOrder={"EQ", "NE", "GT", "GE", "LT", "LE", "FTSE"})
@XmlEnum
public enum FieldOperator {
    EQ,
    NE,
    GT,
    GE,
    LT,
    LE,
    FTSE;
    

    private FieldOperator() {
    }
}

