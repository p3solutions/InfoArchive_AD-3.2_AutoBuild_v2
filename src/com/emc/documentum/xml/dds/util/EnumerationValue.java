/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.util;

import java.io.Serializable;

public interface EnumerationValue
extends Serializable {
    public String getXMLValue();

    public String getUserReadable();

    public EnumerationValue reverse(String var1);
}

