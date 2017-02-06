/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  com.google.gwt.core.client.GWT
 */
package com.emc.documentum.xml.dds.gwt.client.i18n;

import com.emc.documentum.xml.dds.gwt.client.i18n.DDSErrors;
import com.emc.documentum.xml.dds.gwt.client.i18n.DDSLabels;
import com.emc.documentum.xml.dds.gwt.client.i18n.DDSMessages;
import com.google.gwt.core.client.GWT;
import java.io.PrintStream;

public final class Locale {
    private static DDSLabels labels;
    private static DDSMessages messages;
    private static DDSErrors errors;

    private Locale() {
    }

    public static DDSLabels getLabels() {
        if (labels == null) {
            labels = (DDSLabels)GWT.create(DDSLabels.class);
        }
        return labels;
    }

    public static DDSMessages getMessages() {
        if (messages == null) {
            messages = (DDSMessages)GWT.create(DDSMessages.class);
        }
        return messages;
    }

    public static DDSErrors getErrors() {
        if (errors == null) {
            errors = (DDSErrors)GWT.create(DDSErrors.class);
        }
        return errors;
    }

    public static String getIndexType(int type) {
        if (labels == null) {
            Locale.getLabels();
        }
        switch (type) {
            case 7: {
                return labels.fullTextIndex();
            }
            case 4: {
                return labels.valueIndex();
            }
            case 10: {
                return labels.pathValueIndex();
            }
            case 8: {
                return labels.metadataIndex();
            }
            case 9: {
                return labels.metadataFullTextIndex();
            }
            case 2: {
                return labels.libraryNameIndex();
            }
            case 1: {
                return labels.libraryIdIndex();
            }
        }
        return labels.unknown();
    }

    public static String getValueIndexType(int type) {
        if (labels == null) {
            Locale.getLabels();
        }
        switch (type) {
            case 1536: {
                return labels.idxOptionTypeDate();
            }
            case 1280: {
                return labels.idxOptionTypeDatetime();
            }
            case 1024: {
                return labels.idxOptionTypeDouble();
            }
            case 768: {
                return labels.idxOptionTypeFloat();
            }
            case 256: {
                return labels.idxOptionTypeInt();
            }
            case 512: {
                return labels.idxOptionTypeLong();
            }
            case 1792: {
                return labels.idxOptionTypeTime();
            }
            case 0: {
                return labels.idxOptionTypeString();
            }
            case 3584: {
                return labels.idxOptionTypeDayTimeDuration();
            }
            case 3328: {
                return labels.idxOptionTypeYearMonthDuration();
            }
        }
        System.out.println("Unknown! " + type);
        return labels.unknown();
    }

    public static String getNodeTypeString(short nodeType) {
        if (labels == null) {
            Locale.getLabels();
        }
        switch (nodeType) {
            case 1: {
                return labels.element();
            }
            case 2: {
                return labels.attribute();
            }
            case 3: {
                return labels.text();
            }
            case 4: {
                return labels.cdataSection();
            }
            case 5: {
                return labels.entityReference();
            }
            case 6: {
                return labels.entity();
            }
            case 7: {
                return labels.processingInstruction();
            }
            case 8: {
                return labels.comment();
            }
            case 9: {
                return labels.document();
            }
            case 10: {
                return labels.documentType();
            }
            case 11: {
                return labels.documentFragment();
            }
            case 12: {
                return labels.notation();
            }
            case 201: {
                return labels.library();
            }
            case 202: {
                return labels.namespace();
            }
            case 203: {
                return labels.blob();
            }
        }
        return labels.unknown();
    }
}

