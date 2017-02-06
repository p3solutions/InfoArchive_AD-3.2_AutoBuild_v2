/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.service.impl;

import com.emc.documentum.xml.dds.application.Application;
import com.emc.documentum.xml.dds.configuration.Configuration;
import com.emc.documentum.xml.dds.logging.LogCenter;
import com.emc.documentum.xml.dds.service.Action;
import com.emc.documentum.xml.dds.service.Service;
import com.emc.documentum.xml.dds.service.State;
import com.emc.documentum.xml.dds.service.exception.ServiceNotAvailableException;
import java.util.HashMap;
import java.util.Map;

public abstract class ServiceImpl
implements Service {
    private static Map<Action, Map<State, State>> stateMachine = new HashMap<Action, Map<State, State>>();
    private String name;
    private Configuration configuration;
    private State state = State.STOPPED;
    private final Object stateMutex = new Object();
    private Action lastAction = Action.NONE;
    private boolean actionProcessed = true;
    private final Object actionMutex = new Object();
    private final Object switchingMutex = new Object();
    private Application application;

    protected ServiceImpl() {
    }

    @Override
    public Application getApplication() {
        return this.application;
    }

    @Override
    public void setApplication(Application application) {
        this.application = application;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(String name) {
        if (name == null || "".equals(name.trim())) {
            LogCenter.error(this, "Name for service is empty or null.");
        } else {
            this.name = name;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public State getState() {
        Object object = this.stateMutex;
        synchronized (object) {
            return this.state;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void setState(State state) {
        Object object = this.stateMutex;
        synchronized (object) {
            this.state = state;
            this.stateMutex.notify();
        }
    }

    @Override
    public Action getLastAction() {
        return this.lastAction;
    }

    @Override
    public Configuration getConfiguration() {
        return this.configuration;
    }

    @Override
    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public boolean configure(Configuration config) {
        this.setConfiguration(config);
        return this.activateConfiguration();
    }

    @Override
    public boolean initialize() {
        return this.checkDependencies() && this.processAction(Action.INITIALIZE);
    }

    @Override
    public boolean start() {
        return this.processAction(Action.START);
    }

    @Override
    public boolean stop() {
        return this.processAction(Action.STOP);
    }

    @Override
    public boolean pause() {
        return this.processAction(Action.PAUSE);
    }

    @Override
    public boolean resume() {
        return this.processAction(Action.RESUME);
    }

    @Override
    public boolean fullStartup() {
        return this.initialize() && this.start();
    }

    private State getNextState(Action action) {
        Map<State, State> transitions = stateMachine.get(action);
        if (transitions == null) {
            LogCenter.error(this, "Internal Error : Could not find transition for Action : " + action.getUserReadable());
            return null;
        }
        return transitions.get(this.getState());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean processAction(Action action) {
        Object newState;
        if (this.name == null) {
            LogCenter.error(this, "Tried to process action for Manager which has not been named");
            return false;
        }
        LogCenter.debug(this, "Processing action " + action.getUserReadable() + " for Manager " + this.name);
        State oldState = null;
        Object object = this.actionMutex;
        synchronized (object) {
            oldState = this.getState();
            newState = this.getNextState(action);
            if (newState == null) {
                LogCenter.warning(this, "Could not process action - Current State = " + this.getState().getUserReadable() + " does not allow Action " + action.getUserReadable());
                return false;
            }
            if (!this.actionProcessed) {
                LogCenter.warning(this, "Could not process action - previous action not yet processed");
                return false;
            }
            this.actionProcessed = false;
            this.setState((State)newState);
        }
        this.lastAction = action;
        boolean success = false;
        newState = this.switchingMutex;
        synchronized (newState) {
            try {
                switch (action) {
                    case INITIALIZE: {
                        success = this.executeInitialization();
                        break;
                    }
                    case START: {
                        success = this.executeStartup();
                        break;
                    }
                    case STOP: {
                        success = this.executeShutdown();
                        break;
                    }
                    case PAUSE: {
                        success = this.executePause();
                        break;
                    }
                    case RESUME: {
                        success = this.executeResume();
                        break;
                    }
                }
            }
            catch (Exception e) {
                LogCenter.exception(this, "Action " + action.getUserReadable() + " was not executed successfully :", e);
                success = false;
            }
        }
        newState = this.actionMutex;
        synchronized (newState) {
            this.setState(success ? this.getNextState(Action.INTERNAL) : oldState);
            this.actionProcessed = true;
            if (success) {
                LogCenter.debug(this, "Processed action " + action.getUserReadable() + " for Manager " + this.name + "; new state = " + this.getState().getUserReadable());
            } else {
                LogCenter.error(this, "Action " + action.getUserReadable() + " for Manager " + this.name + " FAILED; new state = " + this.getState().getUserReadable());
            }
            return success;
        }
    }

    protected void checkRunning() throws ServiceNotAvailableException {
        if (this.getState() != State.RUNNING) {
            throw new ServiceNotAvailableException();
        }
    }

    protected abstract boolean checkDependencies();

    protected abstract boolean executeInitialization();

    protected abstract boolean executeStartup();

    protected abstract boolean executeShutdown();

    protected abstract boolean executePause();

    protected abstract boolean executeResume();

    protected Object getStateMutex() {
        return this.stateMutex;
    }

    static
    {
      HashMap<State, State> transitions = new HashMap();
      transitions.put(State.STOPPED, State.INITIALIZING);
      stateMachine.put(Action.INITIALIZE, transitions);
      
      transitions = new HashMap();
      transitions.put(State.INITIALIZED, State.STARTING);
      stateMachine.put(Action.START, transitions);
      
      transitions = new HashMap();
      transitions.put(State.RUNNING, State.PAUSING);
      stateMachine.put(Action.PAUSE, transitions);
      
      transitions = new HashMap();
      transitions.put(State.PAUSED, State.RESUMING);
      stateMachine.put(Action.RESUME, transitions);
      
      transitions = new HashMap();
      transitions.put(State.INITIALIZED, State.STOPPING);
      transitions.put(State.RUNNING, State.STOPPING);
      transitions.put(State.PAUSED, State.STOPPING);
      stateMachine.put(Action.STOP, transitions);
      
      transitions = new HashMap();
      transitions.put(State.INITIALIZING, State.INITIALIZED);
      transitions.put(State.STARTING, State.RUNNING);
      transitions.put(State.PAUSING, State.PAUSED);
      transitions.put(State.RESUMING, State.RUNNING);
      transitions.put(State.STOPPING, State.STOPPED);
      stateMachine.put(Action.INTERNAL, transitions);
    }

}

