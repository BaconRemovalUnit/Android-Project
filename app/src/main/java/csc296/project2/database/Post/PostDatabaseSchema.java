package csc296.project2.database.Post;

/**
 * Created by Berto on 2015/11/8.
 */
public class PostDatabaseSchema {
    public static final int VERSION = 1;
    public static final String DATABASE_NAME = "posts.db";

    public static final class PostTable {
        public static final String NAME = "post";

        public static final class Cols {
            public static final String ID = "id";
            public static final String USER = "user";
            public static final String POSTDATE = "date";
            public static final String MESSAGE = "message";
            public static final String PICTURE = "picture";

        }
    }
}
