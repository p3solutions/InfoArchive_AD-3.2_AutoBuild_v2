/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  com.emc.documentum.xml.le.compiler.ProcessCompiler
 *  com.emc.documentum.xml.le.compiler.ProcessCompilerFactory
 *  com.emc.documentum.xml.le.engine.LogicEngine
 *  com.emc.documentum.xml.le.engine.LogicEngineFactory
 *  com.emc.documentum.xml.le.engine.process.model.ProcessDefinition
 *  com.emc.documentum.xml.le.engine.process.state.StateManager
 *  com.emc.documentum.xml.le.engine.resolver.DataModuleRef
 *  com.emc.documentum.xml.le.engine.resolver.DataModuleResolver
 *  com.emc.documentum.xml.le.error.ProcessException
 *  com.emc.documentum.xml.le.options.ProcessOptions
 *  com.emc.documentum.xml.le.renderer.OutputMetadata
 *  com.emc.documentum.xml.le.renderer.Renderer
 *  com.emc.documentum.xml.le.renderer.RendererFactory
 *  com.emc.documentum.xml.le.renderer.error.RenderException
 *  com.emc.documentum.xml.le.renderer.layout.LayoutPackage
 *  com.emc.documentum.xml.le.renderer.process.graph.ProcessGraph
 *  com.emc.documentum.xml.le.renderer.process.graph.ProcessGraphFactory
 *  com.emc.documentum.xml.le.renderer.process.layout.ProcessGraphLayouter
 *  com.emc.documentum.xml.le.renderer.process.layout.ProcessGraphLayouterFactory
 *  com.emc.documentum.xml.le.renderer.process.renderables.ProcessGraphRenderableProvider
 *  com.emc.documentum.xml.le.renderer.process.renderables.ProcessGraphRenderableProviderFactory
 *  com.emc.documentum.xml.le.util.xml.XmlInput
 */
package com.emc.documentum.xml.dds.le.impl;

import com.emc.documentum.xml.dds.application.Application;
import com.emc.documentum.xml.dds.configuration.Configuration;
import com.emc.documentum.xml.dds.le.AbstractDDSDataModuleResolver;
import com.emc.documentum.xml.dds.le.AbstractDDSStateManager;
import com.emc.documentum.xml.dds.le.DDSLogicEngine;
import com.emc.documentum.xml.dds.le.DDSProcessDataModuleRenderer;
import com.emc.documentum.xml.dds.le.LogicEngineConfiguration;
import com.emc.documentum.xml.dds.le.LogicEngineService;
import com.emc.documentum.xml.dds.le.exception.DDSLogicEngineConfigurationException;
import com.emc.documentum.xml.dds.le.exception.DDSProcessDataModuleRendererConfigurationException;
import com.emc.documentum.xml.dds.le.exception.DDSProcessDataModuleRendererException;
import com.emc.documentum.xml.dds.persistence.Session;
import com.emc.documentum.xml.dds.service.DDSServiceType;
import com.emc.documentum.xml.dds.service.ServiceType;
import com.emc.documentum.xml.dds.service.exception.ServiceNotAvailableException;
import com.emc.documentum.xml.dds.service.impl.ServiceImpl;
import com.emc.documentum.xml.dds.user.User;
import com.emc.documentum.xml.dds.util.SessionPool;
import com.emc.documentum.xml.le.compiler.ProcessCompiler;
import com.emc.documentum.xml.le.compiler.ProcessCompilerFactory;
import com.emc.documentum.xml.le.engine.LogicEngine;
import com.emc.documentum.xml.le.engine.LogicEngineFactory;
import com.emc.documentum.xml.le.engine.process.model.ProcessDefinition;
import com.emc.documentum.xml.le.engine.process.state.StateManager;
import com.emc.documentum.xml.le.engine.resolver.DataModuleRef;
import com.emc.documentum.xml.le.engine.resolver.DataModuleResolver;
import com.emc.documentum.xml.le.error.ProcessException;
import com.emc.documentum.xml.le.options.ProcessOptions;
import com.emc.documentum.xml.le.renderer.OutputMetadata;
import com.emc.documentum.xml.le.renderer.Renderer;
import com.emc.documentum.xml.le.renderer.RendererFactory;
import com.emc.documentum.xml.le.renderer.error.RenderException;
import com.emc.documentum.xml.le.renderer.layout.LayoutPackage;
import com.emc.documentum.xml.le.renderer.process.graph.ProcessGraph;
import com.emc.documentum.xml.le.renderer.process.graph.ProcessGraphFactory;
import com.emc.documentum.xml.le.renderer.process.layout.ProcessGraphLayouter;
import com.emc.documentum.xml.le.renderer.process.layout.ProcessGraphLayouterFactory;
import com.emc.documentum.xml.le.renderer.process.renderables.ProcessGraphRenderableProvider;
import com.emc.documentum.xml.le.renderer.process.renderables.ProcessGraphRenderableProviderFactory;
import com.emc.documentum.xml.le.util.xml.XmlInput;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class LogicEngineServiceImpl
extends ServiceImpl
implements LogicEngineService {
    public static final String S1000D_VERSION_2_2 = "2.2";
    public static final String S1000D_VERSION_2_2_1 = "2.2.1";
    public static final String S1000D_VERSION_2_3 = "2.3";
    public static final String S1000D_VERSION_3_0 = "3.0";
    private String dataModuleResolver;
    private String stateManager;
    private String s1000DVersion;

    @Override
    public ServiceType getType() {
        return DDSServiceType.LOGICENGINE;
    }

    @Override
    protected boolean checkDependencies() {
        return true;
    }

    @Override
    public boolean activateConfiguration() {
        LogicEngineConfiguration logicEngineConfiguration = (LogicEngineConfiguration)this.getConfiguration();
        this.dataModuleResolver = logicEngineConfiguration.getDataModuleResolver();
        this.stateManager = logicEngineConfiguration.getStateManager();
        this.s1000DVersion = logicEngineConfiguration.getS1000DVersion();
        if (this.s1000DVersion == null) {
            this.s1000DVersion = "3.0";
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
    public DDSLogicEngine newLogicEngine(User user) throws DDSLogicEngineConfigurationException, ServiceNotAvailableException {
        this.checkRunning();
        try {
            SessionPool sessionPool = new SessionPool();
            LogicEngine logicEngine = this.newLogicEngine(user, sessionPool);
            return new DDSLogicEngineImpl(logicEngine, sessionPool);
        }
        catch (Exception e) {
            if (e instanceof DDSLogicEngineConfigurationException) {
                throw (DDSLogicEngineConfigurationException)e;
            }
            throw new DDSLogicEngineConfigurationException(e);
        }
    }

    @Override
    public DDSProcessDataModuleRenderer newProcessDataModuleRenderer(User user) throws DDSProcessDataModuleRendererConfigurationException, ServiceNotAvailableException {
        this.checkRunning();
        try {
            SessionPool sessionPool = new SessionPool();
            LogicEngine logicEngine = this.newLogicEngine(user, sessionPool);
            ProcessOptions processOptions = this.getProcessOptions(this.s1000DVersion);
            return new DDSProcessDataModuleRendererImpl(logicEngine, sessionPool, processOptions);
        }
        catch (Exception e) {
            if (e instanceof DDSProcessDataModuleRendererConfigurationException) {
                throw (DDSProcessDataModuleRendererConfigurationException)e;
            }
            throw new DDSProcessDataModuleRendererConfigurationException(e);
        }
    }

    private LogicEngine newLogicEngine(User user, SessionPool sessionPool) throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException, ProcessException, DDSLogicEngineConfigurationException {
        StateManager stateManagerInstance;
        DataModuleResolver dataModuleResolverInstance = (DataModuleResolver)this.instantiateObject(this.dataModuleResolver, DataModuleResolver.class);
        if (dataModuleResolverInstance instanceof AbstractDDSDataModuleResolver) {
            AbstractDDSDataModuleResolver dataModuleResolverInstanceIS = (AbstractDDSDataModuleResolver)dataModuleResolverInstance;
            dataModuleResolverInstanceIS.setApplication(this.getApplication());
            dataModuleResolverInstanceIS.setUser(user);
            dataModuleResolverInstanceIS.setSessionPool(sessionPool);
        }
        if ((stateManagerInstance = (StateManager)this.instantiateObject(this.stateManager, StateManager.class)) instanceof AbstractDDSStateManager) {
            AbstractDDSStateManager stateManagerInstanceIS = (AbstractDDSStateManager)stateManagerInstance;
            stateManagerInstanceIS.setApplication(this.getApplication());
            stateManagerInstanceIS.setUser(user);
            stateManagerInstanceIS.setSessionPool(sessionPool);
        }
        ProcessOptions processOptions = this.getProcessOptions(this.s1000DVersion);
        LogicEngine logicEngine = LogicEngineFactory.newInstance().newLogicEngine(processOptions, dataModuleResolverInstance, stateManagerInstance);
        return logicEngine;
    }

    private ProcessOptions getProcessOptions(String s1000DVersionName) throws ProcessException, DDSLogicEngineConfigurationException {
        ProcessOptions options = new ProcessOptions();
        if ("2.2".equals(s1000DVersionName)) {
            options.setOption("option://www.s1000d.org/S1000D_2-2/xml_schema/dm/processSchema.xsd", true);
        } else if ("2.2.1".equals(s1000DVersionName)) {
            options.setOption("option://www.s1000d.org/S1000D_2-2-1/xml_schema/dm/processSchema.xsd", true);
        } else if ("2.3".equals(s1000DVersionName)) {
            options.setOption("option://www.s1000d.org/S1000D_2-3/xml_schema_master/dm/processSchema.xsd", true);
        } else if ("3.0".equals(s1000DVersionName)) {
            options.setOption("option://www.s1000d.org/S1000D_3-0/xml_schema_master/dm/processSchema.xsd", true);
        } else {
            throw new DDSLogicEngineConfigurationException("Unsupported S1000D version: " + s1000DVersionName);
        }
        return options;
    }

    private Object instantiateObject(String className, Class<?> parentKlass) throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        Class klass = Thread.currentThread().getContextClassLoader().loadClass(className);
        Constructor constructor = klass.getConstructor(new Class[0]);
        Object instance = constructor.newInstance(new Object[0]);
        return parentKlass.cast(instance);
    }

    private static class DDSProcessDataModuleRendererImpl
    implements DDSProcessDataModuleRenderer {
        private final LogicEngine logicEngine;
        private final SessionPool sessionPool;
        private final ProcessOptions processOptions;

        public DDSProcessDataModuleRendererImpl(LogicEngine logicEngine, SessionPool sessionPool, ProcessOptions processOptions) {
            this.logicEngine = logicEngine;
            this.sessionPool = sessionPool;
            this.processOptions = processOptions;
        }

        @Override
        public SessionPool getSessionPool() {
            return this.sessionPool;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public Collection<Session> getOpenSessions() {
            SessionPool sessionPool = this.sessionPool;
            synchronized (sessionPool) {
                Collection<Session> readOnlySessions = this.sessionPool.getReadWriteSessions();
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
        }

        @Override
        public void render(DataModuleRef dataModuleRef, OutputStream outputStream, DDSProcessDataModuleRenderer.OutputType type) throws DDSProcessDataModuleRendererException {
            if (type != DDSProcessDataModuleRenderer.OutputType.PDF) {
                throw new DDSProcessDataModuleRendererException("Unsupported process data module renderer type: " + (Object)((Object)type));
            }
            try {
                DataModuleResolver dataModuleResolver = this.logicEngine.getResolver();
                XmlInput input = dataModuleResolver.resolveDataModule(dataModuleRef);
                ProcessDefinition processDefinition = ProcessCompilerFactory.newInstance().newProcessCompiler(this.processOptions).compileProcess(input);
                DDSProcessDataModuleRendererImpl.renderPDF(processDefinition, outputStream);
            }
            catch (Exception e) {
                if (e instanceof DDSProcessDataModuleRendererException) {
                    throw (DDSProcessDataModuleRendererException)e;
                }
                throw new DDSProcessDataModuleRendererException(e);
            }
        }

        private static void renderPDF(ProcessDefinition processDefinition, OutputStream outputStream) throws RenderException, ProcessException {
            ProcessGraph processGraph = ProcessGraphFactory.newInstance().newProcessGraph(processDefinition);
            ProcessGraphLayouter pagedLayouter = ProcessGraphLayouterFactory.newInstance().newPagedLayouter();
            Renderer renderer = RendererFactory.newInstance().newPdfRenderer();
            ProcessGraphRenderableProvider processRenderableProvider = ProcessGraphRenderableProviderFactory.newInstance().newProcessGraphRenderableProvider();
            LayoutPackage layoutPackage = pagedLayouter.layout(processGraph, processRenderableProvider, renderer);
            OutputMetadata outputMetadata = new OutputMetadata();
            renderer.render(layoutPackage, outputMetadata, outputStream);
        }
    }

    private static class DDSLogicEngineImpl
    implements DDSLogicEngine {
        private final LogicEngine logicEngine;
        private final SessionPool sessionPool;

        public DDSLogicEngineImpl(LogicEngine logicEngine, SessionPool sessionPool) {
            this.logicEngine = logicEngine;
            this.sessionPool = sessionPool;
        }

        @Override
        public SessionPool getSessionPool() {
            return this.sessionPool;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public Collection<Session> getOpenSessions() {
            SessionPool sessionPool = this.sessionPool;
            synchronized (sessionPool) {
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
        }

        @Override
        public LogicEngine getLogicEngine() {
            return this.logicEngine;
        }
    }

}

