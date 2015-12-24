package csc296.project2.model.Post;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import csc296.project2.database.Favorite.FavoriteDatabaseSchema;
import csc296.project2.database.Post.PostCursorWrapper;
import csc296.project2.database.Post.PostDatabaseHelper;
import csc296.project2.database.Post.PostDatabaseSchema;
import csc296.project2.model.Favorite.Favorite;
import csc296.project2.model.Favorite.FavoriteList;
import csc296.project2.model.User.User;

/**
 * Created by Berto on 2015/11/8.
 */
public class PostBoard {
    private static final String TAG = "PostBoard";
    private final List<Post> mPosts;
    private final Map<UUID,Post> mPostMap;
    private final SQLiteDatabase mDatabase;
    private final Context mContext;
    private static PostBoard sPostBoard;

    private PostBoard(Context context) {
        mContext = context.getApplicationContext();
        // open a connection to the database
        mDatabase = new PostDatabaseHelper(mContext).getWritableDatabase();
        // list of posts
        mPosts = new LinkedList<>();
        mPostMap = new HashMap<>();
    }

    public static synchronized PostBoard get(Context context) {
        if(sPostBoard == null) {
            sPostBoard = new PostBoard(context);
        }
        return sPostBoard;
    }

    public List<Post> getUserPosts(UUID user){
        mPosts.clear();
        mPostMap.clear();
        PostCursorWrapper wrapper = queryUserPosts(user);
        wrapper.moveToFirst();
        try {
            wrapper.moveToFirst();
            while(wrapper.isAfterLast() == false) {
                Post post = wrapper.getPost();
                mPosts.add(post);
                mPostMap.put(post.getID(), post);
                wrapper.moveToNext();
            }
        }
        finally {
            wrapper.close();
        }
        return mPosts;
    }

    public List<Post> getPosts() {
        mPosts.clear();
        mPostMap.clear();
        PostCursorWrapper wrapper = queryPosts(null, null);

        try {
            wrapper.moveToFirst();
            while(wrapper.isAfterLast() == false) {
                Post post = wrapper.getPost();
                mPosts.add(post);
                mPostMap.put(post.getID(), post);
                wrapper.moveToNext();
            }
        }
        finally {
            wrapper.close();
        }

        return mPosts;
    }

    public void addPost(Post post) {
        ContentValues values = getContentvalues(post);

        mDatabase.insert(PostDatabaseSchema.PostTable.NAME, null, values);
    }

    public void updatePost(Post post) {
        String id = post.getID().toString();
        Log.d("PostBoard", "Post:" + post.getMessage());
        ContentValues values = getContentvalues(post);
        mDatabase.update(PostDatabaseSchema.PostTable.NAME,
                values,
                PostDatabaseSchema.PostTable.Cols.ID + "=?",
                new String[]{id});
        Log.d(TAG, "post updated!"); }

    public Post getPost(UUID id) {
        return mPostMap.get(id);
    }

    private static ContentValues getContentvalues(Post post) {
        ContentValues values = new ContentValues();
        values.put(PostDatabaseSchema.PostTable.Cols.ID, post.getID().toString());
        values.put(PostDatabaseSchema.PostTable.Cols.USER, post.getPoster().toString());
        values.put(PostDatabaseSchema.PostTable.Cols.POSTDATE, post.getPostTime().getTime());
        values.put(PostDatabaseSchema.PostTable.Cols.MESSAGE, post.getMessage());

        return values;
    }

    private PostCursorWrapper queryPosts(String where, String[] args) {
        String sortOrder = PostDatabaseSchema.PostTable.Cols.POSTDATE + " DESC";
        Cursor cursor = mDatabase.query(
                PostDatabaseSchema.PostTable.NAME, // table name
                null,                          // which columns; null for all
                where,                         // where clause, e.g. id=?
                args,                          // where arguments
                null,                          // group by
                null,                          // having
                sortOrder                           // order by
        );

        return new PostCursorWrapper(cursor);
    }

    private PostCursorWrapper queryUserPosts(UUID user){
        String posts = PostDatabaseSchema.PostTable.NAME;
        String poster = PostDatabaseSchema.PostTable.Cols.USER;
        String rawQuery = "SELECT * FROM "+ posts +
                " WHERE "+posts+"."+poster+"=?";
        Cursor cursor = mDatabase.rawQuery(rawQuery, new String[]{user.toString()});
        return new PostCursorWrapper(cursor);
    }
}
