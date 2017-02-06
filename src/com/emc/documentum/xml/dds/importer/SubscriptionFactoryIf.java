/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.importer;

import com.emc.documentum.xml.dds.importer.SubscriptionIf;
import java.util.List;

public interface SubscriptionFactoryIf {
    public boolean isSubscription(String var1);

    public List getSubscriptions();

    public void createSubscription(String var1);

    public void deleteSubcription(String var1);

    public SubscriptionIf getSubscription(String var1);

    public void terminateSession();

    public void closeDriver();
}

