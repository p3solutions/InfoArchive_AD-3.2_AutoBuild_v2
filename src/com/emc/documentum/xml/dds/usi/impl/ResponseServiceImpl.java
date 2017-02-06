/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.usi.impl;

import com.emc.documentum.xml.dds.application.Application;
import com.emc.documentum.xml.dds.configuration.Configuration;
import com.emc.documentum.xml.dds.logging.LogCenter;
import com.emc.documentum.xml.dds.operation.exception.OperationException;
import com.emc.documentum.xml.dds.operation.framework.Operation;
import com.emc.documentum.xml.dds.operation.library.xbase.StoreEntryOperation;
import com.emc.documentum.xml.dds.service.DDSServiceType;
import com.emc.documentum.xml.dds.service.ServiceType;
import com.emc.documentum.xml.dds.service.exception.ServiceNotAvailableException;
import com.emc.documentum.xml.dds.service.impl.ServiceImpl;
import com.emc.documentum.xml.dds.user.User;
import com.emc.documentum.xml.dds.usi.ProcessorAssociation;
import com.emc.documentum.xml.dds.usi.Response;
import com.emc.documentum.xml.dds.usi.ResponseFilter;
import com.emc.documentum.xml.dds.usi.ResponseProcessor;
import com.emc.documentum.xml.dds.usi.ResponseService;
import com.emc.documentum.xml.dds.usi.ResponseServiceConfiguration;
import com.emc.documentum.xml.dds.usi.SimpleResponse;
import com.emc.documentum.xml.dds.usi.XBaseAssociation;
import com.emc.documentum.xml.dds.xbase.XBase;
import com.emc.documentum.xml.dds.xbase.XBaseEntry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.w3c.dom.Node;

public class ResponseServiceImpl
extends ServiceImpl
implements ResponseService {
    private List<ProcessorPair> preProcessors = new ArrayList<ProcessorPair>();
    private List<ProcessorPair> postProcessors = new ArrayList<ProcessorPair>();
    private List<XBasePair> xBases = new ArrayList<XBasePair>();
    private final Map<String, ResponseProcessor> processorMap = new HashMap<String, ResponseProcessor>();
    private final Object preProcessorMutex = new Object();
    private final Object postProcessorMutex = new Object();
    private final Object xBaseMutex = new Object();

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void submit(Response response) throws ServiceNotAvailableException, OperationException {
        this.checkRunning();
        Response processedResponse = response;
        Object object = this.preProcessorMutex;
        synchronized (object) {
            for (ProcessorPair pair : this.preProcessors) {
                if (pair == null || !pair.getFilter().accept(processedResponse)) continue;
                processedResponse = pair.getProcessor().process(processedResponse);
            }
        }
        object = this.xBaseMutex;
        synchronized (object) {
            Iterator<XBasePair> xBaseIterator = this.xBases.iterator();
            boolean stored = false;
            while (!stored && xBaseIterator.hasNext()) {
                XBasePair pair = xBaseIterator.next();
                if (pair == null || !pair.getFilter().accept(processedResponse)) continue;
                XBase xBase = pair.getXBase();
                XBaseEntry entry = xBase.newEntry(processedResponse.asNode());
                this.getApplication().execute(this.getApplication().getApplicationUser(), new StoreEntryOperation(xBase, entry));
                stored = true;
            }
        }
        object = this.postProcessorMutex;
        synchronized (object) {
            for (ProcessorPair pair : this.postProcessors) {
                if (pair == null || !pair.getFilter().accept(processedResponse)) continue;
                processedResponse = pair.getProcessor().process(processedResponse);
            }
        }
    }

    @Override
    public void submit(String xmlFragment) throws ServiceNotAvailableException, OperationException {
        this.checkRunning();
        this.submit(new SimpleResponse(xmlFragment));
    }

    @Override
    public void submit(Node node) throws ServiceNotAvailableException, OperationException {
        this.checkRunning();
        this.submit(new SimpleResponse(node));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int registerPreProcessor(ResponseFilter filter, ResponseProcessor preProcessor) {
        Object object = this.preProcessorMutex;
        synchronized (object) {
            this.preProcessors.add(new ProcessorPair(filter, preProcessor));
            this.processorMap.put(preProcessor.getId(), preProcessor);
            return this.preProcessors.size();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unregisterPreProcessor(int preProcessorId) {
        Object object = this.preProcessorMutex;
        synchronized (object) {
            this.preProcessors.set(preProcessorId - 1, null);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int registerPostProcessor(ResponseFilter filter, ResponseProcessor postProcessor) {
        Object object = this.postProcessorMutex;
        synchronized (object) {
            this.postProcessors.add(new ProcessorPair(filter, postProcessor));
            this.processorMap.put(postProcessor.getId(), postProcessor);
            return this.postProcessors.size();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unregisterPostProcessor(int postProcessorId) {
        Object object = this.postProcessorMutex;
        synchronized (object) {
            this.postProcessors.set(postProcessorId - 1, null);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int registerXBase(ResponseFilter filter, XBase xBase) {
        Object object = this.xBaseMutex;
        synchronized (object) {
            this.xBases.add(new XBasePair(filter, xBase));
            return this.xBases.size();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unregisterXBase(int xBaseId) {
        Object object = this.xBaseMutex;
        synchronized (object) {
            this.xBases.set(xBaseId - 1, null);
        }
    }

    @Override
    public ResponseProcessor getProcessor(String id) {
        return this.processorMap.get(id);
    }

    @Override
    public ServiceType getType() {
        return DDSServiceType.RESPONSE;
    }

    @Override
    public boolean activateConfiguration() {
        try {
            ResponseServiceConfiguration serviceConfiguration = (ResponseServiceConfiguration)this.getConfiguration();
            for (ProcessorAssociation association22 : serviceConfiguration.getPreProcessors()) {
                this.registerPreProcessor(association22.getFilter(), association22.getProcessor());
            }
            for (XBaseAssociation association : serviceConfiguration.getXBases()) {
                XBase xBase = this.getApplication().getXBase(association.getXBaseId());
                if (xBase == null) {
                    LogCenter.error(this, "XBase not found - please check whether the configuration for the ResponseService and the XBaseManager are correct. XBase Id = " + association.getXBaseId());
                    return false;
                }
                this.registerXBase(association.getFilter(), xBase);
                LogCenter.debug(this, "Added XBase association for XBase : " + association.getXBaseId());
            }
            for (ProcessorAssociation association2 : serviceConfiguration.getPostProcessors()) {
                this.registerPostProcessor(association2.getFilter(), association2.getProcessor());
            }
            return true;
        }
        catch (Exception e) {
            LogCenter.exception(this, "ResponseService configuration failed... Please check the Configuration.", e);
            return false;
        }
    }

    @Override
    protected boolean checkDependencies() {
        return true;
    }

    @Override
    protected boolean executeInitialization() {
        return this.activateConfiguration();
    }

    @Override
    protected boolean executeStartup() {
        return true;
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
        this.preProcessors = new ArrayList<ProcessorPair>();
        this.postProcessors = new ArrayList<ProcessorPair>();
        this.xBases = new ArrayList<XBasePair>();
        return true;
    }

    private class XBasePair {
        private final ResponseFilter filter;
        private final XBase xBase;

        protected XBasePair(ResponseFilter filter, XBase xBase) {
            this.filter = filter;
            this.xBase = xBase;
        }

        protected ResponseFilter getFilter() {
            return this.filter;
        }

        protected XBase getXBase() {
            return this.xBase;
        }
    }

    private class ProcessorPair {
        private final ResponseFilter filter;
        private final ResponseProcessor processor;

        protected ProcessorPair(ResponseFilter filter, ResponseProcessor processor) {
            this.filter = filter;
            this.processor = processor;
        }

        protected ResponseFilter getFilter() {
            return this.filter;
        }

        protected ResponseProcessor getProcessor() {
            return this.processor;
        }
    }

}

