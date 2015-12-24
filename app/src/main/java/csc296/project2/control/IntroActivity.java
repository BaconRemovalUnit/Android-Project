package csc296.project2.control;

import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import csc296.project2.R;
import csc296.project2.control.PostBoard.PostActivity;

import csc296.project2.control.Utils.SHA;
import csc296.project2.model.User.User;
import csc296.project2.model.User.Users;

public class IntroActivity extends AppCompatActivity {
    private static final String TAG = "IntroActivity";
    public static final String USER_ID = "Current_User_ID";
    private SoundPool soundPool;
    private int soundId;
    private boolean flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        EditText pwET = (EditText)findViewById(R.id.intro_edittext_password);
        final Button signup = (Button)findViewById(R.id.intro_button_signup);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp();
            }
        });
        soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        soundId = soundPool.load(this,R.raw.portal2_sfx_button_positive, 1);
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                flag = true;
                Log.d(TAG,"sound loaded!");
            }
        });



        final Button login = (Button)findViewById(R.id.intro_button_login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logIn();
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        return super.onOptionsItemSelected(item);
    }

    public void signUp(){
        Log.d(TAG, "sign up new user");
        if(flag) {//if sound is loaded
            soundPool.play(soundId, 1.0f, 0.5f, 1, 0, 1.0f);
        }
        SignupDialog dialog = new SignupDialog();
        dialog.show(getSupportFragmentManager(),"SignupDialog");
    }

    public void logIn() {
        if(flag) {//if sound is loaded
            soundPool.play(soundId, 1.0f, 0.5f, 1, 0, 1.0f);
        }
        EditText pwET = (EditText)findViewById(R.id.intro_edittext_password);
        EditText nameET = (EditText)findViewById(R.id.intro_edittext_email);

        String pw = pwET.getText().toString();
        String name = nameET.getText().toString();

        //try to login
        try {
            if(!pw.isEmpty()&&!name.isEmpty()){
                User user = Users.get(this).getUser(name);
                if(user!=null){

                    String hashedPassword = SHA.encrypt(pw, user.getId().toString());
                    if(hashedPassword.equals(user.getPassword())){
                        Log.d(TAG, "User " + user.getUserName() + " Logging in");
                        //logging in
                        //Play a welcom sound

                        nameET.setText("");
                        pwET.setText("");
                        Intent data = new Intent(this, PostActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(USER_ID,user.getId());
                        data.putExtras(bundle);
                        Log.d(TAG,"User id put into intent:"+user.getId().toString());
                        startActivity(data);
                    }
                    else{
                        Toast.makeText(getApplicationContext(),getString(R.string.signup_error_input),Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(getApplicationContext(),getString(R.string.signup_error_input),Toast.LENGTH_SHORT).show();
                }
            }
            else {
                Toast.makeText(getApplicationContext(),getString(R.string.signup_empty_input),Toast.LENGTH_SHORT).show();
            }
        }
        catch(NoSuchAlgorithmException e){
            e.printStackTrace();
        }
        catch(UnsupportedEncodingException e){
            e.printStackTrace();
        }

    }

    @Override
    protected void onDestroy() {
        soundPool.release();
        soundPool = null;
        super.onDestroy();
    }
}
