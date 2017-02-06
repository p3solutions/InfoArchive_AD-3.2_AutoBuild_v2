/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  com.xhive.core.interfaces.XhiveSessionIf
 *  com.xhive.error.XhiveDeadlockException
 */
package com.emc.documentum.xml.dds.operation.framework;

import com.emc.documentum.xml.dds.application.Application;
import com.emc.documentum.xml.dds.logging.LogCenter;
import com.emc.documentum.xml.dds.operation.exception.OperationFailedException;
import com.emc.documentum.xml.dds.operation.exception.RollbackFailedException;
import com.emc.documentum.xml.dds.operation.exception.RollbackNotAvailableException;
import com.emc.documentum.xml.dds.operation.framework.Operation;
import com.emc.documentum.xml.dds.operation.framework.OperationExecutable;
import com.emc.documentum.xml.dds.persistence.Session;
import com.emc.documentum.xml.dds.persistence.SessionStoreUserStrategy;
import com.emc.documentum.xml.dds.persistence.Store;
import com.emc.documentum.xml.dds.persistence.StoreUser;
import com.emc.documentum.xml.dds.persistence.exception.DeadlockException;
import com.emc.documentum.xml.dds.persistence.exception.StoreSpecificException;
import com.emc.documentum.xml.dds.persistence.xdb.internal.ReplicatedXDBSession;
import com.emc.documentum.xml.dds.persistence.xdb.internal.XDBSession;
import com.emc.documentum.xml.dds.user.User;
import com.xhive.core.interfaces.XhiveSessionIf;
import com.xhive.error.XhiveDeadlockException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Executor {
    private static final int NROFRETRIES = 99;
    private final Application application;
    private final SessionStoreUserStrategy strategy;

    public static final <P extends Operation<T>, T> OperationExecutable<P, T> createExecutable(Application application, Operation<T> operation) {
        String className = operation.getExecutableClassName();
        if (className == null) {
            className = operation.getClass().getName() + "Executable";
        }
        try {
            OperationExecutable result = (OperationExecutable)Class.forName(className).newInstance();
            result.setOperation(operation);
            result.setApplication(application);
            return result;
        }
        catch (Exception e) {
            LogCenter.exception("Problem creating Executable " + className, (Throwable)e);
            return null;
        }
    }

    public Executor(Application application, SessionStoreUserStrategy strategy) {
        this.application = application;
        this.strategy = strategy;
    }

    /*
     * Loose catch block
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     * Lifted jumps to return sites
     */
    public <T, P> T execute(User user, Operation<T> operation) throws OperationFailedException {
        List<String> storeAliases = operation.getStoreAliases();
        HashMap<String, Session> sessionMap = new HashMap<String, Session>();
        for (String storeAlias : storeAliases) {
            try {
                sessionMap.put(storeAlias, this.application.getStore(storeAlias).getSession(this.strategy.getStoreUser(this.application, user, storeAlias), operation.isReadOnly(storeAlias)));
                continue;
            }
            catch (StoreSpecificException sse) {
                throw new OperationFailedException(sse);
            }
        }
        OperationExecutable<Operation<T>, T> executable = Executor.createExecutable(this.application, operation);
        int counter = 0;
        do {
            try {
                executable.beforeRun();
                for (Session session : sessionMap.values()) {
                    session.begin();
                }
                T result = executable.run(sessionMap);
                executable.afterRun();
                Iterator<Session> i$ = sessionMap.values().iterator();
                while (i$.hasNext()) {
                    Session session2 = i$.next();
                    session2.commit();
                }
                return result;
            }
            catch (DeadlockException de) {
                if (counter == 99) {
                    String string;
                    this.rollback(operation, sessionMap, executable, de, true);
                    if (operation.getId() == null) {
                        string = operation.getClass().getName();
                        throw new OperationFailedException("Operation " + string + " failed :", de);
                    }
                    string = operation.getId();
                    throw new OperationFailedException("Operation " + string + " failed :", de);
                }
                this.rollback(operation, sessionMap, executable, de, false);
                ++counter;
                continue;
            }
            catch (XhiveDeadlockException xde) {
                if (counter == 99) {
                    String string;
                    this.rollback(operation, sessionMap, executable, (Throwable)xde, true);
                    if (operation.getId() == null) {
                        string = operation.getClass().getName();
                        throw new OperationFailedException("Operation " + string + " failed :", (Throwable)xde);
                    }
                    string = operation.getId();
                    throw new OperationFailedException("Operation " + string + " failed :", (Throwable)xde);
                }
                this.rollback(operation, sessionMap, executable, (Throwable)xde, false);
                ++counter;
                continue;
            } catch (Exception e) {
                String string;
                this.rollback(operation, sessionMap, executable, e, true);
                if (operation.getId() == null) {
                    string = operation.getClass().getName();
                    throw new OperationFailedException("Operation " + string + " failed :", e);
                }
                string = operation.getId();
                throw new OperationFailedException("Operation " + string + " failed :", e);
            }
        } while (true);
       
    }

    private void rollback(Operation<?> operation, Map<String, Session> sessionMap, OperationExecutable<?, ?> executable, Throwable e, boolean terminate) throws OperationFailedException {
        try {
            for (Session session : sessionMap.values()) {
                if (!terminate) {
                    if (session instanceof XDBSession || session instanceof ReplicatedXDBSession) {
                        ((XhiveSessionIf)session.getSession()).rollback();
                        continue;
                    }
                    session.rollback();
                    continue;
                }
                session.rollback();
            }
            if (executable.canRollback()) {
                executable.rollback();
            }
        }
        catch (RollbackFailedException rfe) {
            throw new OperationFailedException("Operation " + operation.getId() + " failed, and was rolled back unsuccessfully.", e);
        }
        catch (RollbackNotAvailableException rnae) {
            // empty catch block
        }
    }
}

