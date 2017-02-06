/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.structure.internal;

import com.emc.documentum.xml.dds.persistence.Location;
import com.emc.documentum.xml.dds.structure.DDSDataSet;
import com.emc.documentum.xml.dds.structure.DDSLocale;
import com.emc.documentum.xml.dds.structure.Structure;
import com.emc.documentum.xml.dds.structure.StructureType;
import com.emc.documentum.xml.dds.structure.exception.StructureException;
import com.emc.documentum.xml.dds.structure.internal.AbstractStructure;
import com.emc.documentum.xml.dds.structure.internal.RootStructure;
import java.util.List;
import java.util.Locale;

public class DDSLocaleImpl
extends AbstractStructure
implements DDSLocale {
    private final Structure parent;
    private final Locale locale;
    private transient String cachedId;

    public DDSLocaleImpl(Locale locale, Structure parent) {
        this.parent = parent;
        this.locale = locale;
    }

    public DDSLocaleImpl(String fullLocaleString, Structure parent) throws StructureException {
        this.parent = parent;
        this.locale = this.localeForFullString(fullLocaleString);
        if (!this.locale.toString().equals(fullLocaleString)) {
            this.cachedId = fullLocaleString;
        }
    }

    public DDSLocaleImpl(Structure parent, String language) {
        this.parent = parent;
        this.locale = new Locale(language);
    }

    public DDSLocaleImpl(Structure parent, String language, String country) {
        this.parent = parent;
        this.locale = new Locale(language, country);
    }

    public DDSLocaleImpl(Structure parent, String language, String country, String variant) {
        this.parent = parent;
        this.locale = new Locale(language, country, variant);
    }

    @Override
    public DDSDataSet getDataSet() {
        return (DDSDataSet)this.parent.getParentStructure();
    }

    @Override
    public Locale getJavaLocale() {
        return this.locale;
    }

    @Override
    public String getId() {
        return this.cachedId == null ? this.locale.toString() : this.cachedId;
    }

    @Override
    public StructureType getType() {
        return StructureType.LOCALE;
    }

    @Override
    public RootStructure getRootStructure() {
        return this.parent.getRootStructure();
    }

    @Override
    public Location getRootLocation() {
        Location result = this.parent.getRootLocation(this);
        result.setContext(this);
        return result;
    }

    @Override
    public Structure getParentStructure() {
        return this.parent;
    }

    @Override
    public Location getRootLocation(Structure child) {
        return null;
    }

    @Override
    public Structure resolveContext(List<String> relativePathComponents) {
        return this;
    }

    private Locale localeForFullString(String fullLocaleString) throws StructureException {
        String[] components = fullLocaleString.split("_");
        switch (components.length) {
            case 1: {
                return new Locale(components[0]);
            }
            case 2: {
                return new Locale(components[0], components[1]);
            }
            case 3: {
                return new Locale(components[0], components[1], components[2]);
            }
        }
        throw new StructureException(fullLocaleString);
    }
}

