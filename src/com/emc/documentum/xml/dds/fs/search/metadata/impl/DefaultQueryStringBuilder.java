/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.fs.search.metadata.impl;

import com.emc.documentum.xml.dds.DDS;
import com.emc.documentum.xml.dds.application.Application;
import com.emc.documentum.xml.dds.exception.DDSException;
import com.emc.documentum.xml.dds.fs.search.metadata.FieldExpression;
import com.emc.documentum.xml.dds.fs.search.metadata.FieldOperator;
import com.emc.documentum.xml.dds.fs.search.metadata.LogicalExpression;
import com.emc.documentum.xml.dds.fs.search.metadata.LogicalOperator;
import com.emc.documentum.xml.dds.fs.search.metadata.MetadataField;
import com.emc.documentum.xml.dds.fs.search.metadata.MetadataFieldType;
import com.emc.documentum.xml.dds.fs.search.metadata.MetadataFieldValue;
import com.emc.documentum.xml.dds.fs.search.metadata.MetadataSearchContext;
import com.emc.documentum.xml.dds.fs.search.metadata.MetadataSearchQuery;
import com.emc.documentum.xml.dds.fs.search.metadata.QueryStringBuilder;
import com.emc.documentum.xml.dds.fs.search.metadata.Range;
import com.emc.documentum.xml.dds.fs.search.metadata.SearchExpression;
import com.emc.documentum.xml.dds.fs.search.metadata.SortExpression;
import com.emc.documentum.xml.dds.fs.search.metadata.SortOrder;
import com.emc.documentum.xml.dds.persistence.Location;
import com.emc.documentum.xml.dds.persistence.Store;
import com.emc.documentum.xml.dds.persistence.StoreChild;
import com.emc.documentum.xml.dds.structure.DDSDataSet;
import com.emc.documentum.xml.dds.structure.DDSLocale;
import com.emc.documentum.xml.dds.structure.StructureStrategy;
import com.emc.documentum.xml.dds.structure.StructureStrategyType;
import com.emc.documentum.xml.dds.structure.internal.DataStructure;
import com.emc.documentum.xml.dds.structure.internal.MetadataStructure;
import com.emc.documentum.xml.dds.structure.internal.RootStructure;
import com.emc.documentum.xml.dds.structure.strategy.DDSStructureStrategyType;
import com.emc.documentum.xml.dds.uri.URIResolver;
import com.emc.documentum.xml.dds.uri.URITarget;
import com.emc.documentum.xml.dds.user.User;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class DefaultQueryStringBuilder
implements QueryStringBuilder {
    private final MetadataSearchQuery metadataSearchQuery;
    private final User user;
    private Location contextLocation;
    private Location metadataContextLocation;
    private DDSDataSet contextDataSet;
    private DDSLocale contextLocale;
    private String contextRelativePath = "";
    private boolean contextSet;
    private final MetadataSearchContext context;
    private StructureStrategyType contextStrategyType;
    private final Map<FieldOperator, String> ordinalOperatorMap;
    private final Map<LogicalOperator, String> logicalOperatorMap;

    public DefaultQueryStringBuilder(MetadataSearchContext context, MetadataSearchQuery metadataSearchQuery, User user) {
        this.ordinalOperatorMap = new HashMap<FieldOperator, String>(){};
        this.logicalOperatorMap = new HashMap<LogicalOperator, String>(){};
        this.context = context;
        this.metadataSearchQuery = metadataSearchQuery;
        this.user = user;
    }

    @Override
    public String getQueryString() throws DDSException {
        Iterable<MetadataField> resultFields;
        this.setContext();
        StringBuilder result = new StringBuilder();
        result.append("declare namespace dds='").append("http://www.emc.com/documentum/xml/dds").append("';\n");
        Iterable<SortExpression> sortItems = this.metadataSearchQuery.getSortExpressions();
        Range range = this.metadataSearchQuery.getRange();
        SearchExpression clause = this.metadataSearchQuery.getSearchExpression();
        if (range != null) {
            result.append("fn:subsequence(\n");
        }
        if (this.contextStrategyType == DDSStructureStrategyType.DOCUMENTUM) {
            result.append("for $doc in doc('").append("CollectionMetadata");
            result.append("')\n");
            result.append(" where $doc/*[").append(this.getClauseString(clause)).append("]\n");
            result.append("and starts-with(document-uri($doc), '");
            result.append("/").append("DATA").append("/").append(this.contextDataSet.getId()).append("/");
            result.append("CollectionMetadata").append("/").append(this.contextRelativePath);
            result.append("')\n");
            if (sortItems != null && sortItems.iterator().hasNext()) {
                result.append(" order by\n");
                boolean first = true;
                for (SortExpression sortItem : sortItems) {
                    MetadataField metadataField2 = sortItem.getMetadataField();
                    MetadataFieldType metadatafieldType = sortItem.getMetadataFieldType();
                    SortOrder sortOrder = sortItem.getSortOrder();
                    if (!first) {
                        result.append(",");
                    }
                    if (metadatafieldType != null) {
                        switch (metadatafieldType) {
                            case STRING: {
                                result.append(" $doc/*/").append(metadataField2.getName());
                                break;
                            }
                            case NUMBER: {
                                result.append(" xs:double(").append("$doc/*/").append(metadataField2.getName()).append(")");
                                break;
                            }
                            case BOOLEAN: {
                                result.append(" xs:boolean(").append("$doc/*/").append(metadataField2.getName()).append(")");
                                break;
                            }
                            case DATE_TIME: {
                                result.append(" xs:dateTime(").append("$doc/*/").append(metadataField2.getName()).append(")");
                            }
                        }
                    } else {
                        result.append(" $doc/*/").append(metadataField2.getName());
                    }
                    if (sortOrder != null) {
                        if (sortOrder.equals((Object)SortOrder.DESCENDING)) {
                            result.append(" descending\n");
                        } else {
                            result.append(" ascending\n");
                        }
                    }
                    first = false;
                }
            }
            result.append(" return(\n");
            resultFields = this.metadataSearchQuery.getResultFields();
            if (resultFields == null || !resultFields.iterator().hasNext()) {
                result.append("<results/>");
            } else {
                result.append("(");
                int i = 1;
                for (MetadataField metadataField2 : resultFields) {
                    result.append("let ").append("$").append("var").append(i).append(" := ");
                    result.append("$doc/*/").append(metadataField2.getName()).append("/text()\n");
                    ++i;
                }
                result.append("return <results>\n");
                i = 1;
                for (MetadataField metadataField2 : resultFields) {
                    result.append("<result>");
                    result.append("<name>").append(metadataField2.getName()).append("</name>\n");
                    result.append("<value>").append("{$").append("var").append(i).append("}").append("</value>\n");
                    result.append("</result>");
                    ++i;
                }
                result.append("</results>)");
            }
            result.append(",\n(let $se := xhive:metadata($doc, 'dds:subscription-element')\n");
            result.append("let $resultDoc := doc('");
            result.append("Collection");
            result.append("')[xhive:metadata(., 'dds:subscription-element') = $se]\n");
            result.append("return ($resultDoc, dds:generate-uri($resultDoc)))\n");
            result.append(")");
        } else {
            result.append("for $doc in doc('").append("')\n");
            result.append("where ").append(this.getClauseString(clause)).append("\n");
            result.append("and starts-with(document-uri($doc), '");
            result.append("/").append("DATA").append("/").append(this.contextDataSet.getId()).append("/");
            result.append(this.contextRelativePath);
            result.append("')\n");
            if (sortItems != null && sortItems.iterator().hasNext()) {
                result.append(" order by\n");
                boolean first = true;
                for (SortExpression sortItem : sortItems) {
                    MetadataField metadataField3 = sortItem.getMetadataField();
                    MetadataFieldType metadatafieldType = sortItem.getMetadataFieldType();
                    SortOrder sortOrder = sortItem.getSortOrder();
                    if (!first) {
                        result.append(",");
                    }
                    if (metadatafieldType != null) {
                        switch (metadatafieldType) {
                            case STRING: {
                                result.append(" xhive:metadata($doc, \"").append(metadataField3.getName()).append("\")");
                                break;
                            }
                            case NUMBER: {
                                result.append(" xs:double(").append("xhive:metadata($doc, \"").append(metadataField3.getName()).append("\")").append(")");
                                break;
                            }
                            case BOOLEAN: {
                                result.append(" xs:boolean(").append("xhive:metadata($doc, \"").append(metadataField3.getName()).append("\")").append(")");
                                break;
                            }
                            case DATE_TIME: {
                                result.append(" xs:dateTime(").append("xhive:metadata($doc, \"").append(metadataField3.getName()).append("\")").append(")");
                            }
                        }
                    } else {
                        result.append(" xhive:metadata($doc, \"").append(metadataField3.getName()).append("\")");
                    }
                    if (sortOrder != null) {
                        if (sortOrder.equals((Object)SortOrder.DESCENDING)) {
                            result.append(" descending\n");
                        } else {
                            result.append(" ascending\n");
                        }
                    }
                    first = false;
                }
            }
            result.append(" return(\n");
            resultFields = this.metadataSearchQuery.getResultFields();
            if (resultFields == null || !resultFields.iterator().hasNext()) {
                result.append("<results/>");
            } else {
                result.append("(");
                int i = 1;
                for (MetadataField metadataField3 : resultFields) {
                    result.append("let ").append("$").append("var").append(i).append(" := ");
                    result.append("xhive:metadata($doc, \"").append(metadataField3.getName()).append("\")\n");
                    ++i;
                }
                result.append("return <results>");
                i = 1;
                for (MetadataField metadataField3 : resultFields) {
                    result.append("<result>");
                    result.append("<name>").append(metadataField3.getName()).append("</name>");
                    result.append("<value>").append("{$").append("var").append(i).append("}").append("</value>");
                    result.append("</result>");
                    ++i;
                }
                result.append("</results>)");
            }
            result.append(",\n $doc,\n dds:generate-uri($doc)");
            result.append(")");
        }
        if (range != null) {
            int begin = Math.max(0, 1 + 3 * (range.getBeginIndex() - 1));
            int length = 3 * range.getLength();
            result.append(",\n" + begin + ",\n" + length + ")");
        }
        return result.toString();
    }

    private void setContext() throws DDSException {
        if (!this.contextSet) {
            String id = this.context.getURI();
            Application application = DDS.getApplication();
            URITarget target = application.getDefaultURIResolver().resolveURI(id, this.user);
            this.contextDataSet = target.getDataSet();
            this.contextLocale = target.getLocale();
            StoreChild storeChild = target.getStoreChild();
            if (!storeChild.isLocation()) {
                throw new DDSException(id + " does not identify a location");
            }
            this.contextLocation = (Location)storeChild;
            DDSDataSet dataSet = target.getDataSet();
            StructureStrategy structureStrategy = dataSet.getStructureStrategy();
            this.contextStrategyType = structureStrategy.getType();
            DataStructure dataStructure = new DataStructure(dataSet);
            List<String> relativePath = dataStructure.getRelativePath(this.contextLocation);
            String separator = dataSet.getRootStructure().getStore().getSeparator();
            StringBuilder newRelativePath = new StringBuilder("");
            for (String component : relativePath) {
                newRelativePath.append(component).append(separator);
            }
            this.contextRelativePath = newRelativePath.toString();
            if (this.contextStrategyType == DDSStructureStrategyType.DOCUMENTUM) {
                this.contextStrategyType = DDSStructureStrategyType.DOCUMENTUM;
                MetadataStructure metadataStructure = new MetadataStructure(dataSet);
                this.metadataContextLocation = metadataStructure.getLocation(this.contextRelativePath);
            } else {
                this.contextStrategyType = DDSStructureStrategyType.DDS;
                this.metadataContextLocation = this.contextLocation;
            }
        }
    }

    private String getClauseString(SearchExpression clause) throws DDSException {
        this.setContext();
        if (clause instanceof FieldExpression) {
            return this.getAtomicClauseString((FieldExpression)clause);
        }
        return this.getCompoundClauseString((LogicalExpression)clause);
    }

    private String getAtomicClauseString(FieldExpression atomicClause) {
        StringBuilder result = new StringBuilder();
        MetadataField metadataField = atomicClause.getMetadataField();
        FieldOperator operator = atomicClause.getOperator();
        MetadataFieldValue value = atomicClause.getMetadataFieldValue();
        result.append("(");
        if (this.contextStrategyType == DDSStructureStrategyType.DOCUMENTUM) {
            result.append(metadataField.getName());
        } else {
            result.append("xhive:metadata($doc, \"").append(metadataField.getName()).append("\")");
        }
        result.append(" ");
        result.append(this.ordinalOperatorMap.get((Object)operator));
        result.append(" ");
        StringBuilder valueOperand = new StringBuilder();
        switch (value.getMetadataFieldType()) {
            case STRING: {
                valueOperand.append("\"").append(value.getStringValue()).append("\"");
                break;
            }
            case NUMBER: {
                valueOperand.append("xs:double(\"").append(value.getStringValue()).append("\")");
                break;
            }
            case BOOLEAN: {
                valueOperand.append("xs:boolean(\"").append(value.getStringValue()).append("\")");
                break;
            }
            case DATE_TIME: {
                valueOperand.append("xs:dateTime(\"").append(value.getStringValue()).append("\")");
            }
        }
        result.append(valueOperand.toString());
        result.append(")");
        return result.toString();
    }

    private String getCompoundClauseString(LogicalExpression compoundClause) throws DDSException {
        StringBuilder result = new StringBuilder();
        Iterable<SearchExpression> searchExpressions = compoundClause.getSearchExpressions();
        LogicalOperator operator = compoundClause.getOperator();
        if (searchExpressions != null && searchExpressions.iterator().hasNext()) {
            Iterator<SearchExpression> iterator = searchExpressions.iterator();
            result.append("(");
            result.append(this.getClauseString(iterator.next()));
            while (iterator.hasNext()) {
                result.append(" ").append(this.logicalOperatorMap.get((Object)operator)).append(" ");
                result.append(this.getClauseString(iterator.next()));
            }
            result.append(")");
        }
        return result.toString();
    }

}

