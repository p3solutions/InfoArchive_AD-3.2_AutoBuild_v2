/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.xbase.logbase.internal;

import com.emc.documentum.xml.dds.persistence.Session;
import com.emc.documentum.xml.dds.xbase.XBaseFile;
import com.emc.documentum.xml.dds.xbase.logbase.LogBaseEntry;
import com.emc.documentum.xml.dds.xbase.logbase.LogBaseFile;
import java.util.Map;
import java.util.Set;
import org.w3c.dom.Node;

public final class LogBaseEntryImpl
implements LogBaseEntry {
    private static final int STRING = 0;
    private static final int NODE = 1;
    private static final int MAP = 2;
    private final int mode;
    private String xmlFragment;
    private Node xmlNode;
    private Map<String, String> pairs;

    public LogBaseEntryImpl(String xmlFragment) {
        this.xmlFragment = xmlFragment;
        this.mode = 0;
    }

    public LogBaseEntryImpl(Node xmlNode) {
        this.xmlNode = xmlNode;
        this.mode = 1;
    }

    public LogBaseEntryImpl(Map<String, String> pairs) {
        this.pairs = pairs;
        this.mode = 2;
    }

    @Override
    public void write(Session session, XBaseFile xBaseFile) {
        LogBaseFile logBaseFile = (LogBaseFile)xBaseFile;
        switch (this.mode) {
            case 0: {
                logBaseFile.addAsString(session, this.xmlFragment);
                break;
            }
            case 1: {
                logBaseFile.addAsNode(session, this.xmlNode);
                break;
            }
            case 2: {
                logBaseFile.createEntry(session);
                for (Map.Entry<String, String> pair : this.pairs.entrySet()) {
                    logBaseFile.addPair(session, pair.getKey(), pair.getValue());
                }
                logBaseFile.closeEntry(session);
                break;
            }
        }
    }
}

