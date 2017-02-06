/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  com.emc.documentum.xml.xproc.pipeline.model.PipelineInput
 *  com.xhive.core.interfaces.XhiveSessionIf
 *  com.xhive.core.interfaces.XhiveUserIf
 *  com.xhive.core.interfaces.XhiveUserListIf
 */
package com.emc.documentum.xml.dds.persistence.xdb.internal;

import com.emc.documentum.xml.dds.configuration.persistence.xdb.XDBStoreUserConfiguration;
import com.emc.documentum.xml.dds.persistence.StoreType;
import com.emc.documentum.xml.dds.persistence.StoreUser;
import com.emc.documentum.xml.dds.util.internal.StringUtils;
import com.emc.documentum.xml.xproc.pipeline.model.PipelineInput;
import com.xhive.core.interfaces.XhiveSessionIf;
import com.xhive.core.interfaces.XhiveUserIf;
import com.xhive.core.interfaces.XhiveUserListIf;
import javax.xml.namespace.QName;

public final class XDBStoreUser
implements StoreUser {
    private final String id;
    private final String password;

    public XDBStoreUser(XDBStoreUserConfiguration configuration) {
        this.id = configuration.getId();
        this.password = configuration.getEncryptedPassword() != null ? StringUtils.decrypt(configuration.getEncryptedPassword()) : null;
    }

    public String getId() {
        return this.id;
    }

    @Override
    public StoreType getStoreType() {
        return StoreType.XDB;
    }

    protected void connect(XhiveSessionIf session, String databaseName) {
        session.connect(this.id, this.password, databaseName);
    }

    protected void addSelfToXDBUserList(XhiveUserListIf userList) {
        userList.addUser(this.id, this.password);
    }

    public void addSelfToPipelineInput(PipelineInput input) {
        input.addParameter("properties", new QName("xhive.username"), this.id);
        input.addParameter("properties", new QName("xhive.password"), this.password);
    }
}

