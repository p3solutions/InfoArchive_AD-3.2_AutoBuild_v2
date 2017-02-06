/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  com.xhive.core.interfaces.XhiveDatabaseIf
 *  com.xhive.core.interfaces.XhiveDriverIf
 *  com.xhive.core.interfaces.XhiveSessionIf
 *  com.xhive.dom.interfaces.XhiveLibraryChildIf
 *  com.xhive.dom.interfaces.XhiveLibraryIf
 *  com.xhive.dom.interfaces.XhiveNodeIf
 *  com.xhive.error.XhiveException
 *  com.xhive.util.interfaces.IterableIterator
 */
package com.emc.documentum.xml.dds.importer;

import com.emc.documentum.xml.dds.importer.SCSSubscriptionImpl;
import com.emc.documentum.xml.dds.importer.Subscription;
import com.emc.documentum.xml.dds.importer.SubscriptionFactoryIf;
import com.emc.documentum.xml.dds.importer.SubscriptionIf;
import com.emc.documentum.xml.dds.util.internal.FederationSupport;
import com.xhive.core.interfaces.XhiveDatabaseIf;
import com.xhive.core.interfaces.XhiveDriverIf;
import com.xhive.core.interfaces.XhiveSessionIf;
import com.xhive.dom.interfaces.XhiveLibraryChildIf;
import com.xhive.dom.interfaces.XhiveLibraryIf;
import com.xhive.dom.interfaces.XhiveNodeIf;
import com.xhive.error.XhiveException;
import com.xhive.util.interfaces.IterableIterator;
import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.Node;

public class DDSSubscriptionFactory
implements SubscriptionFactoryIf {
    private final XhiveDriverIf driver;
    private final XhiveSessionIf session;

    public DDSSubscriptionFactory(String database, String username, String password, String bootstrapFileName) {
        this.driver = FederationSupport.getInstance().getDriver(bootstrapFileName, 10000);
        this.session = this.driver.createSession();
        this.session.connect(username, password, database);
        this.session.begin();
    }

    @Override
    public void createSubscription(String name) {
        Subscription.createSubscription(name, null, null, this.session);
    }

    @Override
    public void deleteSubcription(String name) {
        if (!this.isSubscription(name)) {
            throw new XhiveException(2005, new String[0]);
        }
        XhiveLibraryIf lib = (XhiveLibraryIf)this.getRoot().getByPath("/DATA");
        XhiveLibraryChildIf node = lib.get(name);
        lib.removeChild((Node)node);
    }

    private XhiveLibraryIf getRoot() {
        return this.session.getDatabase().getRoot();
    }

    @Override
    public List<String> getSubscriptions() {
        ArrayList<String> sList = new ArrayList<String>();
        XhiveLibraryIf root = this.getRoot();
        XhiveLibraryIf lib = (XhiveLibraryIf)root.getByPath("/DATA");
        if (lib == null) {
            return sList;
        }
        for (XhiveNodeIf node : lib.getChildren()) {
            if (!Subscription.subscriptionExists(node)) continue;
            String name = ((XhiveLibraryIf)node).getName();
            sList.add(name);
        }
        return sList;
    }

    @Override
    public boolean isSubscription(String name) {
        return Subscription.subscriptionExists(name, this.getRoot());
    }

    @Override
    public SubscriptionIf getSubscription(String subscriptionName) {
        if (this.isSubscription(subscriptionName)) {
            return new SCSSubscriptionImpl(this.session, subscriptionName);
        }
        return null;
    }

    @Override
    public void terminateSession() {
        if (this.session.isOpen()) {
            this.session.commit();
        }
        if (this.session.isConnected()) {
            this.session.disconnect();
        }
    }

    @Override
    public void closeDriver() {
        if (this.driver != null && this.driver.isInitialized()) {
            this.driver.close();
        }
    }
}

