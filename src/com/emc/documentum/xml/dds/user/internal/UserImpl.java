/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.user.internal;

import com.emc.documentum.xml.dds.configuration.persistence.StoreUserConfiguration;
import com.emc.documentum.xml.dds.configuration.user.UserConfiguration;
import com.emc.documentum.xml.dds.persistence.StoreUser;
import com.emc.documentum.xml.dds.persistence.StoreUserFactory;
import com.emc.documentum.xml.dds.user.User;
import com.emc.documentum.xml.dds.util.internal.StringUtils;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class UserImpl
implements User {
    private final String id;
    private String password;
    private boolean administrator;
    private final Map<String, StoreUser> users = new HashMap<String, StoreUser>();

    public UserImpl(UserConfiguration configuration) {
        this.id = configuration.getId();
        if (configuration.getEncryptedPassword() != null) {
            this.password = StringUtils.decrypt(configuration.getEncryptedPassword());
        }
        this.administrator = configuration.isAdministrator();
        for (Map.Entry<String, StoreUserConfiguration> entry : configuration.getStoreUsers().entrySet()) {
            this.addStoreUser(entry.getKey(), StoreUserFactory.newStoreUser(entry.getValue()));
        }
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean isAdministrator() {
        return this.administrator;
    }

    @Override
    public void setAdministrator(boolean administrator) {
        this.administrator = administrator;
    }

    @Override
    public boolean checkPassword(String input) {
        return input.equals(this.password);
    }

    @Override
    public StoreUser getStoreUser(String storeAlias) {
        return this.users.get(storeAlias);
    }

    @Override
    public void addStoreUser(String storeAlias, StoreUser storeUser) {
        this.users.put(storeAlias, storeUser);
    }

    @Override
    public void removeStoreUser(String storeAlias) {
        this.users.remove(storeAlias);
    }
}

