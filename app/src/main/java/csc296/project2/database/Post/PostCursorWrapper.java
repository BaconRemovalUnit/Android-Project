package csc296.project2.database.Post;

import android.database.Cursor;
import android.database.CursorWrapper;

import java.util.Date;
import java.util.UUID;

import csc296.project2.database.Post.PostDatabaseSchema.PostTable;
import csc296.project2.model.Post.Post;


/**
 * Created by Berto on 11/08/2015.
 */
public class PostCursorWrapper extends CursorWrapper {

    public PostCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Post getPost() {
        UUID id = UUID.fromString(getString(getColumnIndex(PostTable.Cols.ID)));
        UUID user = UUID.fromString(getString(getColumnIndex(PostTable.Cols.USER)));
        String message = getString(getColumnIndex(PostTable.Cols.MESSAGE));
        Date date = new Date(getLong(getColumnIndex(PostTable.Cols.POSTDATE)));
        Post post = new Post(id);

        post.setPoster(user);
        post.setMessage(message);
        post.setPostTime(date);

        return post;
    }
}
