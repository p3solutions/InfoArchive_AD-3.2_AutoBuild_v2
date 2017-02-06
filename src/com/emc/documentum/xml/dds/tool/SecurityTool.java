/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.tool;

import com.emc.documentum.xml.dds.configuration.baseline.security.CryptoSettings;
import com.emc.documentum.xml.dds.configuration.baseline.security.JCESettings;
import com.emc.documentum.xml.dds.configuration.baseline.security.SecureRandomSettings;
import com.emc.documentum.xml.dds.util.internal.Cryptographer;
import com.emc.documentum.xml.dds.util.internal.StringUtils;
import java.io.File;
import java.io.PrintStream;

public final class SecurityTool {
    public static final String COMMAND_GENERATE = "generate";
    public static final String COMMAND_ENCRYPT = "encrypt";

    private SecurityTool() {
    }

    public static void main(String[] args) {
        String command = null;
        String privatePath = null;
        String publicPath = null;
        String password = null;
        String cryptoTransformation = null;
        String cryptoProvider = null;
        String secureRandomAlgorithm = null;
        String secureRandomProvider = null;
        for (int counter = 0; counter < args.length; ++counter) {
            String arg = args[counter];
            if (arg.startsWith("-command")) {
                command = args[++counter].trim();
                continue;
            }
            if (arg.startsWith("-private")) {
                privatePath = args[++counter].trim();
                continue;
            }
            if (arg.startsWith("-public")) {
                publicPath = args[++counter].trim();
                continue;
            }
            if (arg.startsWith("-password")) {
                password = args[++counter].trim();
                continue;
            }
            if (arg.startsWith("-cryptoTransformation")) {
                cryptoTransformation = args[++counter].trim();
                continue;
            }
            if (arg.startsWith("-cryptoProvider")) {
                cryptoProvider = args[++counter].trim();
                continue;
            }
            if (arg.startsWith("-secureRandomAlgorithm")) {
                secureRandomAlgorithm = args[++counter].trim();
                continue;
            }
            if (!arg.startsWith("-secureRandomProvider")) continue;
            secureRandomProvider = args[++counter].trim();
        }
        JCESettings jceSettings = new JCESettings();
        jceSettings.getCrypto().setTransformation(cryptoTransformation);
        jceSettings.getCrypto().setProvider(cryptoProvider);
        jceSettings.getSecureRandom().setAlgorithm(secureRandomAlgorithm);
        jceSettings.getSecureRandom().setProvider(secureRandomProvider);
        System.out.println(SecurityTool.doTool(command, privatePath, publicPath, password, jceSettings));
    }

    public static String doTool(String command, String privatePath, String publicPath, String password, JCESettings jceSettings) {
        String privPath = privatePath;
        String pubPath = publicPath;
        if (privPath == null || "".equals(privPath)) {
            privPath = "./DDSPrivateKey.dat";
        }
        if (pubPath == null || "".equals(pubPath)) {
            pubPath = "./DDSPublicKey.dat";
        }
        if ("generate".equalsIgnoreCase(command)) {
            Cryptographer.generateKeys(jceSettings, new File(privPath), new File(pubPath));
        } else if ("encrypt".equalsIgnoreCase(command)) {
            File privateKeyFile = new File(privPath);
            File publicKeyFile = new File(pubPath);
            if (!privateKeyFile.exists()) {
                System.err.println("Could not find file containing private key : " + privateKeyFile.getAbsolutePath());
                return null;
            }
            if (!publicKeyFile.exists()) {
                System.err.println("Could not find file containing public key : " + publicKeyFile.getAbsolutePath());
                return null;
            }
            if (Cryptographer.initialize(jceSettings, privPath, pubPath)) {
                String encrypted = StringUtils.encrypt(password);
                return encrypted;
            }
            System.out.println("Encryption failed.");
        } else {
            System.out.println("Invalid command: " + command + ". Valid commands are \"generate\" and \"encrypt\"");
        }
        return null;
    }
}

