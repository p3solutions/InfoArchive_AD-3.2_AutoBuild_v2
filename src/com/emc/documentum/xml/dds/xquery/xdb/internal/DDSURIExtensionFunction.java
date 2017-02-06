/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  com.xhive.dom.interfaces.XhiveNodeIf
 *  com.xhive.query.interfaces.XhiveXQueryExtensionFunctionIf
 *  com.xhive.query.interfaces.XhiveXQueryValueIf
 */
package com.emc.documentum.xml.dds.xquery.xdb.internal;

import com.emc.documentum.xml.dds.application.Application;
import com.emc.documentum.xml.dds.xquery.xdb.internal.DDSXQueryUtils;
import com.xhive.dom.interfaces.XhiveNodeIf;
import com.xhive.query.interfaces.XhiveXQueryExtensionFunctionIf;
import com.xhive.query.interfaces.XhiveXQueryValueIf;
import java.util.Iterator;
import org.w3c.dom.Node;

public class DDSURIExtensionFunction
implements XhiveXQueryExtensionFunctionIf {
    private final Application application;

    public DDSURIExtensionFunction(Application application) {
        this.application = application;
    }

    public Object[] call(Iterator<? extends XhiveXQueryValueIf>[] args) {
        Iterator<? extends XhiveXQueryValueIf> it;
        if (args != null && args.length == 1 && (it = args[0]).hasNext()) {
            XhiveXQueryValueIf value = it.next();
            XhiveNodeIf node = value.asNode();
            return new String[]{DDSXQueryUtils.generateURI(this.application, (Node)node)};
        }
        return new String[0];
    }
}

