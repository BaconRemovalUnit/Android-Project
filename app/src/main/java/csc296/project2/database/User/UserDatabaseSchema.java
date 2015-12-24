package csc296.project2.database.User;


public class UserDatabaseSchema {
    public static final int VERSION = 1;
    public static final String DATABASE_NAME = "user.db";

    public static final class UserTable {
        public static final String NAME = "user";

        public static final class Cols {
            public static final String ID = "id";
            public static final String USERNAME = "name";
            public static final String BIRTHDAY = "birthday";
            public static final String LOCATION = "location";
            public static final String PASSWORD = "password";
            public static final String PHOTO = "photo";
            public static final String MESSAGE = "message";
        }
    }
}
