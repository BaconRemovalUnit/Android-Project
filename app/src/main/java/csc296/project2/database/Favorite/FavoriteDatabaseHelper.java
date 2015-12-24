package csc296.project2.database.Favorite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import csc296.project2.database.Favorite.FavoriteDatabaseSchema.FavoriteTable;
/**
 * Created by Berto on 2015/11/8.
 */
public class FavoriteDatabaseHelper extends SQLiteOpenHelper {
    public FavoriteDatabaseHelper(Context context) {
        super(context, FavoriteDatabaseSchema.DATABASE_NAME, null, FavoriteDatabaseSchema.VERSION );
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + FavoriteTable.NAME
                + "(_ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                +FavoriteTable.Cols.ID+", "
                + FavoriteTable.Cols.USER + ", "
                + FavoriteTable.Cols.FOLLOWING
                +")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}