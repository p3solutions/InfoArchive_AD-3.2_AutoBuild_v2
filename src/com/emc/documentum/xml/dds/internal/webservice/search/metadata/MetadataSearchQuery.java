/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.internal.webservice.search.metadata;

import com.emc.documentum.xml.dds.internal.webservice.search.metadata.FieldExpression;
import com.emc.documentum.xml.dds.internal.webservice.search.metadata.LogicalExpression;
import com.emc.documentum.xml.dds.internal.webservice.search.metadata.MetadataField;
import com.emc.documentum.xml.dds.internal.webservice.search.metadata.Range;
import com.emc.documentum.xml.dds.internal.webservice.search.metadata.SearchExpression;
import com.emc.documentum.xml.dds.internal.webservice.search.metadata.SortExpression;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;

@XmlType(name="MetadataSearchQuery", namespace="http://metadata.search.datamodel.dds.xml.documentum.emc.com/")
@XmlAccessorType(value=XmlAccessType.FIELD)
public class MetadataSearchQuery {
    @XmlElements(value={@XmlElement(name="SortExpression")})
    private List<SortExpression> sortExpressions;
    @XmlElements(value={@XmlElement(name="ResultField")})
    private List<MetadataField> resultFields;
    @XmlElement(name="Range", namespace="http://metadata.search.datamodel.dds.xml.documentum.emc.com/")
    private Range range;
    @XmlElement(name="SearchExpression", namespace="http://metadata.search.datamodel.dds.xml.documentum.emc.com/")
    private SearchExpression searchExpression;

    public MetadataSearchQuery() {
    }

    public MetadataSearchQuery(SearchExpression searchExpression, Range range, List<SortExpression> sortExpressions, List<MetadataField> resultFields) {
        this.searchExpression = searchExpression;
        this.range = range;
        this.sortExpressions = sortExpressions;
        this.resultFields = resultFields;
    }

    public Range getRange() {
        return this.range;
    }

    public void setRange(Range range) {
        this.range = range;
    }

    public List<SortExpression> getSortExpressions() {
        return this.sortExpressions;
    }

    public void setSortExpressions(List<SortExpression> sortExpressions) {
        this.sortExpressions = sortExpressions;
    }

    public SearchExpression getSearchExpression() {
        return this.searchExpression;
    }

    public void setSearchExpression(SearchExpression searchExpression) {
        this.searchExpression = searchExpression;
    }

    public List<MetadataField> getResultFields() {
        return this.resultFields;
    }

    public void setResultFields(List<MetadataField> resultFields) {
        this.resultFields = resultFields;
    }

    public com.emc.documentum.xml.dds.fs.search.metadata.MetadataSearchQuery getMetadataSearchQuery() {
        return new com.emc.documentum.xml.dds.fs.search.metadata.MetadataSearchQuery(){

            @Override
            public com.emc.documentum.xml.dds.fs.search.metadata.Range getRange() {
                if (MetadataSearchQuery.this.range != null) {
                    return MetadataSearchQuery.this.range.getRange();
                }
                return null;
            }

            @Override
            public Iterable<com.emc.documentum.xml.dds.fs.search.metadata.SortExpression> getSortExpressions() {
                if (MetadataSearchQuery.this.sortExpressions != null) {
                    return new Iterable<com.emc.documentum.xml.dds.fs.search.metadata.SortExpression>(){

                        @Override
                        public Iterator<com.emc.documentum.xml.dds.fs.search.metadata.SortExpression> iterator() {
                            return new Iterator<com.emc.documentum.xml.dds.fs.search.metadata.SortExpression>(){
                                private Iterator<SortExpression> iterator;

                                @Override
                                public boolean hasNext() {
                                    return this.iterator.hasNext();
                                }

                                @Override
                                public com.emc.documentum.xml.dds.fs.search.metadata.SortExpression next() {
                                    SortExpression sortItem = this.iterator.next();
                                    return sortItem.getSortItem();
                                }

                                @Override
                                public void remove() {
                                }
                            };
                        }

                    };
                }
                return null;
            }

            @Override
            public Iterable<com.emc.documentum.xml.dds.fs.search.metadata.MetadataField> getResultFields() {
                if (MetadataSearchQuery.this.resultFields != null) {
                    return new Iterable<com.emc.documentum.xml.dds.fs.search.metadata.MetadataField>(){

                        @Override
                        public Iterator<com.emc.documentum.xml.dds.fs.search.metadata.MetadataField> iterator() {
                            return new Iterator<com.emc.documentum.xml.dds.fs.search.metadata.MetadataField>(){
                                private Iterator<MetadataField> iterator;

                                @Override
                                public boolean hasNext() {
                                    return this.iterator.hasNext();
                                }

                                @Override
                                public com.emc.documentum.xml.dds.fs.search.metadata.MetadataField next() {
                                    MetadataField resultField = this.iterator.next();
                                    return resultField.getMetadataField();
                                }

                                @Override
                                public void remove() {
                                }
                            };
                        }

                    };
                }
                return null;
            }

            @Override
            public com.emc.documentum.xml.dds.fs.search.metadata.SearchExpression getSearchExpression() {
                if (MetadataSearchQuery.this.searchExpression != null) {
                    if (MetadataSearchQuery.this.searchExpression instanceof FieldExpression) {
                        return ((FieldExpression)MetadataSearchQuery.this.searchExpression).getFieldExpression();
                    }
                    return ((LogicalExpression)MetadataSearchQuery.this.searchExpression).getLogicalExpression();
                }
                return null;
            }

        };
    }

}

