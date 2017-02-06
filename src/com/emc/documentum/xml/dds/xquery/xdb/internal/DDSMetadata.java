/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  com.xhive.dom.interfaces.XhiveNodeIf
 *  com.xhive.query.interfaces.XhiveXQueryExtensionFunctionIf
 *  com.xhive.query.interfaces.XhiveXQueryValueIf
 */
package com.emc.documentum.xml.dds.xquery.xdb.internal;

import com.emc.documentum.xml.dds.DDS;
import com.emc.documentum.xml.dds.xquery.xdb.internal.DDSXQueryUtils;
import com.xhive.dom.interfaces.XhiveNodeIf;
import com.xhive.query.interfaces.XhiveXQueryExtensionFunctionIf;
import com.xhive.query.interfaces.XhiveXQueryValueIf;
import java.util.Iterator;
import org.w3c.dom.Node;

public class DDSMetadata
implements XhiveXQueryExtensionFunctionIf {
    public Object[] call(Iterator<? extends XhiveXQueryValueIf>[] arg) {
        Iterator<? extends XhiveXQueryValueIf> context = arg[0];
        if (context != null && context.hasNext()) {
            XhiveNodeIf node = context.next().asNode();
            Iterator<? extends XhiveXQueryValueIf> scheme = arg[1];
            if (scheme.hasNext()) {
                String value = scheme.next().asString();
                return new Object[]{DDSXQueryUtils.getMetadata(DDS.getApplication(), (Node)node, value)};
            }
        }
        return null;
    }
}

