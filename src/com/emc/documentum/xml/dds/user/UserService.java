/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.user;

import com.emc.documentum.xml.dds.configuration.user.UserConfiguration;
import com.emc.documentum.xml.dds.exception.DDSException;
import com.emc.documentum.xml.dds.persistence.Location;
import com.emc.documentum.xml.dds.persistence.exception.UserNotFoundException;
import com.emc.documentum.xml.dds.service.Service;
import com.emc.documentum.xml.dds.service.exception.ServiceNotAvailableException;
import com.emc.documentum.xml.dds.user.User;
import com.emc.documentum.xml.dds.user.exception.BadPasswordException;
import java.util.List;

public interface UserService
extends Service {
    public List<User> getUsers();

    public List<User> getUsers(boolean var1);

    public User getUser(String var1);

    public Location getHome(String var1);

    public Location getUsersLocation();

    public User createUser(UserConfiguration var1) throws DDSException;

    public void deleteUser(User var1) throws DDSException;

    public boolean loginUser(String var1, String var2) throws UserNotFoundException, BadPasswordException, ServiceNotAvailableException;

    public boolean logoutUser(String var1) throws UserNotFoundException, ServiceNotAvailableException;

    public boolean isLoggedIn(String var1);
}

