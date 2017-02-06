/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  com.emc.documentum.xml.xproc.XProcConfiguration
 *  com.emc.documentum.xml.xproc.impl.api.xquery.XQueryFactory
 *  com.emc.documentum.xml.xproc.io.Resolver
 *  com.emc.documentum.xml.xproc.io.ResolverHandler
 *  com.emc.documentum.xml.xproc.io.Writer
 *  com.emc.documentum.xml.xproc.io.WriterHandler
 *  com.emc.documentum.xml.xproc.plugin.GenericPlugin
 *  com.emc.documentum.xml.xproc.security.SecurityHandler
 *  com.emc.documentum.xml.xproc.security.SecurityManager
 *  com.emc.documentum.xml.xproc.util.Registration
 *  com.emc.documentum.xml.xproc.util.impl.XProcConfigUtil
 */
package com.emc.documentum.xml.dds.xproc.internal;

import com.emc.documentum.xml.dds.application.Application;
import com.emc.documentum.xml.dds.user.User;
import com.emc.documentum.xml.dds.util.SessionPool;
import com.emc.documentum.xml.dds.xproc.AbstractDDSResolverHandler;
import com.emc.documentum.xml.dds.xproc.AbstractDDSWriterHandler;
import com.emc.documentum.xml.dds.xproc.exception.DDSXProcConfigurationException;
import com.emc.documentum.xml.dds.xproc.internal.DDSDOMImplementation;
import com.emc.documentum.xml.dds.xproc.internal.DDSSecurityHandler;
import com.emc.documentum.xml.dds.xproc.internal.DDSXQueryFactory;
import com.emc.documentum.xml.dds.xproc.internal.DefaultDDSResolverHandler;
import com.emc.documentum.xml.dds.xproc.internal.DefaultDDSWriterHandler;
import com.emc.documentum.xml.dds.xproc.internal.TransientWriterResolverHandler;
import com.emc.documentum.xml.xproc.XProcConfiguration;
import com.emc.documentum.xml.xproc.impl.api.xquery.XQueryFactory;
import com.emc.documentum.xml.xproc.io.Resolver;
import com.emc.documentum.xml.xproc.io.ResolverHandler;
import com.emc.documentum.xml.xproc.io.Writer;
import com.emc.documentum.xml.xproc.io.WriterHandler;
import com.emc.documentum.xml.xproc.plugin.GenericPlugin;
import com.emc.documentum.xml.xproc.security.SecurityHandler;
import com.emc.documentum.xml.xproc.security.SecurityManager;
import com.emc.documentum.xml.xproc.util.Registration;
import com.emc.documentum.xml.xproc.util.impl.XProcConfigUtil;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import javax.xml.namespace.QName;
import org.w3c.dom.DOMImplementation;

public class DDSPlugin
extends GenericPlugin {
    private final SessionPool sessionPool;
    private final Application application;
    private final User user;
    private final boolean readOnly;
    private final List<String> resolverHandlers;
    private final List<String> writerHandlers;

    public DDSPlugin(Application application, User user, boolean readOnly, SessionPool sessionPool, List<String> resolverHandlers, List<String> writerHandlers) {
        this.sessionPool = sessionPool;
        this.application = application;
        this.user = user;
        this.readOnly = readOnly;
        this.resolverHandlers = resolverHandlers;
        this.writerHandlers = writerHandlers;
    }

    public void plugin() throws Exception {
        XProcConfiguration xprocConfig = this.getXProcConfiguration();
        XProcConfigUtil.setDOMImplementation((XProcConfiguration)xprocConfig, (DOMImplementation)new DDSDOMImplementation(this.application, this.user, XProcConfigUtil.getDOMImplementation((XProcConfiguration)xprocConfig), this.sessionPool, this.readOnly));
        XProcConfigUtil.setXQueryFactory((XProcConfiguration)xprocConfig, (XQueryFactory)new DDSXQueryFactory(xprocConfig));
        TransientWriterResolverHandler transientWriterResolverHandler = new TransientWriterResolverHandler(false, true);
        xprocConfig.setAttribute(TransientWriterResolverHandler.TRANSIENT_HANDLER_INTERNAL_ATTR, (Object)transientWriterResolverHandler);
        DefaultDDSResolverHandler resolverHandler = new DefaultDDSResolverHandler(this.readOnly);
        resolverHandler.setApplication(this.application);
        resolverHandler.setUser(this.user);
        resolverHandler.setSessionPool(this.sessionPool);
        ArrayList<ResolverHandler> resolverHandlersInstances = new ArrayList<ResolverHandler>();
        resolverHandlersInstances.add(transientWriterResolverHandler);
        resolverHandlersInstances.add(resolverHandler);
        List<Object> rHandlers = this.instantiateObjects(this.resolverHandlers, AbstractDDSResolverHandler.class);
        for (Object handler : rHandlers) {
            resolverHandlersInstances.add((AbstractDDSResolverHandler)handler);
        }
        Resolver resolver = xprocConfig.getResolver();
        for (ResolverHandler handler2 : resolverHandlersInstances) {
            if (handler2 instanceof AbstractDDSResolverHandler) {
                AbstractDDSResolverHandler ddsHandler = (AbstractDDSResolverHandler)handler2;
                ddsHandler.setApplication(this.application);
                ddsHandler.setUser(this.user);
                ddsHandler.setSessionPool(this.sessionPool);
            }
            resolver.registerHandler(handler2);
        }
        DefaultDDSWriterHandler writerHandler = new DefaultDDSWriterHandler(this.readOnly, true);
        ArrayList<WriterHandler> writerHandlersInstances = new ArrayList<WriterHandler>();
        writerHandlersInstances.add(transientWriterResolverHandler);
        writerHandlersInstances.add(writerHandler);
        List<Object> wHandlers = this.instantiateObjects(this.writerHandlers, AbstractDDSWriterHandler.class);
        for (Object handler3 : wHandlers) {
            writerHandlersInstances.add(((WriterHandler)handler3));
        }
        Writer writer = xprocConfig.getWriter();
        for (WriterHandler handler4 : writerHandlersInstances) {
            if (handler4 instanceof AbstractDDSWriterHandler) {
                AbstractDDSWriterHandler ddsHandler = (AbstractDDSWriterHandler)handler4;
                ddsHandler.setApplication(this.application);
                ddsHandler.setUser(this.user);
                ddsHandler.setSessionPool(this.sessionPool);
            }
            writer.registerHandler(handler4);
        }
        SecurityManager securityManager = xprocConfig.getSecurityManager();
        securityManager.registerHandler((SecurityHandler)new DDSSecurityHandler());
    }

    private List<Object> instantiateObjects(List<String> classNames, Class<?> parentKlass) throws DDSXProcConfigurationException {
        ArrayList<Object> result = new ArrayList<Object>();
        if (classNames != null) {
            for (String className : classNames) {
                try {
                    Class klass = Thread.currentThread().getContextClassLoader().loadClass(className);
                    Constructor constructor = klass.getConstructor(new Class[0]);
                    Object instance = constructor.newInstance(new Object[0]);
                    if (!parentKlass.isInstance(instance)) {
                        throw new DDSXProcConfigurationException("Class: " + klass.getName() + " must be a subclass of: " + parentKlass.getName());
                    }
                    result.add(instance);
                    continue;
                }
                catch (Exception e) {
                    if (e instanceof DDSXProcConfigurationException) {
                        throw (DDSXProcConfigurationException)e;
                    }
                    throw new DDSXProcConfigurationException(e);
                }
            }
        }
        return result;
    }
}

