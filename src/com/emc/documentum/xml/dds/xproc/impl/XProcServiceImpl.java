/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  com.emc.documentum.xml.xproc.XProc
 *  com.emc.documentum.xml.xproc.XProcConfiguration
 *  com.emc.documentum.xml.xproc.XProcFactory
 *  com.emc.documentum.xml.xproc.plugin.Plugin
 *  com.emc.documentum.xml.xproc.plugin.PluginManager
 *  com.emc.documentum.xml.xproc.plugin.fop.FOPPlugin
 *  com.emc.documentum.xml.xproc.plugin.xpression.XPressionPlugin
 *  com.emc.documentum.xml.xproc.util.Registration
 */
package com.emc.documentum.xml.dds.xproc.impl;

import com.emc.documentum.xml.dds.application.Application;
import com.emc.documentum.xml.dds.configuration.Configuration;
import com.emc.documentum.xml.dds.logging.LogCenter;
import com.emc.documentum.xml.dds.persistence.Session;
import com.emc.documentum.xml.dds.service.DDSServiceType;
import com.emc.documentum.xml.dds.service.ServiceType;
import com.emc.documentum.xml.dds.service.exception.ServiceNotAvailableException;
import com.emc.documentum.xml.dds.service.impl.ServiceImpl;
import com.emc.documentum.xml.dds.user.User;
import com.emc.documentum.xml.dds.util.SessionPool;
import com.emc.documentum.xml.dds.util.internal.StringUtils;
import com.emc.documentum.xml.dds.xproc.DDSXProc;
import com.emc.documentum.xml.dds.xproc.XProcConfiguration;
import com.emc.documentum.xml.dds.xproc.XProcService;
import com.emc.documentum.xml.dds.xproc.exception.DDSXProcConfigurationException;
import com.emc.documentum.xml.dds.xproc.internal.DDSPlugin;
import com.emc.documentum.xml.xproc.XProc;
import com.emc.documentum.xml.xproc.XProcFactory;
import com.emc.documentum.xml.xproc.plugin.Plugin;
import com.emc.documentum.xml.xproc.plugin.PluginManager;
import com.emc.documentum.xml.xproc.plugin.fop.FOPPlugin;
import com.emc.documentum.xml.xproc.plugin.xpression.XPressionPlugin;
import com.emc.documentum.xml.xproc.util.Registration;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class XProcServiceImpl
extends ServiceImpl
implements XProcService {
    private String fopConfigurationLocation;
    private List<String> resolverModules = new ArrayList<String>();
    private List<String> writerModules = new ArrayList<String>();
    private boolean xPressionEnabled;
    private String xPressionEndpoint;
    private String xPressionUsername;
    private char[] xPressionPassword;

    @Override
    public ServiceType getType() {
        return DDSServiceType.XPROC;
    }

    @Override
    protected boolean checkDependencies() {
        return true;
    }

    @Override
    public boolean activateConfiguration() {
        XProcConfiguration.XSLFormatter.FOP fop;
        XProcConfiguration.XSLFormatter xslFormatter;
        XProcConfiguration xprocConfiguration = (XProcConfiguration)this.getConfiguration();
        XProcConfiguration.IO io = xprocConfiguration.getIO();
        if (io != null) {
            this.resolverModules = io.getResolverModules();
        }
        if (io != null) {
            this.writerModules = io.getWriterModules();
        }
        if ((xslFormatter = xprocConfiguration.getXSLFormatter()) != null && (fop = xslFormatter.getFOP()) != null) {
            this.fopConfigurationLocation = fop.getConfigurationLocation();
        }
        this.xPressionEnabled = false;
        XProcConfiguration.XPression xPression = xprocConfiguration.getXPression();
        if (xPression != null) {
            this.xPressionEndpoint = xPression.getEndpoint();
            if (StringUtils.isEmpty(this.xPressionEndpoint)) {
                LogCenter.error(this, "EMC/Documentum xPression webservice endpoint URL not set.");
                return false;
            }
            this.xPressionUsername = xPression.getUsername();
            if (StringUtils.isEmpty(this.xPressionUsername)) {
                LogCenter.error(this, "EMC/Documentum xPression webservice user name not set.");
                return false;
            }
            String xPressionEncryptedPassword = xPression.getEncryptedPassword();
            if (StringUtils.isEmpty(xPressionEncryptedPassword)) {
                LogCenter.error(this, "EMC/Documentum xPression webservice user password not set.");
                return false;
            }
            String tmp = StringUtils.decrypt(xPressionEncryptedPassword);
            if (tmp == null) {
                LogCenter.error(this, "EMC/Documentum xPression webservice user password decryption failed.");
                return false;
            }
            this.xPressionPassword = tmp.toCharArray();
            this.xPressionEnabled = true;
        }
        return true;
    }

    @Override
    protected boolean executeInitialization() {
        return this.activateConfiguration();
    }

    @Override
    protected boolean executePause() {
        return true;
    }

    @Override
    protected boolean executeResume() {
        return true;
    }

    @Override
    protected boolean executeShutdown() {
        return true;
    }

    @Override
    protected boolean executeStartup() {
        return true;
    }

    @Override
    public DDSXProc newXProc(User user, boolean readOnly) throws DDSXProcConfigurationException, ServiceNotAvailableException {
        this.checkRunning();
        try {
            XProcFactory xprocFactory = XProcFactory.newInstance();
            XProc xproc = xprocFactory.newXProc();
            com.emc.documentum.xml.xproc.XProcConfiguration xprocConfig = xproc.getXProcConfiguration();
            PluginManager pluginManager = xprocConfig.getPluginManager();
            SessionPool sessionPool = new SessionPool();
            pluginManager.registerPlugin((Plugin)this.getDDSPlugin(user, readOnly, sessionPool), null);
            pluginManager.registerPlugin((Plugin)this.getFOPPlugin(), null);
            if (this.xPressionEnabled) {
                pluginManager.registerPlugin((Plugin)new XPressionPlugin(this.xPressionEndpoint, this.xPressionUsername, this.xPressionPassword), null);
            }
            return new DDSXProcImpl(xproc, sessionPool);
        }
        catch (Exception e) {
            if (e instanceof DDSXProcConfigurationException) {
                throw (DDSXProcConfigurationException)e;
            }
            throw new DDSXProcConfigurationException(e);
        }
    }

    private DDSPlugin getDDSPlugin(User user, boolean readOnly, SessionPool sessionPool) {
        return new DDSPlugin(this.getApplication(), user, readOnly, sessionPool, this.resolverModules, this.writerModules);
    }

    private FOPPlugin getFOPPlugin() throws IOException {
        if (!StringUtils.isEmpty(this.fopConfigurationLocation)) {
            InputStream is;
            String uri;
            URL url = Thread.currentThread().getContextClassLoader().getResource(this.fopConfigurationLocation);
            if (url == null) {
                File fopUserConfigFile = new File(this.fopConfigurationLocation);
                is = new FileInputStream(fopUserConfigFile);
                uri = fopUserConfigFile.getAbsolutePath();
            } else {
                is = url.openStream();
                uri = url.toString();
            }
            return new FOPPlugin(is, uri);
        }
        return new FOPPlugin();
    }

    private static class DDSXProcImpl
    implements DDSXProc {
        private final XProc xproc;
        private final SessionPool sessionPool;

        public DDSXProcImpl(XProc xproc, SessionPool sessionPool) {
            this.xproc = xproc;
            this.sessionPool = sessionPool;
        }

        @Override
        public SessionPool getSessionPool() {
            return this.sessionPool;
        }

        @Override
        public Collection<Session> getOpenSessions() {
            Collection<Session> readOnlySessions = this.sessionPool.getReadOnlySessions();
            Collection<Session> readWriteSessions = this.sessionPool.getReadWriteSessions();
            if (readOnlySessions == null || readOnlySessions.isEmpty()) {
                return (Collection<Session>) (readWriteSessions == null ? Collections.emptyList() : readWriteSessions);
            }
            if (readWriteSessions == null || readWriteSessions.isEmpty()) {
                return readOnlySessions;
            }
            ArrayList<Session> result = new ArrayList<Session>();
            result.addAll(readOnlySessions);
            result.addAll(readWriteSessions);
            return result;
        }

        @Override
        public XProc getXProc() {
            return this.xproc;
        }
    }

}

