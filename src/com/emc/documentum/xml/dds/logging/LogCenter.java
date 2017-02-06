/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.logging;

import com.emc.documentum.xml.dds.logging.FileLogger;
import com.emc.documentum.xml.dds.logging.Logger;
import com.emc.documentum.xml.dds.logging.SystemStreamsLogger;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

public final class LogCenter {
    private static Logger defaultLogger;
    private static boolean debug;
    private static String defaultLogPath;
    private static String defaultLogSuffix;
    private static Map<String, Logger> registeredClassNames;
    private static Map<String, Logger> loggersByPrefix;
    private static Map<String, String> prefixMapping;
    private static Map<String, Boolean> debugMapping;

    public static void setDefaultLogger(Logger logger) {
        defaultLogger = logger;
    }

    public static Logger getDefaultLogger() {
        return defaultLogger;
    }

    public static void setDefaultLogPath(String defaultLogPath) {
        LogCenter.defaultLogPath = defaultLogPath;
        File dir = new File(LogCenter.uriForPath(defaultLogPath));
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    public static void setDefaultSuffix(String suffix) {
        defaultLogSuffix = suffix;
    }

    public static void activateDebug() {
        debug = true;
    }

    public static void deactivateDebug() {
        debug = false;
    }

    public static boolean debugStatus() {
        return debug;
    }

    public static Map<String, String> getPrefixMapping() {
        return prefixMapping;
    }

    public static Map<String, Boolean> getDebugMapping() {
        return debugMapping;
    }

    public static void register(String className, String prefix, boolean debugLevel) {
        if (prefix != null) {
            prefixMapping.put(className, prefix);
        }
        debugMapping.put(className, debugLevel);
        registeredClassNames.put(className, LogCenter.createLoggerForClassName(className));
    }

    public static void unregister(String className) {
        String prefix = prefixMapping.get(className);
        prefixMapping.remove(className);
        Logger logger = registeredClassNames.get(className);
        if (logger != null) {
            registeredClassNames.remove(className);
            debugMapping.remove(className);
            if (!registeredClassNames.containsValue(logger)) {
                logger.close();
                loggersByPrefix.remove(prefix);
            }
        }
    }

    public static void shutdown() {
        Object[] loggables;
        for (Object loggable : loggables = registeredClassNames.keySet().toArray()) {
            String loggableName = (String)loggable;
            LogCenter.unregister(loggableName);
        }
        defaultLogger.close();
    }

    public static void setDebug(String className, boolean debugLevel) {
        debugMapping.put(className, debugLevel);
    }

    private static Logger createLoggerForClassName(String className) {
        String prefix = prefixMapping.get(className);
        Logger logger = null;
        if (prefix == null || "".equals(prefix)) {
            logger = defaultLogger;
        } else {
            logger = loggersByPrefix.get(prefix);
            if (logger == null) {
                logger = new FileLogger(defaultLogPath, prefix, defaultLogSuffix);
                loggersByPrefix.put(prefix, logger);
            }
        }
        return logger;
    }

    private static Logger getLoggerForSender(Object sender) {
        Logger logger = registeredClassNames.get(sender.getClass().getName());
        if (logger == null) {
            logger = defaultLogger;
        }
        return logger;
    }

    public static void log(String msg) {
        defaultLogger.log(msg);
    }

    public static void warning(String msg) {
        defaultLogger.warning(msg);
    }

    public static void error(String msg) {
        defaultLogger.error(msg);
    }

    public static void debug(String msg) {
        if (debug) {
            defaultLogger.debug(msg);
        }
    }

    public static void exception(String msg, Throwable throwable) {
        defaultLogger.exception(msg, throwable);
    }

    public static void log(Object sender, String msg) {
        Logger logger = LogCenter.getLoggerForSender(sender);
        logger.log(sender, msg);
    }

    public static void warning(Object sender, String msg) {
        Logger logger = LogCenter.getLoggerForSender(sender);
        logger.warning(sender, msg);
    }

    public static void error(Object sender, String msg) {
        Logger logger = LogCenter.getLoggerForSender(sender);
        logger.error(sender, msg);
    }

    public static void debug(Object sender, String msg) {
        Boolean debugLevel = debugMapping.get(sender.getClass().getName());
        if (debugLevel == null) {
            debugLevel = false;
        }
        Logger logger = LogCenter.getLoggerForSender(sender);
        if (debug || debugLevel.booleanValue()) {
            logger.debug(sender, msg);
        }
    }

    public static void exception(Object sender, Throwable throwable) {
        Logger logger = LogCenter.getLoggerForSender(sender);
        logger.exception(sender, throwable);
    }

    public static void exception(Object sender, String msg, Throwable throwable) {
        Logger logger = LogCenter.getLoggerForSender(sender);
        logger.exception(sender, msg, throwable);
    }

    private static URI uriForPath(String path) {
        try {
            return new URI("file:///" + (path == null ? "." : path));
        }
        catch (URISyntaxException use) {
            LogCenter.error("Bad Filename Syntax !");
            return null;
        }
    }

    private LogCenter() {
    }

    static {
        registeredClassNames = new Hashtable<String, Logger>();
        loggersByPrefix = new Hashtable<String, Logger>();
        prefixMapping = new Hashtable<String, String>();
        debugMapping = new Hashtable<String, Boolean>();
        defaultLogger = new SystemStreamsLogger();
    }
}

