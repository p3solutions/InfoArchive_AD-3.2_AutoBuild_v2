/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.configuration.user;

import com.emc.documentum.xml.dds.configuration.Configuration;
import com.emc.documentum.xml.dds.configuration.persistence.StoreUserConfiguration;
import com.emc.documentum.xml.dds.configuration.user.StoreUserSpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class UserConfiguration
implements Configuration {
    private String id;
    private String encryptedPassword;
    private boolean administrator;
    private List<StoreUserSpec> storeUserList = new ArrayList<StoreUserSpec>();

    public UserConfiguration(String id, String encryptedPassword) {
        this.id = id;
        this.encryptedPassword = encryptedPassword;
    }

    public String getId() {
        return this.id == null ? null : this.id.trim();
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEncryptedPassword() {
        return this.encryptedPassword == null ? null : this.encryptedPassword.trim();
    }

    public void setEncryptedPassword(String encryptedPassword) {
        this.encryptedPassword = encryptedPassword;
    }

    public boolean isAdministrator() {
        return this.administrator;
    }

    public void setAdministrator(boolean administrator) {
        this.administrator = administrator;
    }

    public StoreUserConfiguration getStoreUser(String storeAlias) {
        StoreUserSpec spec = this.getStoreUserSpec(storeAlias);
        if (spec != null) {
            return spec.getStoreUser();
        }
        return null;
    }

    public Map<String, StoreUserConfiguration> getStoreUsers() {
        HashMap<String, StoreUserConfiguration> storeUserMap = new HashMap<String, StoreUserConfiguration>();
        if (this.storeUserList == null) {
            return storeUserMap;
        }
        for (StoreUserSpec spec : this.storeUserList) {
            storeUserMap.put(spec.getStoreAlias(), spec.getStoreUser());
        }
        return storeUserMap;
    }

    public void setStoreUser(String storeAlias, StoreUserConfiguration storeUser) {
        StoreUserSpec spec = this.getStoreUserSpec(storeAlias);
        if (spec != null) {
            spec.setStoreUser(storeUser);
        } else {
            this.storeUserList.add(new StoreUserSpec(storeAlias, storeUser));
        }
    }

    public void removeStoreUser(String storeAlias) {
        StoreUserSpec spec = this.getStoreUserSpec(storeAlias);
        if (spec != null) {
            this.storeUserList.remove(spec);
        }
    }

    private StoreUserSpec getStoreUserSpec(String storeAlias) {
        if (this.storeUserList == null) {
            this.storeUserList = new ArrayList<StoreUserSpec>();
            return null;
        }
        for (StoreUserSpec spec : this.storeUserList) {
            if (!storeAlias.equals(spec.getStoreAlias())) continue;
            return spec;
        }
        return null;
    }
}

