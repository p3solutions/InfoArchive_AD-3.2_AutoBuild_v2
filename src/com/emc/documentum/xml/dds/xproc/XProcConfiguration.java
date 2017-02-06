/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.xproc;

import com.emc.documentum.xml.dds.configuration.baseline.ServiceConfiguration;
import java.util.ArrayList;
import java.util.List;

public class XProcConfiguration
extends ServiceConfiguration {
    private IO io;
    private XSLFormatter xslFormatter;
    private XPression xPression;

    public IO getIO() {
        return this.io;
    }

    public void setIO(IO ioSection) {
        this.io = ioSection;
    }

    public XSLFormatter getXSLFormatter() {
        return this.xslFormatter;
    }

    public void setXSLFormatter(XSLFormatter xslFormatterSection) {
        this.xslFormatter = xslFormatterSection;
    }

    public XPression getXPression() {
        return this.xPression;
    }

    public void setXPression(XPression xPressionParam) {
        this.xPression = xPressionParam;
    }

    public static class XPression {
        private String endpoint;
        private String username;
        private String encryptedPassword;

        public String getEndpoint() {
            return this.endpoint == null ? null : this.endpoint.trim();
        }

        public void setEndpoint(String endpoint) {
            this.endpoint = endpoint;
        }

        public String getUsername() {
            return this.username == null ? null : this.username.trim();
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getEncryptedPassword() {
            return this.encryptedPassword == null ? null : this.encryptedPassword.trim();
        }

        public void setEncryptedPassword(String encryptedPassword) {
            this.encryptedPassword = encryptedPassword;
        }
    }

    public static class XSLFormatter {
        private FOP fop;

        public FOP getFOP() {
            return this.fop;
        }

        public void setFOP(FOP fopSection) {
            this.fop = fopSection;
        }

        public static class FOP {
            private String configurationLocation;

            public String getConfigurationLocation() {
                return this.configurationLocation == null ? null : this.configurationLocation.trim();
            }

            public void setConfigurationLocation(String configurationLocation) {
                this.configurationLocation = configurationLocation;
            }
        }

    }

    public static class IO {
        private List<String> resolverModules;
        private List<String> writerModules;

        public void addResolverModule(String module) {
            if (this.resolverModules == null) {
                this.resolverModules = new ArrayList<String>();
            }
            this.resolverModules.add(module);
        }

        public List<String> getResolverModules() {
            if (this.resolverModules == null) {
                return null;
            }
            ArrayList<String> result = new ArrayList<String>();
            for (String module : this.resolverModules) {
                result.add(module.trim());
            }
            return result;
        }

        public void addWriterModule(String module) {
            if (this.writerModules == null) {
                this.writerModules = new ArrayList<String>();
            }
            this.writerModules.add(module);
        }

        public List<String> getWriterModules() {
            if (this.writerModules == null) {
                return null;
            }
            ArrayList<String> result = new ArrayList<String>();
            for (String module : this.writerModules) {
                result.add(module.trim());
            }
            return result;
        }
    }

}

