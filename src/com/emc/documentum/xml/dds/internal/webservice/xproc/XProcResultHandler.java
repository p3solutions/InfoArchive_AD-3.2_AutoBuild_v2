/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  com.emc.documentum.fs.datamodel.core.ObjectId
 *  com.emc.documentum.xml.xproc.io.Source
 *  com.emc.documentum.xml.xproc.pipeline.model.PipelineInput
 *  com.emc.documentum.xml.xproc.pipeline.model.PipelineOutput
 */
package com.emc.documentum.xml.dds.internal.webservice.xproc;

import com.emc.documentum.fs.datamodel.core.ObjectId;
import com.emc.documentum.xml.dds.exception.DDSException;
import com.emc.documentum.xml.dds.internal.webservice.QName;
import com.emc.documentum.xml.dds.internal.webservice.xproc.Option;
import com.emc.documentum.xml.dds.internal.webservice.xproc.Parameter;
import com.emc.documentum.xml.dds.internal.webservice.xproc.ParameterMapping;
import com.emc.documentum.xml.dds.internal.webservice.xproc.PipelineInput;
import com.emc.documentum.xml.dds.internal.webservice.xproc.PipelineOutput;
import com.emc.documentum.xml.dds.internal.webservice.xproc.Source;
import com.emc.documentum.xml.dds.internal.webservice.xproc.SourceMapping;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Set;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;

public final class XProcResultHandler
implements com.emc.documentum.xml.dds.fs.xproc.XProcResultHandler<PipelineOutput> {
    @Override
    public PipelineOutput transformXProcResult(com.emc.documentum.xml.xproc.pipeline.model.PipelineOutput output) throws DDSException {
        try {
            PipelineOutput sOutput = new PipelineOutput();
            if (output != null) {
                Set<String> outputPorts = output.getOutputPorts();
                if (outputPorts != null) {
                    for (String port : outputPorts) {
                        List<com.emc.documentum.xml.xproc.io.Source> sources = output.getSources(port);
                        if (sources == null) continue;
                        for (com.emc.documentum.xml.xproc.io.Source source : sources) {
                            sOutput.addOutput(port, this.serialize(source));
                        }
                    }
                }
                sOutput.setPrimaryOutputPort(output.getPrimaryOutputPort());
                Set<String> externalURIs = output.getExternalOutputs();
                if (externalURIs != null) {
                    for (String externalURI : externalURIs) {
                        sOutput.addExternalOutput(externalURI);
                    }
                }
            }
            return sOutput;
        }
        catch (IOException ioe) {
            throw new DDSException(ioe);
        }
    }

    public com.emc.documentum.xml.xproc.pipeline.model.PipelineInput deserialize(PipelineInput serializableInput) throws IOException {
        com.emc.documentum.xml.xproc.pipeline.model.PipelineInput input = new com.emc.documentum.xml.xproc.pipeline.model.PipelineInput();
        if (serializableInput != null) {
            List<Option> sOptions;
            List<ParameterMapping> sParameterMappings;
            List<SourceMapping> inputs = serializableInput.getInputs();
            if (inputs != null) {
                for (SourceMapping sourceMapping : inputs) {
                    String port = sourceMapping.getPort();
                    List<Source> sources = sourceMapping.getSources();
                    if (sources == null) continue;
                    for (Source sSource : sources) {
                        com.emc.documentum.xml.xproc.io.Source source = this.deserialize(sSource);
                        input.addInput(port, source);
                    }
                }
            }
            if ((sOptions = serializableInput.getOptions()) != null) {
                for (Option option : sOptions) {
                    QName name = option.getQname();
                    String value = option.getValue();
                    input.setOption(new javax.xml.namespace.QName(name.getNamespaceURI(), name.getLocalPart()), value);
                }
            }
            if ((sParameterMappings = serializableInput.getParameterMappings()) != null) {
                for (ParameterMapping parameterMapping : sParameterMappings) {
                    String port = parameterMapping.getPort();
                    List<Parameter> parameters = parameterMapping.getParameters();
                    if (parameters == null) continue;
                    for (Parameter parameter : parameters) {
                        QName name = parameter.getQName();
                        String value = parameter.getValue();
                        input.addParameter(port, new javax.xml.namespace.QName(name.getNamespaceURI(), name.getLocalPart()), value);
                    }
                }
            }
        }
        return input;
    }

    public Source serialize(com.emc.documentum.xml.xproc.io.Source source) throws IOException {
        String publicID = source.getPublicID();
        String systemID = source.getSystemID();
        Node node = source.getNode();
        InputStream is = null;
        String data = null;
        if (node == null) {
            is = source.getInputStream();
            if (is != null) {
                int c;
                ByteArrayOutputStream blobBaos = new ByteArrayOutputStream();
                while ((c = is.read()) != -1) {
                    blobBaos.write(c);
                }
                is.close();
                data = new String(blobBaos.toByteArray(), "UTF-8");
            }
        } else {
            DOMImplementationLS domImplementationLS = null;
            domImplementationLS = node.getNodeType() != 9 ? (DOMImplementationLS)((Object)node.getOwnerDocument().getImplementation()) : (DOMImplementationLS)((Object)((Document)node).getImplementation());
            LSSerializer ls = domImplementationLS.createLSSerializer();
            data = ls.writeToString(node);
        }
        return new Source(data, publicID == null ? null : new ObjectId(publicID), systemID == null ? null : new ObjectId(systemID));
    }

    public com.emc.documentum.xml.xproc.io.Source deserialize(Source source) throws IOException {
        String publicID;
        String data = source.getData();
        ByteArrayInputStream is = null;
        ObjectId objectId = source.getSystemId();
        String systemID = objectId == null ? null : objectId.getId();
        objectId = source.getPublicId();
        String string = publicID = objectId == null ? null : objectId.getId();
        if (data == null) {
            return new com.emc.documentum.xml.xproc.io.Source((InputStream)is, publicID, systemID);
        }
        is = new ByteArrayInputStream(data.getBytes("UTF-8"));
        return new com.emc.documentum.xml.xproc.io.Source((InputStream)is, publicID, systemID);
    }
}

