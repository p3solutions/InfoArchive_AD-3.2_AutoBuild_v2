/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.user.internal;

import com.emc.documentum.xml.dds.user.UserToken;
import java.io.Serializable;

public final class UserTokenImpl
implements Serializable,
UserToken {
    private static final long serialVersionUID = 1;
    private final String applicationName;
    private final String userId;
    private final long tokenId;
    private final long timestamp;

    public UserTokenImpl(String applicationName, String userId, long tokenId) {
        this.applicationName = applicationName;
        this.userId = userId;
        this.tokenId = tokenId;
        this.timestamp = System.currentTimeMillis();
    }

    @Override
    public String getApplicationName() {
        return this.applicationName;
    }

    @Override
    public String getUserId() {
        return this.userId;
    }

    @Override
    public long getTokenId() {
        return this.tokenId;
    }

    @Override
    public long getTimestamp() {
        return this.timestamp;
    }

    public boolean equals(Object object) {
        if (!(object instanceof UserTokenImpl)) {
            return false;
        }
        UserToken token = (UserToken)object;
        return this.applicationName.equals(token.getApplicationName()) && this.userId.equals(token.getUserId()) && this.tokenId == token.getTokenId();
    }

    public int hashCode() {
        return super.hashCode();
    }
}

