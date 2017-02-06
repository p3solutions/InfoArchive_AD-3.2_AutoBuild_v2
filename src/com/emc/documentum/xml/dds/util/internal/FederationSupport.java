/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  com.xhive.XhiveDriverFactory
 *  com.xhive.core.interfaces.XhiveDriverIf
 *  com.xhive.core.interfaces.XhivePageCacheIf
 *  com.xhive.error.XhiveException
 *  com.xhive.federationset.interfaces.XhiveFederationSetFactory
 */
package com.emc.documentum.xml.dds.util.internal;

import com.xhive.XhiveDriverFactory;
import com.xhive.core.interfaces.XhiveDriverIf;
import com.xhive.core.interfaces.XhivePageCacheIf;
import com.xhive.error.XhiveException;
import com.xhive.federationset.interfaces.XhiveFederationSetFactory;
import java.io.IOException;

public final class FederationSupport {
    private XhivePageCacheIf cache;
    private static FederationSupport instance = new FederationSupport();

    private FederationSupport() {
    }

    public static FederationSupport getInstance() {
        return instance;
    }

    private synchronized XhiveDriverIf getDriver(String boot, XhivePageCacheIf myCache) {
        try {
            XhiveDriverIf driver;
            if (FederationSupport.isInFederationSet(boot)) {
                driver = XhiveFederationSetFactory.getFederation((String)boot, (XhivePageCacheIf)this.initCache(myCache));
            } else {
                driver = XhiveDriverFactory.getDriver();
                if (!driver.isInitialized()) {
                    driver.init(this.initCache(myCache));
                }
            }
            if (driver == null) {
                throw new XhiveException(452, new String[]{boot});
            }
            return driver;
        }
        catch (IOException ioe) {
            throw new XhiveException(2006, (Throwable)ioe, new String[0]);
        }
    }

    public synchronized XhiveDriverIf getDriver(String boot, int size) {
        return this.getDriver(boot, this.initCache(size));
    }

    public synchronized XhiveDriverIf getDriver(String boot) {
        if (FederationSupport.isInFederationSet(boot)) {
            return this.getDriver(boot, this.initCache());
        }
        return XhiveDriverFactory.getDriver((String)boot);
    }

    private static boolean isInFederationSet(String boot) {
        return boot != null && boot.indexOf("#") != -1;
    }

    private XhivePageCacheIf createPageCache(int size) {
        return XhiveDriverFactory.getFederationFactory().createPageCache(size);
    }

    private XhivePageCacheIf initCache(XhivePageCacheIf myCache) {
        if (this.cache == null) {
            this.cache = myCache;
        }
        return this.cache;
    }

    private XhivePageCacheIf initCache(int size) {
        if (this.cache == null) {
            return this.initCache(this.createPageCache(size));
        }
        return this.cache;
    }

    private XhivePageCacheIf initCache() {
        return this.initCache(0);
    }
}

