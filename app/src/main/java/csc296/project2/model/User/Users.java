package csc296.project2.model.User;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import csc296.project2.database.User.UserDatabaseHelper;
import csc296.project2.database.User.UserCursorWrapper;
import csc296.project2.database.User.UserDatabaseSchema;

/**
 * Created by USX13992 on 10/23/2015.
 */
public class Users {
    private static Users sUsers;

    private final Context mContext;
    private final SQLiteDatabase mDatabase;
    private final List<User> mUsers;
    private final Map<UUID,User> mUsersMap;

    private Users(Context context) {
        // make sure to use the application context, not an individual activity because the
        // collection needs to outlive activities that are destroyed
        mContext = context.getApplicationContext();
        // open a connection to the database
        mDatabase = new UserDatabaseHelper(mContext).getWritableDatabase();
        // create an empty list of users
        mUsers = new LinkedList<>();
        mUsersMap = new HashMap<>();
    }

    public static synchronized Users get(Context context) {
        if(sUsers == null) {
            sUsers = new Users(context);
        }
        return sUsers;
    }

    public List<User> getUsers() {
        mUsers.clear();
        mUsersMap.clear();
        UserCursorWrapper wrapper = queryUsers(null, null);

        try {
            wrapper.moveToFirst();
            while(wrapper.isAfterLast() == false) {
                User user = wrapper.getUser();
                mUsers.add(user);
                mUsersMap.put(user.getId(), user);
                wrapper.moveToNext();
            }
        }
        finally {
            wrapper.close();
        }

        return mUsers;
    }

    public boolean hasUser(String name){//checks if the id is already being used
        mUsers.clear();
        mUsersMap.clear();
        UserCursorWrapper wrapper = queryUsers( UserDatabaseSchema.UserTable.Cols.ID+ "=?"
               , new String[]{name});
        boolean result = wrapper.moveToNext();
        wrapper.close();
        return result;
    }

    public void addUser(User user) {
        ContentValues values = getContentvalues(user);
        mDatabase.insert(UserDatabaseSchema.UserTable.NAME, null, values);
    }

    public void updateUser(User user) {
        String id = user.getId().toString();
        ContentValues values = getContentvalues(user);
        mDatabase.update(UserDatabaseSchema.UserTable.NAME,
                values,
                UserDatabaseSchema.UserTable.Cols.ID + "=?",
                new String[]{id});
    }

    public User getUser(UUID id) {
        return mUsersMap.get(id);
    }

    public User getUser(String id){
        User temp = new User();
        UserCursorWrapper wrapper =  queryUsers(UserDatabaseSchema.UserTable.Cols.USERNAME+"=?",new String[]{id});
        try {
            wrapper.moveToFirst();
            while(wrapper.isAfterLast() == false) {
                temp = wrapper.getUser();
                wrapper.moveToNext();
            }
        }
        finally {
            wrapper.close();
        }
        return temp;
    }

    private static ContentValues getContentvalues(User user) {
        ContentValues values = new ContentValues();

        values.put(UserDatabaseSchema.UserTable.Cols.ID, user.getId().toString());
        values.put(UserDatabaseSchema.UserTable.Cols.USERNAME, user.getUserName());
        values.put(UserDatabaseSchema.UserTable.Cols.BIRTHDAY, user.getBirthday().getTime());
        values.put(UserDatabaseSchema.UserTable.Cols.LOCATION, user.getLocation());
        values.put(UserDatabaseSchema.UserTable.Cols.PASSWORD, user.getPassword());
        values.put(UserDatabaseSchema.UserTable.Cols.PHOTO, user.getPhoto());
        values.put(UserDatabaseSchema.UserTable.Cols.MESSAGE, user.getMessage());

        return values;
    }

    private UserCursorWrapper queryUsers(String where, String[] args) {
        Cursor cursor = mDatabase.query(
                UserDatabaseSchema.UserTable.NAME, // table name
                null,                          // which columns; null for all
                where,                         // where clause, e.g. id=?
                args,                          // where arguments
                null,                          // group by
                null,                          // having
                null                           // order by
        );

        return new UserCursorWrapper(cursor);
    }
}
