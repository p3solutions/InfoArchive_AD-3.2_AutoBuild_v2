/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.util.internal;

import com.emc.documentum.xml.dds.configuration.baseline.security.CryptoSettings;
import com.emc.documentum.xml.dds.configuration.baseline.security.JCESettings;
import com.emc.documentum.xml.dds.configuration.baseline.security.SecureRandomSettings;
import com.emc.documentum.xml.dds.logging.LogCenter;
import com.emc.documentum.xml.dds.util.PathResolver;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.Cipher;

public final class Cryptographer {
    private static Object cipherMutex = new Object();
    private final JCESettings jceSettings;
    private String privateKeyPath;
    private String publicKeyPath;
    private PKCS8EncodedKeySpec privateKeySpec;
    private X509EncodedKeySpec publicKeySpec;
    private KeyFactory keyFactory;
    private Key privateKey;
    private Key publicKey;
    private Cipher encryptingCipher;
    private Cipher decryptingCipher;
    private final boolean initialized;
    private static Cryptographer instance;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static boolean initialize(JCESettings jceSettings, String privateKeyPath, String publicKeyPath) {
        Object object = cipherMutex;
        synchronized (object) {
            instance = new Cryptographer(jceSettings, privateKeyPath, publicKeyPath);
            return Cryptographer.instance.initialized;
        }
    }

    public static boolean isInitialized() {
        return Cryptographer.instance.initialized;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected static byte[] encrypt(byte[] messageBytes) {
        Object object = cipherMutex;
        synchronized (object) {
            return instance.encryptBytes(messageBytes);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected static byte[] decrypt(byte[] messageBytes) {
        Object object = cipherMutex;
        synchronized (object) {
            return instance.decryptBytes(messageBytes);
        }
    }

    public static boolean generateKeys(File privateKeyFile, File publicKeyFile) {
        return Cryptographer.generateKeys(new JCESettings(), privateKeyFile, publicKeyFile);
    }

    public static boolean generateKeys(JCESettings jceSettings, File privateKeyFile, File publicKeyFile) {
        LogCenter.log("Generating keys.");
        FileOutputStream fileOutputStream = null;
        try {
            String secAlgorithm = jceSettings.getCrypto().getAlgorithm();
            String secProvider = jceSettings.getCrypto().getProvider();
            KeyPairGenerator keyGen = secProvider == null ? KeyPairGenerator.getInstance(secAlgorithm) : KeyPairGenerator.getInstance(secAlgorithm, secProvider);
            String srAlgorithm = jceSettings.getSecureRandom().getAlgorithm();
            String srProvider = jceSettings.getSecureRandom().getProvider();
            SecureRandom random = srProvider == null ? SecureRandom.getInstance(srAlgorithm) : SecureRandom.getInstance(srAlgorithm, srProvider);
            keyGen.initialize(1024, random);
            KeyPair pair = keyGen.generateKeyPair();
            X509EncodedKeySpec encodedPublicKeySpec = new X509EncodedKeySpec(pair.getPublic().getEncoded());
            PKCS8EncodedKeySpec encodedPrivateKeySpec = new PKCS8EncodedKeySpec(pair.getPrivate().getEncoded());
            fileOutputStream = new FileOutputStream(publicKeyFile);
            fileOutputStream.write(encodedPublicKeySpec.getEncoded());
            fileOutputStream.close();
            fileOutputStream = new FileOutputStream(privateKeyFile);
            fileOutputStream.write(encodedPrivateKeySpec.getEncoded());
            fileOutputStream.close();
            return true;
        }
        catch (Exception e) {
            LogCenter.exception("failed to generate keys :", (Throwable)e);
            return false;
        }
    }

    private Cryptographer(JCESettings jceSettings, String privateKeyPath, String publicKeyPath) {
        this.jceSettings = jceSettings == null ? new JCESettings() : jceSettings;
        this.setPrivateKeyFile(privateKeyPath);
        this.setPublicKeyFile(publicKeyPath);
        this.initialized = this.initialize();
    }

    private void setPrivateKeyFile(String keyPath) {
        this.privateKeyPath = keyPath;
    }

    private void setPublicKeyFile(String keyPath) {
        this.publicKeyPath = keyPath;
    }

    public String getNameForLogging() {
        return "Cryptographer";
    }

    private byte[] encryptBytes(byte[] messageBytes) {
        try {
            return this.encryptingCipher.doFinal(messageBytes);
        }
        catch (Exception e) {
            LogCenter.exception(this, "Problem encrypting message :", e);
            return null;
        }
    }

    private byte[] decryptBytes(byte[] messageBytes) {
        try {
            return this.decryptingCipher.doFinal(messageBytes);
        }
        catch (Exception e) {
            LogCenter.exception(this, "Problem decrypting message :", e);
            return null;
        }
    }

    private boolean initialize() {
        LogCenter.debug(this, "Initializing Cryptographer");
        this.privateKeySpec = (PKCS8EncodedKeySpec)this.readKeySpec(this.privateKeyPath, true);
        this.publicKeySpec = (X509EncodedKeySpec)this.readKeySpec(this.publicKeyPath, false);
        String transformation = this.jceSettings.getCrypto().getTransformation();
        String algorithm = this.jceSettings.getCrypto().getAlgorithm();
        String provider = this.jceSettings.getCrypto().getProvider();
        try {
            this.keyFactory = provider == null ? KeyFactory.getInstance(algorithm) : KeyFactory.getInstance(algorithm, provider);
            this.privateKey = this.keyFactory.generatePrivate(this.privateKeySpec);
            this.publicKey = this.keyFactory.generatePublic(this.publicKeySpec);
        }
        catch (Exception e) {
            LogCenter.exception(this, "Initialization problem :", e);
            return false;
        }
        try {
            this.decryptingCipher = provider == null ? Cipher.getInstance(transformation) : Cipher.getInstance(transformation, provider);
            this.decryptingCipher.init(2, this.privateKey);
            this.encryptingCipher = provider == null ? Cipher.getInstance(transformation) : Cipher.getInstance(transformation, provider);
            this.encryptingCipher.init(1, this.publicKey);
        }
        catch (Exception e) {
            LogCenter.exception(this, "Initialization problem :", e);
            return false;
        }
        LogCenter.debug(this, "Initialization of Cryptographer finished");
        return true;
    }

    private KeySpec readKeySpec(String path, boolean isPrivate) {
        try {
            InputStream is = PathResolver.getInputStream(path);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            while (is.available() > 0) {
                byteArrayOutputStream.write(is.read());
            }
            is.close();
            if (isPrivate) {
                return new PKCS8EncodedKeySpec(byteArrayOutputStream.toByteArray());
            }
            return new X509EncodedKeySpec(byteArrayOutputStream.toByteArray());
        }
        catch (Exception e) {
            LogCenter.exception(this, "Problem getting KeySpec :", e);
            return null;
        }
    }
}

