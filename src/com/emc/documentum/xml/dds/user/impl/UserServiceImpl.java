/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.user.impl;

import com.emc.documentum.xml.dds.application.Application;
import com.emc.documentum.xml.dds.application.StructureManager;
import com.emc.documentum.xml.dds.configuration.user.UserConfiguration;
import com.emc.documentum.xml.dds.exception.DDSException;
import com.emc.documentum.xml.dds.logging.LogCenter;
import com.emc.documentum.xml.dds.operation.exception.OperationException;
import com.emc.documentum.xml.dds.operation.framework.Operation;
import com.emc.documentum.xml.dds.operation.library.persistence.CreateLocationOperation;
import com.emc.documentum.xml.dds.operation.library.persistence.DeleteStoreChildOperation;
import com.emc.documentum.xml.dds.operation.library.persistence.ExistsStoreChildOperation;
import com.emc.documentum.xml.dds.operation.library.persistence.ListChildrenOperation;
import com.emc.documentum.xml.dds.operation.library.persistence.PersistOperation;
import com.emc.documentum.xml.dds.operation.library.persistence.RetrieveOperation;
import com.emc.documentum.xml.dds.persistence.Container;
import com.emc.documentum.xml.dds.persistence.ContentDescriptor;
import com.emc.documentum.xml.dds.persistence.Data;
import com.emc.documentum.xml.dds.persistence.Location;
import com.emc.documentum.xml.dds.persistence.LocationOptions;
import com.emc.documentum.xml.dds.persistence.Store;
import com.emc.documentum.xml.dds.persistence.StoreChild;
import com.emc.documentum.xml.dds.persistence.content.ObjectContentDescriptor;
import com.emc.documentum.xml.dds.persistence.data.ObjectData;
import com.emc.documentum.xml.dds.persistence.exception.UserAlreadyExistsException;
import com.emc.documentum.xml.dds.persistence.exception.UserNotFoundException;
import com.emc.documentum.xml.dds.service.DDSServiceType;
import com.emc.documentum.xml.dds.service.ServiceType;
import com.emc.documentum.xml.dds.service.exception.ServiceNotAvailableException;
import com.emc.documentum.xml.dds.service.impl.ServiceImpl;
import com.emc.documentum.xml.dds.structure.internal.ApplicationStructure;
import com.emc.documentum.xml.dds.structure.internal.UserStructure;
import com.emc.documentum.xml.dds.user.User;
import com.emc.documentum.xml.dds.user.UserService;
import com.emc.documentum.xml.dds.user.exception.BadPasswordException;
import com.emc.documentum.xml.dds.user.internal.UserImpl;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserServiceImpl
extends ServiceImpl
implements UserService {
    private Store store;
    private Location userLibrary;
    private User isUser;
    private Map<String, User> users = new HashMap<String, User>();
    private final List<String> loggedInUsers = new ArrayList<String>();
    private final List<String> notLoggedInUsers = new ArrayList<String>();
    private final Object userMutex = new Object();

    public void setUsersLocation(Location location) {
        this.userLibrary = location;
    }

    public void setUser(User user) {
        this.isUser = user;
    }

    @Override
    public ServiceType getType() {
        return DDSServiceType.USER;
    }

    @Override
    public boolean activateConfiguration() {
        return true;
    }

    @Override
    protected boolean checkDependencies() {
        if (this.getApplication() == null) {
            LogCenter.error(this, "No Application was specified before initialization.");
            return false;
        }
        if (this.store == null) {
            this.store = this.getApplication().getMainStore();
        }
        if (this.isUser == null) {
            this.isUser = this.getApplication().getApplicationUser();
        }
        if (this.store != null && this.userLibrary == null) {
            ArrayList<String> pathComponents = new ArrayList<String>();
            pathComponents.add("APPLICATIONS");
            pathComponents.add(this.getApplication().getName());
            pathComponents.add("users");
            this.userLibrary = this.store.getLocation(pathComponents);
        }
        return this.store != null && this.userLibrary != null && this.isUser != null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected boolean executeInitialization() {
        Object object = this.userMutex;
        synchronized (object) {
            try {
                this.users = new HashMap<String, User>();
                if (!((Boolean)this.getApplication().execute(this.isUser, new ExistsStoreChildOperation(this.userLibrary))).booleanValue()) {
                    LogCenter.debug(this, "Creating Users directory : " + this.userLibrary.getPath(false));
                    this.getApplication().execute(this.isUser, new CreateLocationOperation(this.userLibrary, null, true));
                }
                LogCenter.debug(this, "Listing User Home Locations.");
                Collection<StoreChild> userContainers = this.getApplication().execute(this.isUser, new ListChildrenOperation(this.userLibrary, false, true, false));
                for (StoreChild userContainer : userContainers) {
                    LogCenter.debug(this, "Retrieving User from " + userContainer.getName());
                    UserConfiguration userConfiguration = (UserConfiguration)((ObjectData)this.getApplication().execute(this.isUser, new RetrieveOperation((Container)userContainer, new ObjectContentDescriptor()))).content();
                    this.users.put(userConfiguration.getId(), new UserImpl(userConfiguration));
                    this.notLoggedInUsers.add(userConfiguration.getId());
                }
                LogCenter.debug("Loaded " + this.users.size() + " Users during initialization.");
                return true;
            }
            catch (OperationException ofe) {
                LogCenter.exception(this, "Could not initialize the UserManager :", ofe);
                return false;
            }
        }
    }

    @Override
    protected boolean executePause() {
        return true;
    }

    @Override
    protected boolean executeResume() {
        return true;
    }

    @Override
    protected boolean executeShutdown() {
        this.store = null;
        this.isUser = null;
        this.userLibrary = null;
        return true;
    }

    @Override
    protected boolean executeStartup() {
        return true;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public User createUser(UserConfiguration configuration) throws DDSException {
        Object object = this.userMutex;
        synchronized (object) {
            String id = configuration.getId();
            if (this.users.get(id) != null) {
                throw new UserAlreadyExistsException("User already exists : " + id);
            }
            UserImpl user = new UserImpl(configuration);
            this.users.put(id, user);
            this.notLoggedInUsers.add(id);
            ApplicationStructure struct = this.getApplication().getStructureManager().getApplicationStructure();
            struct.addUserStructure(new UserStructure(id, struct));
            Container container = this.store.getContainer(this.userLibrary, id + ".xml");
            this.getApplication().execute(this.isUser, new PersistOperation(container, new ObjectContentDescriptor(), new ObjectData(configuration), false));
            this.getApplication().execute(this.isUser, new CreateLocationOperation(this.userLibrary.getChildLocation(id), null, true));
            return user;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void deleteUser(User user) throws DDSException {
        Object object = this.userMutex;
        synchronized (object) {
            if (user == null) {
                throw new UserNotFoundException("Null User provided for delete.");
            }
            String id = user.getId();
            if (this.users.get(user.getId()) == null) {
                throw new UserNotFoundException("User not found : " + id);
            }
            this.loggedInUsers.remove(id);
            this.notLoggedInUsers.remove(id);
            this.users.remove(id);
            Container container = this.store.getContainer(this.userLibrary, id + ".xml");
            this.getApplication().execute(this.isUser, new DeleteStoreChildOperation(container));
            Location location = this.userLibrary.getChildLocation(id);
            this.getApplication().execute(this.isUser, new DeleteStoreChildOperation(location));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public User getUser(String id) {
        Object object = this.userMutex;
        synchronized (object) {
            return this.users.get(id);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<User> getUsers() {
        Object object = this.userMutex;
        synchronized (object) {
            return new ArrayList<User>(this.users.values());
        }
    }

    @Override
    public Location getHome(String id) {
        if (this.getUser(id) == null) {
            return null;
        }
        return this.userLibrary.getChildLocation(id);
    }

    @Override
    public Location getUsersLocation() {
        return this.userLibrary.deepCopy();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<User> getUsers(boolean state) {
        Object object = this.userMutex;
        synchronized (object) {
            if (state) {
                return new ArrayList<User>(this.usersForIdList(this.loggedInUsers));
            }
            return new ArrayList<User>(this.usersForIdList(this.notLoggedInUsers));
        }
    }

    @Override
    public boolean loginUser(String id, String password) throws UserNotFoundException, BadPasswordException, ServiceNotAvailableException {
        this.checkRunning();
        Object object = this.userMutex;
        synchronized (object) {
            User user = this.users.get(id);
            if (user == null) {
                throw new UserNotFoundException("User cannot be found : " + id);
            }
            if (user.checkPassword(password)) {
                this.notLoggedInUsers.remove(id);
                if (!this.loggedInUsers.contains(id)) {
                    this.loggedInUsers.add(id);
                }
                return true;
            }
            throw new BadPasswordException("Invalid password provided for User " + id);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean logoutUser(String id) throws UserNotFoundException, ServiceNotAvailableException {
        this.checkRunning();
        Object object = this.userMutex;
        synchronized (object) {
            if (this.users.get(id) == null) {
                throw new UserNotFoundException("User cannot be found : " + id);
            }
            if (!this.notLoggedInUsers.contains(id)) {
                this.notLoggedInUsers.add(id);
            }
            if (this.loggedInUsers.contains(id)) {
                this.loggedInUsers.remove(id);
                return true;
            }
            return false;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isLoggedIn(String id) {
        Object object = this.userMutex;
        synchronized (object) {
            return this.loggedInUsers.contains(id);
        }
    }

    private List<User> usersForIdList(List<String> userIds) {
        if (userIds == null) {
            return new ArrayList<User>();
        }
        ArrayList<User> result = new ArrayList<User>();
        for (String userId : userIds) {
            result.add(this.users.get(userId));
        }
        return result;
    }
}

