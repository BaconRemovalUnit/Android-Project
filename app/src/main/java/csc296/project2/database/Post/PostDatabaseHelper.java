package csc296.project2.database.Post;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import csc296.project2.database.Post.PostDatabaseSchema;
import csc296.project2.database.Post.PostDatabaseSchema.PostTable;

/**
 * Created by Berto on 2015/11/8.
 */
public class PostDatabaseHelper extends SQLiteOpenHelper {
    public PostDatabaseHelper(Context context) {
        super(context, PostDatabaseSchema.DATABASE_NAME, null, PostDatabaseSchema.VERSION );
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + PostTable.NAME
                + "(_ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                +PostTable.Cols.ID+", "
                + PostTable.Cols.USER + ", "
                + PostTable.Cols.POSTDATE + ", "
                + PostTable.Cols.MESSAGE
                +")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}