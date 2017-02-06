/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.application;

import com.emc.documentum.xml.dds.configuration.Configurable;
import com.emc.documentum.xml.dds.operation.exception.OperationException;
import com.emc.documentum.xml.dds.persistence.Store;
import com.emc.documentum.xml.dds.structure.DDSDataSet;
import com.emc.documentum.xml.dds.structure.DDSLocale;
import com.emc.documentum.xml.dds.structure.StructureStrategy;
import com.emc.documentum.xml.dds.structure.exception.DataSetAlreadyExistsException;
import com.emc.documentum.xml.dds.structure.exception.DataSetNotFoundException;
import com.emc.documentum.xml.dds.structure.exception.LocaleAlreadyExistsException;
import com.emc.documentum.xml.dds.structure.exception.LocaleNotFoundException;
import com.emc.documentum.xml.dds.structure.internal.ApplicationStructure;
import java.util.List;
import java.util.Locale;

public interface StructureManager
extends Configurable {
    public DDSDataSet getDataSet(String var1);

    public List<DDSDataSet> getDataSets();

    public DDSDataSet getDefaultDataSet();

    public void setDefaultDataSet(DDSDataSet var1);

    public DDSDataSet addDataSet(String var1, String var2, Store var3) throws DataSetNotFoundException;

    public void removeDataSet(String var1) throws DataSetNotFoundException;

    public DDSDataSet createDataSet(String var1, String var2, Store var3, boolean var4, StructureStrategy var5) throws DataSetAlreadyExistsException, OperationException;

    public DDSDataSet createDataSet(String var1, String var2, Store var3, String var4, StructureStrategy var5) throws DataSetAlreadyExistsException, OperationException;

    public DDSLocale getLocale(DDSDataSet var1, String var2);

    public DDSLocale getLocale(DDSDataSet var1, Locale var2);

    public List<DDSLocale> getLocales(DDSDataSet var1);

    public List<DDSLocale> getLocales(DDSDataSet var1, String var2);

    public List<DDSLocale> getLocales(DDSDataSet var1, String var2, String var3);

    public DDSLocale addLocale(DDSDataSet var1, String var2) throws LocaleNotFoundException;

    public void removeLocale(DDSDataSet var1, String var2) throws LocaleNotFoundException;

    public DDSLocale createLocale(DDSDataSet var1, Locale var2) throws LocaleAlreadyExistsException, OperationException;

    public DDSLocale createLocale(DDSDataSet var1, String var2) throws LocaleAlreadyExistsException, OperationException;

    public DDSLocale createLocale(DDSDataSet var1, String var2, String var3) throws LocaleAlreadyExistsException, OperationException;

    public DDSLocale createLocale(DDSDataSet var1, String var2, String var3, String var4) throws LocaleAlreadyExistsException, OperationException;

    public ApplicationStructure getApplicationStructure();
}

