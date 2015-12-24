package csc296.project2.control;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import csc296.project2.R;
import csc296.project2.control.Utils.ImageLoader;
import csc296.project2.control.Utils.PhotoUtils;
import csc296.project2.control.Utils.SHA;
import csc296.project2.model.User.User;
import csc296.project2.model.User.Users;

public class SignupDialog extends DialogFragment {
    public static final String KEY_ID = "ID";

    private EditText mPhotoURL;
    private EditText mName;
    private EditText mPassword;
    private EditText mBirthday;
    private EditText mBIO;
    private EditText mLocation;
    private ImageView mSelfie;
    private Button  mSubmit;
    private File mPhotoFile;
    private String mPhotoAddress = "";

    public SignupDialog() {
        // Required empty public constructor
        Bundle args = new Bundle();
        this.setArguments(args);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Log.d("Here", "onCreateDialog called");
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View view = inflater.inflate(R.layout.fragment_signup, null);
        Bundle args = getArguments();
        Toast.makeText(this.getContext(),getString(R.string.signup_toast),Toast.LENGTH_SHORT).show();
        if(args != null) {
            mPhotoURL = (EditText) view.findViewById(R.id.signup_edittext_picutre);
            mName = (EditText) view.findViewById(R.id.signup_edittext_username);
            mPassword = (EditText) view.findViewById(R.id.signup_edittext_password);
            mBirthday = (EditText) view.findViewById(R.id.signup_edittext_birthday);
            mBIO = (EditText) view.findViewById(R.id.signup_edittext_bio);
            mLocation = (EditText) view.findViewById(R.id.signup_edittext_location);
            mSubmit = (Button) view.findViewById(R.id.signup_button_submit);
            mSelfie = (ImageView) view.findViewById(R.id.signup_imageview_selfie);

            mPhotoURL.setOnFocusChangeListener(new View.OnFocusChangeListener() {

                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        String URL = mPhotoURL.getText().toString();
                        if((!URL.equals(mPhotoAddress))&&(!URL.isEmpty())) {
                            new ImageLoader(mSelfie).execute(URL);
                            mPhotoAddress = URL;
                        }
                    }
                }
            });
            mSubmit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            submit();
                        }
                        catch (NoSuchAlgorithmException e){
                            e.printStackTrace();
                        }
                        catch (UnsupportedEncodingException e){
                            e.printStackTrace();
                        }
                    }
                });

            mSelfie.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    takePicture();
                }
            });

        }
        return new AlertDialog.Builder(getContext())
                .setView(view)
                .setTitle(R.string.signup_title)
                .create();
    }

    private void takePicture(){
        Intent intent = new Intent();
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);

        //make filename based on time
        String filename = "IMG_" +System.currentTimeMillis()+ ".jpg";
        //make a file in the external photos directory
        File picturesDir =
                getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        mPhotoFile = new File(picturesDir, filename);
        Uri photoUri = Uri.fromFile(mPhotoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
        Log.d("Signup", "photo saved at: " + picturesDir.toString());
        startActivityForResult(intent, 0);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("SignupDialog","PictureTaken!");
        Bitmap photo = PhotoUtils.getScaledBitmap(mPhotoFile.getPath(),
                mSelfie.getWidth(), mSelfie.getHeight());
        mSelfie.setImageBitmap(photo);
        mPhotoURL.setText(mPhotoFile.getPath());
        mPhotoAddress = mPhotoFile.getPath();
    }

    private void submit()throws NoSuchAlgorithmException, UnsupportedEncodingException {
        String userName = mName.getText().toString().trim();
        String password = mPassword.getText().toString();
        String location = mLocation.getText().toString();
        String birthday = mBirthday.getText().toString();
        String message      = mBIO.getText().toString();
        boolean photo = mPhotoAddress.isEmpty();

//

        String error =   (userName.isEmpty()?"Username ":"")
                        +(password.isEmpty()?"Password ":"")
                        +(location.isEmpty()?"Location ":"")
                        +(birthday.isEmpty()?"Birthday ":"")
                        +(message.isEmpty()?"BIO ":"")
                        +(photo?"Selfie":"");

        if(!error.isEmpty()){//there are errors
            Toast.makeText(getContext(),error+" empty!",Toast.LENGTH_SHORT).show();
            return;
        }
        if(password.length()<6){
            Toast.makeText(getContext(),"Weak password!",Toast.LENGTH_SHORT).show();
            return;
        }
        if(!birthday.matches("((?:19|20)\\d\\d)/(0?[1-9]|1[012])/([12][0-9]|3[01]|0?[1-9])")){
            Toast.makeText(getContext(),"Wrong birthday input!!",Toast.LENGTH_SHORT).show();
            return;
        }

        if(Users.get(getContext()).hasUser(userName)) {
            Toast.makeText(getContext(),"Username exists!",Toast.LENGTH_SHORT).show();
            return;
        }

            Log.d("SignupDialog","Creating new user");
            /*
            * Securely encrypt the user password.
            * double hashing and using user id as salt
            */
            User user = new User();
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
            user.setPhoto(mPhotoAddress);
            user.setMessage(message);
            Users.get(getContext()).addUser(user);
            dismiss();
        }
    }