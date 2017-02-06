/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.util;

import com.emc.documentum.xml.dds.persistence.Session;
import com.emc.documentum.xml.dds.persistence.Store;
import com.emc.documentum.xml.dds.persistence.StoreUser;
import com.emc.documentum.xml.dds.persistence.exception.StoreSpecificException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SessionPool {
    private final Map<UserInfo, Session> readOnlySessions = new HashMap<UserInfo, Session>();
    private final Map<UserInfo, Session> readWriteSessions = new HashMap<UserInfo, Session>();

    public synchronized Session getSession(Store store, StoreUser user, boolean create, boolean readOnly) throws StoreSpecificException {
        Objects.requireNonNull(store, "<null> store");
        Objects.requireNonNull(user, "<null> user");
        Session session = null;
        if (readOnly) {
            session = this.readOnlySessions.get(new UserInfo(user, store.getAlias()));
        }
        if (!readOnly || session == null) {
            session = this.readWriteSessions.get(new UserInfo(user, store.getAlias()));
        }
        if (session == null && create) {
            session = store.getSession(user, readOnly);
            this.put(store, user, session);
        }
        return session;
    }

    public synchronized void put(Store store, StoreUser user, Session session) {
        Objects.requireNonNull(store, "<null> store");
        Objects.requireNonNull(user, "<null> user");
        Objects.requireNonNull(session, "<null> session");
        if (session.isReadOnly()) {
            this.readOnlySessions.put(new UserInfo(user, store.getAlias()), session);
        } else {
            this.readWriteSessions.put(new UserInfo(user, store.getAlias()), session);
        }
    }

    public synchronized void clear() {
        this.readOnlySessions.clear();
        this.readWriteSessions.clear();
    }

    public synchronized Collection<Session> getReadOnlySessions() {
        return this.readOnlySessions.values();
    }

    public synchronized Collection<Session> getReadWriteSessions() {
        return this.readWriteSessions.values();
    }

    private static class UserInfo {
        private final StoreUser user;
        private final String storeAlias;

        public UserInfo(StoreUser user, String storeAlias) {
            this.user = user;
            this.storeAlias = storeAlias;
        }

        public StoreUser getUser() {
            return this.user;
        }

        public String getStoreAlias() {
            return this.storeAlias;
        }

        public boolean equals(Object obj) {
            if (!(obj instanceof UserInfo)) {
                return false;
            }
            UserInfo other = (UserInfo)obj;
            return this.user.equals(other.getUser()) && this.storeAlias.equals(other.getStoreAlias());
        }

        public int hashCode() {
            return ("" + (this.user.hashCode() + 64) + this.storeAlias).hashCode();
        }
    }

}

