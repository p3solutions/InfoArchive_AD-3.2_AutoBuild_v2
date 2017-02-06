/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.configuration.baseline.security;

public class SecureRandomSettings {
    private String algorithm;
    private String provider;

    public String getAlgorithm() {
        return this.algorithm == null || "".equals(this.algorithm.trim()) ? "SHA1PRNG" : this.algorithm.trim();
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    public String getProvider() {
        return this.provider == null || "".equals(this.provider.trim()) ? null : this.provider.trim();
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }
}

