/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.configuration.persistence.xdb;

import com.emc.documentum.xml.dds.configuration.persistence.StoreConfiguration;
import com.emc.documentum.xml.dds.configuration.persistence.StoreUserConfiguration;
import com.emc.documentum.xml.dds.configuration.persistence.xdb.XDBStoreConfiguration;
import com.emc.documentum.xml.dds.persistence.StoreType;
import com.emc.documentum.xml.dds.persistence.xdb.ReplicaSelector;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class ReplicatedXDBStoreConfiguration
extends StoreConfiguration {
    private XDBStoreConfiguration master;
    private final List<XDBStoreConfiguration> slaves = new ArrayList<XDBStoreConfiguration>();
    private ReplicaSelector replicaSelector;
    private long timeout = 30000;

    public ReplicatedXDBStoreConfiguration(String alias, StoreUserConfiguration defaultStoreUser) {
        super(alias, defaultStoreUser);
    }

    public ReplicatedXDBStoreConfiguration(String alias, StoreUserConfiguration defaultStoreUser, XDBStoreConfiguration master, List<XDBStoreConfiguration> slaves, ReplicaSelector replicaSelector) {
        super(alias, defaultStoreUser);
        this.setMaster(master);
        if (slaves != null) {
            for (XDBStoreConfiguration slave : slaves) {
                slave.setDatabaseName(master.getDatabaseName());
                this.addSlave(slave);
            }
        }
        this.replicaSelector = replicaSelector;
    }

    public XDBStoreConfiguration getMaster() {
        return this.master;
    }

    public void setMaster(XDBStoreConfiguration master) {
        this.master = master;
        for (XDBStoreConfiguration slave : this.slaves) {
            slave.setDatabaseName(master.getDatabaseName());
        }
    }

    public List<XDBStoreConfiguration> getSlaves() {
        ArrayList<XDBStoreConfiguration> result = new ArrayList<XDBStoreConfiguration>();
        for (XDBStoreConfiguration slave : this.slaves) {
            slave.setDatabaseName(this.master.getDatabaseName());
        }
        result.addAll(this.slaves);
        return result;
    }

    public XDBStoreConfiguration getSlave(String slaveAlias) {
        for (XDBStoreConfiguration next : this.slaves) {
            if (!next.getAlias().equals(slaveAlias)) continue;
            XDBStoreConfiguration result = next;
            next.setDatabaseName(this.master.getDatabaseName());
            return result;
        }
        return null;
    }

    public void addSlave(XDBStoreConfiguration slave) {
        slave.setDatabaseName(this.master.getDatabaseName());
        this.removeSlave(slave.getAlias());
        this.slaves.add(slave);
    }

    public void removeSlave(String slaveAlias) {
        Iterator<XDBStoreConfiguration> iterator = this.slaves.iterator();
        XDBStoreConfiguration existing = null;
        while (iterator.hasNext()) {
            XDBStoreConfiguration next = iterator.next();
            if (!next.getAlias().equals(slaveAlias)) continue;
            existing = next;
        }
        if (existing != null) {
            this.slaves.remove(existing);
        }
    }

    public ReplicaSelector getReplicaSelector() {
        return this.replicaSelector;
    }

    public void setReplicaSelector(ReplicaSelector replicaSelector) {
        this.replicaSelector = replicaSelector;
    }

    public long getTimeout() {
        return this.timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    @Override
    public StoreType getType() {
        return StoreType.REPLICATEDXDB;
    }
}

