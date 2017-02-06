/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.user;

import com.emc.documentum.xml.dds.persistence.StoreUser;

public interface User {
    public String getId();

    public void setPassword(String var1);

    public boolean isAdministrator();

    public void setAdministrator(boolean var1);

    public boolean checkPassword(String var1);

    public StoreUser getStoreUser(String var1);

    public void addStoreUser(String var1, StoreUser var2);

    public void removeStoreUser(String var1);
}

