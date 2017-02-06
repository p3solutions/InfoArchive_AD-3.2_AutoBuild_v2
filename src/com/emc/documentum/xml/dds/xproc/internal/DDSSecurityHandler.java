/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  com.emc.documentum.xml.xproc.pipeline.model.ExtensionContext
 *  com.emc.documentum.xml.xproc.security.SecurityHandler
 *  com.emc.documentum.xml.xproc.security.SecurityResult
 *  com.emc.documentum.xml.xproc.security.permission.ExecutePermission
 *  com.emc.documentum.xml.xproc.security.permission.Permission
 *  com.emc.documentum.xml.xproc.security.permission.ReadPermission
 *  com.emc.documentum.xml.xproc.security.permission.WritePermission
 */
package com.emc.documentum.xml.dds.xproc.internal;

import com.emc.documentum.xml.xproc.pipeline.model.ExtensionContext;
import com.emc.documentum.xml.xproc.security.SecurityHandler;
import com.emc.documentum.xml.xproc.security.SecurityResult;
import com.emc.documentum.xml.xproc.security.permission.ExecutePermission;
import com.emc.documentum.xml.xproc.security.permission.Permission;
import com.emc.documentum.xml.xproc.security.permission.ReadPermission;
import com.emc.documentum.xml.xproc.security.permission.WritePermission;

public class DDSSecurityHandler
implements SecurityHandler {
    public SecurityResult check(Permission permission, ExtensionContext extensionContext) throws Exception {
        if (permission instanceof ExecutePermission) {
            return SecurityResult.failure((Permission)permission);
        }
        if (permission instanceof ReadPermission) {
            String systemID = ((ReadPermission)permission).getSystemID();
            if (systemID != null && (systemID.startsWith("dds:") || systemID.startsWith("classpath:") || systemID.startsWith("http:") || systemID.startsWith("https:") || systemID.startsWith("transient:"))) {
                return SecurityResult.PASS;
            }
            return SecurityResult.failure((Permission)permission);
        }
        if (permission instanceof WritePermission) {
            String systemID = ((WritePermission)permission).getSystemID();
            if (systemID != null && (systemID.startsWith("dds:") || systemID.startsWith("transient:"))) {
                return SecurityResult.PASS;
            }
            return SecurityResult.failure((Permission)permission);
        }
        return null;
    }
}

