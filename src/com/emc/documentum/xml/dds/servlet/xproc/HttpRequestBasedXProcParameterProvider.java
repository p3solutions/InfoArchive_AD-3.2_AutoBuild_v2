/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  com.emc.documentum.xml.xproc.io.Source
 *  com.emc.documentum.xml.xproc.pipeline.model.PipelineInput
 *  javax.servlet.http.HttpServletRequest
 */
package com.emc.documentum.xml.dds.servlet.xproc;

import com.emc.documentum.xml.dds.servlet.xproc.XProcParameterProvider;
import com.emc.documentum.xml.dds.util.internal.HTTPRequestUtils;
import com.emc.documentum.xml.dds.util.internal.StringUtils;
import com.emc.documentum.xml.dds.util.internal.URIUtils;
import com.emc.documentum.xml.dds.xproc.XProcUtils;
import com.emc.documentum.xml.xproc.io.Source;
import com.emc.documentum.xml.xproc.pipeline.model.PipelineInput;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.xml.namespace.QName;

public class HttpRequestBasedXProcParameterProvider
implements XProcParameterProvider {
    public static final String PARAM_PREFIX_DATA = "data";
    public static final String PARAM_PREFIX_DATA_URI = "datauri";
    public static final String PARAM_PREFIX_DATA_PARAM = "dataparam";
    public static final String PARAM_PREFIX_INPUT = "input";
    public static final String PARAM_PREFIX_PARAMINPUT = "paraminput";
    public static final String PARAM_PREFIX_PARAM = "param";
    public static final String PARAM_PREFIX_OPTION = "option";
    public static final String PARAM_PIPELINE = "pipeline";
    public static final String PARAM_DATA_BASE_URI = "baseuri";
    public static final String PARAM_CONTENT_TYPE = "contenttype";
    public static final String PARAM_EXTERNAL_OUTPUT = "extoutput";
    public static final String PARAM_READ_ONLY = "readonly";
    public static final String PARAM_PREFIX_HEADER = "h";
    private static final String CONTENT_TYPE_APPLICATION_XML = "application/xml";
    private final HttpServletRequest request;
    private final HTTPRequestUtils.ParameterCache paramCache;

    public HttpRequestBasedXProcParameterProvider(HttpServletRequest request) throws IOException {
        this.request = request;
        this.paramCache = HTTPRequestUtils.getParameterCache(request);
    }

    @Override
    public String getPipelineURI() {
        return this.request.getParameter("pipeline");
    }

    @Override
    public boolean useExternalOutput() {
        String extOutput = this.request.getParameter("extoutput");
        return extOutput != null && "true".equals(extOutput);
    }

    @Override
    public String getExternalContentType(String uri) {
        return this.getSimpleContentType();
    }

    @Override
    public boolean isReadOnly() {
        String readOnly = this.request.getParameter("readonly");
        return readOnly == null || "true".equals(readOnly);
    }

    @Override
    public String getContentType(String port) {
        return this.getSimpleContentType();
    }

    private String getSimpleContentType() {
        String contentType = this.request.getParameter("contenttype");
        if (contentType == null) {
            return "application/xml";
        }
        return contentType;
    }

    @Override
    public Map<String, String> getHeaders() throws IOException {
        HashMap<String, String> map = new HashMap<String, String>();
        Iterator<String> paramIt = this.paramCache.getParameters();
        while (paramIt.hasNext()) {
            String parameterName = paramIt.next();
            if (!parameterName.startsWith("h(")) continue;
            HTTPRequestUtils.ParameterValue value = this.paramCache.getParameterValue(parameterName);
            if (value != null && value.isStreamBased()) {
                throw new IOException("Stream based parameter values not supported for header values: " + parameterName);
            }
            String header = this.getEntryName(parameterName);
            map.put(header, value.getValue());
        }
        return map;
    }

    @Override
    public PipelineInput getPipelineInput() throws URISyntaxException, IOException {
        PipelineInput input = XProcUtils.newPipelineInput();
        Iterator<String> paramIt = this.paramCache.getParameters();
        while (paramIt.hasNext()) {
            String parameterName = paramIt.next();
            if (parameterName.startsWith("input(")) {
                this.addInputPortSources(input, parameterName);
                continue;
            }
            if (parameterName.startsWith("paraminput(")) {
                this.addParameterInputPortParameters(input, parameterName);
                continue;
            }
            if (!parameterName.startsWith("option:")) continue;
            this.addOption(input, parameterName);
        }
        return input;
    }

    private void addInputPortSources(PipelineInput input, String param) throws URISyntaxException, IOException {
        String data;
        List<String> sources;
        HTTPRequestUtils.ParameterValue value = this.paramCache.getParameterValue(param);
        if (value != null && value.isStreamBased()) {
            throw new IllegalArgumentException("Stream based parameters are not supported for lists of input sources: " + param);
        }
        String string = data = value == null ? null : value.getValue();
        if (StringUtils.isEmpty(data)) {
            return;
        }
        if (!param.startsWith("input(")) {
            throw new IllegalArgumentException("Invalid parameter name: " + param);
        }
        String baseURI = this.request.getParameter("baseuri");
        String port = this.getEntryName(param);
        if (data != null && (sources = StringUtils.stringToList(data, ",", true)) != null) {
            for (String sourceName : sources) {
                Source source = this.getSource(sourceName, baseURI);
                input.addInput(port, source);
            }
        }
    }

    private Source getSource(String sourceName, String baseURI) throws URISyntaxException, IOException {
        HTTPRequestUtils.ParameterValue value = this.paramCache.getParameterValue("data(" + sourceName + ')');
        if (value != null) {
            if (value.isStreamBased()) {
                return new Source(value.getInputStream(), StringUtils.isEmpty(baseURI) ? null : baseURI);
            }
            if (value.getValue() == null) {
                throw new IOException("No value found for parameter: " + value);
            }
            return new Source((InputStream)new ByteArrayInputStream(value.getValue().getBytes("UTF-8")), StringUtils.isEmpty(baseURI) ? null : baseURI);
        }
        value = this.paramCache.getParameterValue("datauri(" + sourceName + ')');
        if (value != null) {
            if (value.isStreamBased()) {
                throw new IllegalArgumentException("Stream based datauri parameters are not supported: datauri(" + sourceName + ')');
            }
            String uriResolved = StringUtils.isEmpty(baseURI) ? value.getValue() : URIUtils.resolve(value.getValue(), baseURI);
            return new Source(uriResolved);
        }
        value = this.paramCache.getParameterValue("dataparam(" + sourceName + ')');
        if (value != null) {
            if (value.isStreamBased()) {
                throw new IllegalArgumentException("Stream based dataparam parameters are not supported: datauri(" + sourceName + ')');
            }
            HTTPRequestUtils.ParameterValue paramValue = this.paramCache.getParameterValue(value.getValue());
            if (paramValue == null) {
                throw new IOException("Request parameter not found: " + value);
            }
            if (paramValue.isStreamBased()) {
                return new Source(paramValue.getInputStream(), StringUtils.isEmpty(baseURI) ? null : baseURI);
            }
            if (paramValue.getValue() == null) {
                throw new IOException("No value found for parameter: " + value);
            }
            return new Source((InputStream)new ByteArrayInputStream(paramValue.getValue().getBytes("UTF-8")), StringUtils.isEmpty(baseURI) ? null : baseURI);
        }
        throw new IOException("Source data unknown: " + sourceName);
    }

    private void addOption(PipelineInput input, String param) throws IOException {
        if (!param.startsWith("option:")) {
            throw new IOException("Invalid option name: " + param);
        }
        HTTPRequestUtils.ParameterValue value = this.paramCache.getParameterValue(param);
        if (value != null && value.isStreamBased()) {
            throw new IOException("Stream based parameter values not supported for option values: " + param);
        }
        String option = param.substring("option:".length());
        input.setOption(new QName(option), value.getValue());
    }

    private void addParameterInputPortParameters(PipelineInput input, String param) throws IOException {
        if (!param.startsWith("paraminput(")) {
            throw new IllegalArgumentException("Invalid parameter name: " + param);
        }
        HTTPRequestUtils.ParameterValue value = this.paramCache.getParameterValue(param);
        if (value != null) {
            List<String> parameters;
            if (value.isStreamBased()) {
                throw new IOException("Stream based parameter values not supported for parameter lists: " + param);
            }
            String data = value.getValue();
            if (StringUtils.isEmpty(data)) {
                return;
            }
            String port = this.getEntryName(param);
            if (data != null && (parameters = StringUtils.stringToList(data, ",", true)) != null) {
                for (String paramName : parameters) {
                    QName paramQName = QName.valueOf(paramName);
                    String paramValue = this.getParameterValue(paramQName);
                    input.addParameter(port, paramQName, paramValue);
                }
            }
        }
    }

    private String getParameterValue(QName param) throws IOException {
        HTTPRequestUtils.ParameterValue value = this.paramCache.getParameterValue("param(" + param.toString() + ')');
        if (value != null) {
            if (value.isStreamBased()) {
                throw new IOException("Stream based parameter values not supported for parameter values: " + param);
            }
            return value.getValue();
        }
        return null;
    }

    private String getEntryName(String param) {
        int startIndex = param.indexOf(40);
        StringBuffer buffer = new StringBuffer();
        int cnt = 1;
        for (int i = startIndex + 1; i < param.length(); ++i) {
            char ch = param.charAt(i);
            if (ch == ')' && --cnt == 0) {
                if (buffer.length() == 0) {
                    throw new IllegalArgumentException("Invalid parameter name: " + param);
                }
                return buffer.toString();
            }
            if (ch == '(') {
                ++cnt;
            }
            if (cnt <= 0) continue;
            buffer.append(ch);
        }
        throw new IllegalArgumentException("Invalid parameter name: " + param);
    }
}

