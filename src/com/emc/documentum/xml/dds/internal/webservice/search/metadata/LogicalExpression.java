/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.internal.webservice.search.metadata;

import com.emc.documentum.xml.dds.fs.search.metadata.LogicalOperator;
import com.emc.documentum.xml.dds.internal.webservice.search.metadata.FieldExpression;
import com.emc.documentum.xml.dds.internal.webservice.search.metadata.SearchExpression;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(name="LogicalExpression", namespace="http://metadata.search.datamodel.dds.xml.documentum.emc.com/")
@XmlAccessorType(value=XmlAccessType.FIELD)
public class LogicalExpression
extends SearchExpression {
    @XmlElement(name="SearchExpression", namespace="http://metadata.search.datamodel.dds.xml.documentum.emc.com/")
    private List<SearchExpression> searchExpressions;
    @XmlElement(name="Operator", namespace="http://metadata.search.datamodel.dds.xml.documentum.emc.com/")
    private LogicalOperator operator;

    public LogicalExpression() {
    }

    public LogicalExpression(List<SearchExpression> searchExpressions, LogicalOperator operator) {
        this.searchExpressions = searchExpressions;
        this.operator = operator;
    }

    public List<SearchExpression> getSearchExpressions() {
        return this.searchExpressions;
    }

    public void setSearchExpressions(List<SearchExpression> searchExpressions) {
        this.searchExpressions = searchExpressions;
    }

    public LogicalOperator getOperator() {
        return this.operator;
    }

    public void setOperator(LogicalOperator operator) {
        this.operator = operator;
    }

    public com.emc.documentum.xml.dds.fs.search.metadata.LogicalExpression getLogicalExpression() {
        return new com.emc.documentum.xml.dds.fs.search.metadata.LogicalExpression(){

            @Override
            public Iterable<com.emc.documentum.xml.dds.fs.search.metadata.SearchExpression> getSearchExpressions() {
                if (LogicalExpression.this.searchExpressions != null) {
                    return new Iterable<com.emc.documentum.xml.dds.fs.search.metadata.SearchExpression>(){

                        @Override
                        public Iterator<com.emc.documentum.xml.dds.fs.search.metadata.SearchExpression> iterator() {
                            return new Iterator<com.emc.documentum.xml.dds.fs.search.metadata.SearchExpression>(){
                                private Iterator<SearchExpression> iterator;

                                @Override
                                public boolean hasNext() {
                                    return this.iterator.hasNext();
                                }

                                @Override
                                public com.emc.documentum.xml.dds.fs.search.metadata.SearchExpression next() {
                                    SearchExpression searchExpression = this.iterator.next();
                                    if (searchExpression instanceof FieldExpression) {
                                        return ((FieldExpression)searchExpression).getFieldExpression();
                                    }
                                    return ((LogicalExpression)searchExpression).getLogicalExpression();
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
            public LogicalOperator getOperator() {
                return LogicalExpression.this.operator;
            }

        };
    }

}

