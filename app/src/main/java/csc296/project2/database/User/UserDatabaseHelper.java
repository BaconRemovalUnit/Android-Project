package csc296.project2.database.User;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class UserDatabaseHelper extends SQLiteOpenHelper {
    public UserDatabaseHelper(Context context) {
        super(context, UserDatabaseSchema.DATABASE_NAME, null, UserDatabaseSchema.VERSION );
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + UserDatabaseSchema.UserTable.NAME
                + "(_ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + UserDatabaseSchema.UserTable.Cols.ID+", "
                + UserDatabaseSchema.UserTable.Cols.USERNAME + ", "
                + UserDatabaseSchema.UserTable.Cols.BIRTHDAY+ ", "
                + UserDatabaseSchema.UserTable.Cols.LOCATION+ ", "
                + UserDatabaseSchema.UserTable.Cols.PASSWORD+ ", "
                + UserDatabaseSchema.UserTable.Cols.PHOTO+ ", "
                + UserDatabaseSchema.UserTable.Cols.MESSAGE
                +")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
