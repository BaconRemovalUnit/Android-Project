package csc296.project2.database.Favorite;

/**
 * Created by Berto on 2015/11/8.
 */
public class FavoriteDatabaseSchema {
    public static final int VERSION = 1;
    public static final String DATABASE_NAME = "favorites.db";

    public static final class FavoriteTable {
        public static final String NAME = "favorite";

        public static final class Cols {
            public static final String ID = "id";
            public static final String USER = "user";
            public static final String FOLLOWING = "following";

        }
    }
}
