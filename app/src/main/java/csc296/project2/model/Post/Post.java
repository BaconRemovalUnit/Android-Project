package csc296.project2.model.Post;

import java.util.Date;
import java.util.UUID;

/**
 * Created by Berto on 2015/11/8.
 */
public class Post {
    private UUID mId;
    private UUID mPoster;
    private Date mPostTime;
    private String mMessage;

    public Post(){
        this.mId = UUID.randomUUID();
    }

    public Post(UUID mId){
        this.mId = mId;
    }


    public UUID getID() {
        return mId;
    }

    public void setId(UUID mId) {
        this.mId = mId;
    }

    public UUID getPoster() {
        return mPoster;
    }

    public void setPoster(UUID mPoster) {
        this.mPoster = mPoster;
    }

    public Date getPostTime() {
        return mPostTime;
    }

    public void setPostTime(Date mPostTime) {
        this.mPostTime = mPostTime;
    }

    public String getMessage() {
        return mMessage;
    }

    public void setMessage(String mMessage) {
        this.mMessage = mMessage;
    }
}
