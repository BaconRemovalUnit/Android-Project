package csc296.project2.model.Favorite;

import java.util.UUID;

/**
 * Created by Berto on 2015/11/8.
 */
public class Favorite {
    private UUID mEntry;
    private UUID mUser;
    private UUID mFollowing;

    public Favorite(){this.mEntry=UUID.randomUUID();
    }
    public Favorite(UUID mEntry){
        this.mEntry = mEntry;
    }

    public UUID getUser() {
        return mUser;
    }

    public void setUser(UUID mUser) {
        this.mUser = mUser;
    }

    public UUID getFollowing() {
        return mFollowing;
    }

    public void setFollowing(UUID mFollowing) {
        this.mFollowing = mFollowing;
    }

    public UUID getId() {
        return mEntry;
    }

    public void setId(UUID mEntry) {
        this.mEntry = mEntry;
    }
}
