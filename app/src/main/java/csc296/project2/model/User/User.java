package csc296.project2.model.User;

import java.util.Date;
import java.util.UUID;

/**
 * Created by Berto on 2015/11/7.
 */
public class User {
    private UUID mId;
    private String mUserName;
    private Date mBirthday;
    private String mLocation;
    private String mPassword;
    private String mPhoto;
    private String mMessage;

    public User(){
        this(UUID.randomUUID());
    }

    public User(UUID id){
        mId = id;
        mBirthday = new Date();
    }

    public UUID getId() {
        return mId;
    }

    public void setId(UUID mId) {
        this.mId = mId;
    }

    public String getUserName() {
        return mUserName;
    }

    public void setUserName(String mFirstName) {
        this.mUserName = mFirstName;
    }

    public Date getBirthday() {
        return mBirthday;
    }

    public void setBirthday(Date mBirthday) {
        this.mBirthday = mBirthday;
    }

    public String getLocation() {
        return mLocation;
    }

    public void setLocation(String mLocation) {
        this.mLocation = mLocation;
    }

    public String getPassword() {
        return mPassword;
    }

    public void setPassword(String mPassword) {
        this.mPassword = mPassword;
    }

    public String getPhoto() {
        return mPhoto;
    }

    public void setPhoto(String mPhoto) {
        this.mPhoto = mPhoto;
    }

    public String getMessage() {
        return mMessage;
    }

    public void setMessage(String mMessage) {
        this.mMessage = mMessage;
    }
}
