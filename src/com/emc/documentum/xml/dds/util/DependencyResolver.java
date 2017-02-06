/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class DependencyResolver {
    private DependencyResolver() {
    }

    public static List<String> resolveDependencies(Map<String, List<String>> dependencyMap) {
        HashMap<String, List<String>> dependencyMapCopy = new HashMap<String, List<String>>();
        for (Map.Entry<String, List<String>> entry : dependencyMap.entrySet()) {
            dependencyMapCopy.put(entry.getKey(), new ArrayList(entry.getValue()));
        }
        ArrayList<String> result = new ArrayList<String>();
        while (dependencyMapCopy.size() > 0) {
            String noDependencyItem = DependencyResolver.getItemWithNoDependencies(dependencyMapCopy);
            if (noDependencyItem == null) {
                return null;
            }
            result.add(noDependencyItem);
            dependencyMapCopy.remove(noDependencyItem);
            DependencyResolver.removeDependency(dependencyMapCopy, noDependencyItem);
        }
        return result;
    }

    private static String getItemWithNoDependencies(Map<String, List<String>> dependencyMap) {
        for (Map.Entry<String, List<String>> entry : dependencyMap.entrySet()) {
            if (entry.getValue().size() != 0) continue;
            return entry.getKey();
        }
        return null;
    }

    private static void removeDependency(Map<String, List<String>> dependencyMap, String dependency) {
        for (Map.Entry<String, List<String>> entry : dependencyMap.entrySet()) {
            List<String> dependencyList = entry.getValue();
            if (!dependencyList.contains(dependency)) continue;
            dependencyList.remove(dependency);
        }
    }
}

