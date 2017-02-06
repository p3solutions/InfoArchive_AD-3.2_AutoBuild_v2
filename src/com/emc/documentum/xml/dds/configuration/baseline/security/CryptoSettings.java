/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.configuration.baseline.security;

public class CryptoSettings {
    private String transformation;
    private String provider;

    public String getTransformation() {
        return this.transformation == null || "".equals(this.transformation.trim()) ? "RSA/ECB/OAEPWithSHA-1AndMGF1Padding" : this.transformation.trim();
    }

    public void setTransformation(String transformation) {
        this.transformation = transformation;
    }

    public String getAlgorithm() {
        String[] parts = this.getTransformation().split("/");
        return parts[0];
    }

    public String getProvider() {
        return this.provider == null || "".equals(this.provider.trim()) ? null : this.provider.trim();
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }
}

