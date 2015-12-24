package csc296.project2.database.User;

import android.database.Cursor;
import android.database.CursorWrapper;

import java.util.Date;
import java.util.UUID;

import csc296.project2.model.User.User;

public class UserCursorWrapper extends CursorWrapper {

    public UserCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public User getUser() {
        UUID id =UUID.fromString(getString(getColumnIndex(UserDatabaseSchema.UserTable.Cols.ID)));
        String userName = getString(getColumnIndex(UserDatabaseSchema.UserTable.Cols.USERNAME));
        Date birthday = new Date(getLong(getColumnIndex(UserDatabaseSchema.UserTable.Cols.BIRTHDAY)));
        String location = getString(getColumnIndex(UserDatabaseSchema.UserTable.Cols.LOCATION));
        String password = getString(getColumnIndex(UserDatabaseSchema.UserTable.Cols.PASSWORD));
        String photo = getString(getColumnIndex(UserDatabaseSchema.UserTable.Cols.PHOTO));
        String message = getString(getColumnIndex(UserDatabaseSchema.UserTable.Cols.MESSAGE));

        User user = new User(id);

        user.setUserName(userName);
        user.setBirthday(birthday);
        user.setLocation(location);
        user.setPassword(password);
        user.setPhoto(photo);
        user.setMessage(message);

        return user;
    }
}
