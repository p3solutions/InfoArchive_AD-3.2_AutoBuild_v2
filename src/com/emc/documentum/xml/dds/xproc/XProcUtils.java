/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  com.emc.documentum.xml.xproc.XProc
 *  com.emc.documentum.xml.xproc.XProcConfiguration
 *  com.emc.documentum.xml.xproc.io.Resolver
 *  com.emc.documentum.xml.xproc.io.Source
 *  com.emc.documentum.xml.xproc.pipeline.model.Input
 *  com.emc.documentum.xml.xproc.pipeline.model.Output
 *  com.emc.documentum.xml.xproc.pipeline.model.PipelineInput
 *  com.emc.documentum.xml.xproc.pipeline.model.PipelineOutput
 *  com.emc.documentum.xml.xproc.pipeline.model.step.Pipeline
 */
package com.emc.documentum.xml.dds.xproc;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.emc.documentum.xml.dds.exception.DDSException;
import com.emc.documentum.xml.xproc.XProc;
import com.emc.documentum.xml.xproc.XProcConfiguration;
import com.emc.documentum.xml.xproc.io.Resolver;
import com.emc.documentum.xml.xproc.io.Source;
import com.emc.documentum.xml.xproc.pipeline.model.Input;
import com.emc.documentum.xml.xproc.pipeline.model.Output;
import com.emc.documentum.xml.xproc.pipeline.model.PipelineInput;
import com.emc.documentum.xml.xproc.pipeline.model.PipelineOutput;
import com.emc.documentum.xml.xproc.pipeline.model.step.Pipeline;

public final class XProcUtils {
    public static final String PORT_SOURCE = "source";
    public static final String PORT_RESULT = "result";

    private XProcUtils() {
    }

    public static Pipeline newPipeline(XProc xproc, String pipelineURI) {
        XProcConfiguration xprocConfig = xproc.getXProcConfiguration();
        Resolver resolver = xprocConfig.getResolver();
        Source source = resolver.getSource(null, pipelineURI);
        return XProcUtils.newPipeline(xproc, source);
    }

    public static Pipeline newPipeline(XProc xproc, Source source) {
        return xproc.newPipeline(source);
    }

    public static PipelineInput newPipelineInput() {
        return new PipelineInput();
    }

    public static PipelineOutput runPipeline(XProc xproc, Pipeline pipeline, PipelineInput input) {
        PipelineOutput x = xproc.run(pipeline, input);
        return x;
    }

    public static List<String> getInputPorts(Pipeline pipeline) {
        List<Input> inputs = pipeline.getInputPorts();
        ArrayList<String> result = new ArrayList<String>(inputs.size());
        for (Input input : inputs) {
            result.add(input.getPort());
        }
        return result;
    }

    public static String getPrimaryInputPort(Pipeline pipeline) throws DDSException {
        List<Input> inputs = pipeline.getInputPorts();
        for (Input input : inputs) {
            if (!input.isPrimary()) continue;
            return input.getPort();
        }
        throw new DDSException("Primary input port undefined");
    }

    public static List<String> getOutputPorts(Pipeline pipeline) {
        List<Output> outputs = pipeline.getOutputPorts();
        ArrayList<String> result = new ArrayList<String>(outputs.size());
        for (Output output : outputs) {
            result.add(output.getPort());
        }
        return result;
    }

    public static String getPrimaryOutputPort(Pipeline pipeline) throws DDSException {
        List<Output> outputs = pipeline.getOutputPorts();
        for (Output output : outputs) {
            if (!output.isPrimary()) continue;
            return output.getPort();
        }
        throw new DDSException("Primary output port undefined");
    }

    public static String getPrimaryOutputPort(PipelineOutput output) throws DDSException {
        String primaryOutputPort = output.getPrimaryOutputPort();
        if (primaryOutputPort == null) {
            throw new DDSException("Primary output port undefined");
        }
        return primaryOutputPort;
    }

    public static Source getOutputSource(PipelineOutput output, String port) throws DDSException {
        List<Source> sources = XProcUtils.getOutputSources(output, port);
        if (sources != null && sources.size() == 1) {
            return sources.get(0);
        }
        throw new DDSException("One source expected, got: " + (sources == null ? null : Integer.valueOf(sources.size())));
    }

    public static List<Source> getOutputSources(PipelineOutput output, String port) {
        return output.getSources(port);
    }

    public static Source getPrimaryOutputSource(PipelineOutput output) throws DDSException {
        String primaryOutputPort = XProcUtils.getPrimaryOutputPort(output);
        return XProcUtils.getOutputSource(output, primaryOutputPort);
    }

    public static List<Source> getPrimaryOutputSources(PipelineOutput output) throws DDSException {
        String primaryOutputPort = XProcUtils.getPrimaryOutputPort(output);
        if (primaryOutputPort == null) {
            throw new DDSException("Primary output port undefined");
        }
        return XProcUtils.getOutputSources(output, primaryOutputPort);
    }

    public static String getExternalOutputURI(PipelineOutput output) throws DDSException {
        Set<String> externalURIs = XProcUtils.getExternalOutputURIs(output);
        if (externalURIs.size() != 1) {
            throw new DDSException("Exactly one external URI expected, got: " + externalURIs.size());
        }
        return externalURIs.iterator().next();
    }

    public static Set<String> getExternalOutputURIs(PipelineOutput output) {
        return output.getExternalOutputs();
    }
}

