package csc296.project2.control.User;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import csc296.project2.R;
import csc296.project2.control.IntroActivity;
import csc296.project2.control.PostBoard.NewPostActivity;
import csc296.project2.control.PostBoard.PostListFragment;
import csc296.project2.control.Utils.ImageLoader;
import csc296.project2.control.Utils.PhotoUtils;
import csc296.project2.control.Utils.SHA;
import csc296.project2.model.Favorite.Favorite;
import csc296.project2.model.Favorite.FavoriteList;
import csc296.project2.model.User.User;
import csc296.project2.model.User.Users;

public class UserInfo extends AppCompatActivity {
    private final String TAG = "UserInfo";
    private EditText mName;
    private EditText mPassword;
    private EditText mBirthday;
    private EditText mBIO;
    private EditText mLocation;
    private ImageView mSelfie;
    private CheckBox mFavorite;
    private Button mPictureButton;
    private Button mSubmit;
    private Button mBack;
    private File mPhotoFile;
    private User mProfile;
    private User mUser;
    private String mPictureAddress;
    private SoundPool soundPool;
    private int soundId;
    private boolean flag;



    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        mPictureButton = (Button) findViewById(R.id.activity_user_info_picture);
        mName = (EditText)findViewById(R.id.activity_user_info_username);
        mPassword = (EditText)findViewById(R.id.activity_user_info_password);
        mBirthday = (EditText)findViewById(R.id.activity_user_info_birthday);
        mBIO = (EditText)findViewById(R.id.activity_user_info_bio);

        mLocation = (EditText)findViewById(R.id.activity_user_info_location);
        mFavorite = (CheckBox)findViewById(R.id.activity_user_info_favorite);
        mSelfie = (ImageView)findViewById(R.id.activity_user_info_selfie);
        mSubmit = (Button)findViewById(R.id.activity_user_info_submit);
        mBack = (Button)findViewById(R.id.activity_user_info_back);

        Bundle args = getIntent().getExtras();
        UUID uid = (UUID)args.getSerializable(PostListFragment.USER_ID);
        UUID pid = (UUID)args.getSerializable(PostListFragment.POSTER_ID);
        mUser = Users.get(this).getUser(uid);
        mProfile = Users.get(this).getUser(pid);
        mName.setOnClickListener(null);

        soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        soundId = soundPool.load(this,R.raw.portal2_sfx_button_negative, 1);
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                flag = true;
                Log.d(TAG,"sound loaded!");
            }
        });
        String URL = mProfile.getPhoto();
        mPictureAddress = URL;
        if(URL.startsWith("http")){
            new ImageLoader(mSelfie).execute(URL);
        }
        else {
            Bitmap photo = PhotoUtils.getScaledBitmap(mProfile.getPhoto(), mSelfie.getWidth(), mSelfie.getHeight());
            mSelfie.setImageBitmap(photo);
        }
        Date birthdate =mProfile.getBirthday();
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
        String bday = format.format(birthdate.getTime());
        mName.setText(mProfile.getUserName());
        mBirthday.setText(bday);
        mBIO.setText(mProfile.getMessage());
        mLocation.setText(mProfile.getLocation());
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(flag) {//if sound is loaded
                    soundPool.play(soundId, 1.0f, 0.5f, 1, 0, 1.0f);
                }
                finish();
            }
        });
        //editing user profile
        if(mUser.getUserName().equals(mProfile.getUserName())){
            Log.d("UserInfo", "User " + mUser.getUserName() + " is looking at a mirror");
            final MediaPlayer mp = MediaPlayer.create(this, R.raw.portal2_18_robot_waiting_room_2);
            mp.setVolume(0.25f,0.25f);
            mp.setLooping(true);
            mp.start();

            mPictureButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setPictureURL();
                }
            });
            mBack.setText(getString(R.string.cancel));
            mPictureAddress = mProfile.getPhoto();
            mFavorite.setVisibility(View.INVISIBLE);
            mFavorite.setKeyListener(null);
            mPassword.setHint(R.string.new_password);
            mSelfie.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    takePicture();
                }
            });
            mSubmit.setText(R.string.signup_button_submit);
            mSubmit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        submit();
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            });

        }else{//other users profile
            Log.d("UserInfo", "User " + mUser.getUserName() + " is checking out " + mProfile.getUserName());
            boolean isFollowing = FavoriteList.get(this).isFollowing(uid,pid);
            mFavorite.setChecked(isFollowing);
            mFavorite.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    addFavorite();
                }
            });

            mPassword.setVisibility(View.INVISIBLE);
            mPictureButton.setVisibility(View.INVISIBLE);
            mName.setKeyListener(null);
            mPassword.setKeyListener(null);
            mBirthday.setKeyListener(null);
            mBIO.setKeyListener(null);
            mLocation.setKeyListener(null);
            mSubmit.setVisibility(View.INVISIBLE);
            mSubmit.setKeyListener(null);

        }
    }

    private void setPictureURL(){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        final EditText et = new EditText(UserInfo.this);
        et.setGravity(Gravity.CENTER);
        et.setText(mPictureAddress);
        alert.setMessage(getString(R.string.signup_hint_picture));
        alert.setView(et);
        alert.setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String address = et.getText().toString();
                if(address.startsWith("http")){
                    new ImageLoader(mSelfie).execute(address);
                    mPictureAddress = address;

                }else{
                    Toast.makeText(getApplicationContext(),getString(R.string.user_info_not_valid_string),Toast.LENGTH_SHORT).show();
                }
            }
        });
        alert.setNegativeButton(getString(R.string.cancel),null);
        alert.show();
    }

    private void addFavorite(){
        boolean isFollowing = FavoriteList.get(this).isFollowing(mUser.getId(),mProfile.getId());
        if(!isFollowing) {
            Log.d("UserInfo", mUser.getUserName() + " just followed " + mProfile.getUserName());
            Favorite favorite = new Favorite();
            favorite.setUser(mUser.getId());
            favorite.setFollowing(mProfile.getId());
            FavoriteList.get(this).addFavorite(favorite);
        }
        else{
            Log.d("UserInfo", mUser.getUserName() + " just unfollowed " + mProfile.getUserName());
            FavoriteList.get(this).deleteFavorite(mUser.getId(),mProfile.getId());
        }
    }

    private void takePicture(){
        Intent intent = new Intent();
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        //make filename based on time
        String filename = "IMG_" +System.currentTimeMillis()+ ".jpg";
        //make a file in the external photos directory
        File picturesDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        mPhotoFile = new File(picturesDir, filename);
        Uri photoUri = Uri.fromFile(mPhotoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
        Log.d("Signup", "photo saved at: " + picturesDir.toString());
        startActivityForResult(intent, 0);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("SignupDialog", "PictureTaken!");
        if(mPhotoFile.exists()){
        Bitmap photo = PhotoUtils.getScaledBitmap(mPhotoFile.getPath(), mSelfie.getWidth(), mSelfie.getHeight());
            mPictureAddress = mPhotoFile.getPath();
            mSelfie.setImageBitmap(photo);
        }
        else{
            Bitmap photo = PhotoUtils.getScaledBitmap(mProfile.getPhoto(), mSelfie.getWidth(), mSelfie.getHeight());
            mSelfie.setImageBitmap(photo);
        }

    }

    private void submit()throws NoSuchAlgorithmException, UnsupportedEncodingException {
        String userName = mName.getText().toString().trim();
        String password = mPassword.getText().toString();
        String location = mLocation.getText().toString();
        String birthday = mBirthday.getText().toString();
        String message      = mBIO.getText().toString();

        if(flag) {//if sound is loaded
            soundPool.play(soundId, 1.0f, 0.5f, 1, 0, 1.0f);
        }

        String error =   (userName.isEmpty()?"Username ":"")
                +(password.isEmpty()?"Password ":"")
                +(location.isEmpty()?"Location ":"")
                +(birthday.isEmpty()?"Birthday ":"")
                +(message.isEmpty()?"BIO ":"");

        if(!error.isEmpty()){
            Toast.makeText(this, error + " empty!", Toast.LENGTH_SHORT).show();
            return;
        }
        if(password.length()<6){
            Toast.makeText(this,"Weak password!",Toast.LENGTH_SHORT).show();
            return;
        }
        if(!birthday.matches("((?:19|20)\\d\\d)/(0?[1-9]|1[012])/([12][0-9]|3[01]|0?[1-9])")){
            Toast.makeText(this,"Wrong birthday input!!",Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d("UserInfo","updating user"+mUser.getUserName());
            /*
            * Securely encrypt the user password.
            * double hashing and using user id as salt
            */
        User user = Users.get(this).getUser(mUser.getId());
        Date date = new Date();
        String id = user.getId().toString();
        String passwordHash = SHA.encrypt(password, id);
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
        try {
            date = format.parse(birthday);
            Log.d("SignupDialog",date.toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        user.setUserName(userName);
        user.setPassword(passwordHash);
        user.setBirthday(date);
        user.setLocation(location);
        if(mPictureAddress.isEmpty()){
            user.setPhoto(mPictureAddress);
        }
        else{
            user.setPhoto(mUser.getPhoto());
        }
        user.setMessage(message);
        Users.get(this).updateUser(user);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_user_info, menu);

        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean handled;
        UUID mID = mUser.getId();
        Bundle bundle = new Bundle();
        switch(item.getItemId()) {
            case R.id.menu_item_new_post:
                Log.d("TAG",mID.toString());
                bundle.putSerializable(IntroActivity.USER_ID, mID);
                Intent data1 = new Intent(this, NewPostActivity.class);
                data1.putExtras(bundle);
                startActivity(data1);
                handled = true;
                break;
            case R.id.menu_item_favorite:
                Log.d("TAG", mUser.toString());
                finish();
                handled = true;
                break;
            default:
                handled = super.onOptionsItemSelected(item);
                break;
        }
        return handled;
    }

    @Override
    protected void onDestroy() {
        soundPool.release();
        soundPool = null;
        super.onDestroy();
    }
}