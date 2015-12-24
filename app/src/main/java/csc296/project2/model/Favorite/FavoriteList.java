package csc296.project2.model.Favorite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import csc296.project2.database.Favorite.FavoriteCursorWrapper;
import csc296.project2.database.Favorite.FavoriteDatabaseHelper;
import csc296.project2.database.Favorite.FavoriteDatabaseSchema;
import csc296.project2.database.User.UserCursorWrapper;
import csc296.project2.database.User.UserDatabaseSchema;
import csc296.project2.model.Favorite.Favorite;


public class FavoriteList {
    private static FavoriteList sFavoriteList;

    private final Context mContext;
    private final SQLiteDatabase mDatabase;
    private final List<Favorite> mFavorites;
    private final Map<UUID,Favorite> mFavoritesMap;

    private FavoriteList(Context context) {
        // make sure to use the application context, not an individual activity because the
        // collection needs to outlive activities that are destroyed
        mContext = context.getApplicationContext();
        // open a connection to the database
        mDatabase = new FavoriteDatabaseHelper(mContext).getWritableDatabase();
        // create an empty list of favorites\
        mFavorites = new LinkedList<>();
        mFavoritesMap = new HashMap<>();
    }

    public static synchronized FavoriteList get(Context context) {
        if(sFavoriteList == null) {
            sFavoriteList = new FavoriteList(context);
        }
        return sFavoriteList;
    }

    public List<Favorite> getFavorites() {
        mFavorites.clear();
        mFavoritesMap.clear();
        FavoriteCursorWrapper wrapper = queryFavorites(null, null);

        try {
            wrapper.moveToFirst();
            while(wrapper.isAfterLast() == false) {
                Favorite favorite = wrapper.getFavorite();
                mFavorites.add(favorite);
                mFavoritesMap.put(favorite.getId(), favorite);
                wrapper.moveToNext();
            }
        }
        finally {
            wrapper.close();
        }

        return mFavorites;
    }

    public List<Favorite> getUserFavorites(UUID uuid){
        mFavorites.clear();
        mFavoritesMap.clear();
        String id = uuid.toString();
        FavoriteCursorWrapper wrapper = queryFavorites(FavoriteDatabaseSchema.FavoriteTable.Cols.USER+"=?", new String[]{id});

        try {
            wrapper.moveToFirst();
            while(wrapper.isAfterLast() == false) {
                Favorite favorite = wrapper.getFavorite();
                mFavorites.add(favorite);
                mFavoritesMap.put(favorite.getId(), favorite);
                wrapper.moveToNext();
            }
        }
        finally {
            wrapper.close();
        }

        return mFavorites;
    }

    public void addFavorite(Favorite favorite) {
        ContentValues values = getContentvalues(favorite);
        mDatabase.insert(FavoriteDatabaseSchema.FavoriteTable.NAME, null, values);
    }


    public void deleteFavorite(UUID user, UUID following){
        mFavorites.clear();
        mFavoritesMap.clear();
        String deleteQuery = FavoriteDatabaseSchema.FavoriteTable.Cols.USER+"=? " +
                "AND " + FavoriteDatabaseSchema.FavoriteTable.Cols.FOLLOWING +"=?";
        mDatabase.delete(FavoriteDatabaseSchema.FavoriteTable.NAME,
                deleteQuery,
                new String[]{user.toString(),following.toString()});
    }

    public boolean isFollowing(UUID user, UUID following){
        mFavorites.clear();
        mFavoritesMap.clear();
        FavoriteCursorWrapper wrapper = queryFavorites(FavoriteDatabaseSchema.FavoriteTable.Cols.USER+"=? " +
                "AND " + FavoriteDatabaseSchema.FavoriteTable.Cols.FOLLOWING +"=?",new String[]{user.toString(),following.toString()});
        boolean hasRelation = wrapper.moveToNext();
        wrapper.close();
        return hasRelation;
    }

    public Favorite getFavorite(UUID id) {
        return mFavoritesMap.get(id);
    }

    private static ContentValues getContentvalues(Favorite favorite) {
        ContentValues values = new ContentValues();

        values.put(FavoriteDatabaseSchema.FavoriteTable.Cols.ID, favorite.getId().toString());
        values.put(FavoriteDatabaseSchema.FavoriteTable.Cols.USER, favorite.getUser().toString());
        values.put(FavoriteDatabaseSchema.FavoriteTable.Cols.FOLLOWING, favorite.getFollowing().toString());
        return values;
    }



    private FavoriteCursorWrapper queryFavorites(String where, String[] args) {
        Cursor cursor = mDatabase.query(
                FavoriteDatabaseSchema.FavoriteTable.NAME, // table name
                null,                          // which columns; null for all
                where,                         // where clause, e.g. id=?
                args,                          // where arguments
                null,                          // group by
                null,                          // having
                null                           // order by
        );

        return new FavoriteCursorWrapper(cursor);
    }
}
