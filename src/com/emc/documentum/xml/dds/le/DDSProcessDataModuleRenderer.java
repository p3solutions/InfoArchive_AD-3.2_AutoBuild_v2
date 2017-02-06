/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  com.emc.documentum.xml.le.engine.resolver.DataModuleRef
 */
package com.emc.documentum.xml.dds.le;

import com.emc.documentum.xml.dds.le.exception.DDSProcessDataModuleRendererException;
import com.emc.documentum.xml.dds.persistence.Session;
import com.emc.documentum.xml.dds.util.SessionPool;
import com.emc.documentum.xml.le.engine.resolver.DataModuleRef;
import java.io.OutputStream;
import java.util.Collection;

public interface DDSProcessDataModuleRenderer {
    public void render(DataModuleRef var1, OutputStream var2, OutputType var3) throws DDSProcessDataModuleRendererException;

    public SessionPool getSessionPool();

    public Collection<Session> getOpenSessions();

    public static enum OutputType {
        PDF;
        

        private OutputType() {
        }
    }

}

