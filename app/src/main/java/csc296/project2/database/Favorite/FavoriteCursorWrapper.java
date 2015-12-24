package csc296.project2.database.Favorite;

import android.database.Cursor;
import android.database.CursorWrapper;

import java.util.Date;
import java.util.UUID;

import csc296.project2.model.Favorite.Favorite;
import csc296.project2.database.Favorite.FavoriteDatabaseSchema.FavoriteTable;

/**
 * Created by Berto on 11/08/2015.
 */
public class FavoriteCursorWrapper extends CursorWrapper {

    public FavoriteCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Favorite getFavorite() {
        UUID id = UUID.fromString(getString(getColumnIndex(FavoriteTable.Cols.ID)));
        UUID user = UUID.fromString(getString(getColumnIndex(FavoriteTable.Cols.USER)));
        UUID following = UUID.fromString(getString(getColumnIndex(FavoriteTable.Cols.FOLLOWING)));
        Favorite favorite = new Favorite(id);

        favorite.setUser(user);
        favorite.setFollowing(following);

        return favorite;
    }
}
