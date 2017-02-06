/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.configuration.baseline.security;

import com.emc.documentum.xml.dds.configuration.baseline.security.CryptoSettings;
import com.emc.documentum.xml.dds.configuration.baseline.security.SecureRandomSettings;

public class JCESettings {
    private CryptoSettings crypto = new CryptoSettings();
    private SecureRandomSettings secureRandom = new SecureRandomSettings();

    public CryptoSettings getCrypto() {
        if (this.crypto == null) {
            this.crypto = new CryptoSettings();
        }
        return this.crypto;
    }

    public void setCrypto(CryptoSettings crypto) {
        this.crypto = crypto;
    }

    public SecureRandomSettings getSecureRandom() {
        if (this.secureRandom == null) {
            this.secureRandom = new SecureRandomSettings();
        }
        return this.secureRandom;
    }

    public void setSecureRandom(SecureRandomSettings secureRandom) {
        this.secureRandom = secureRandom;
    }
}

