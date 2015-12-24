package csc296.project2.control.PostBoard;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Date;
import java.util.UUID;

import csc296.project2.R;
import csc296.project2.control.IntroActivity;
import csc296.project2.model.Post.Post;
import csc296.project2.model.Post.PostBoard;


public class NewPostActivity extends AppCompatActivity {
    public static final String MESSAGE_KEY = "Message_Key";
    public static final String BUNDLE_KEY = "Bundle_Key";
    private EditText mMessage;
    private UUID mID;
    private Post mPost;
    private Button mSubmit;
    private Button mCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_post);

        Intent intent = getIntent();
        mID = (UUID)intent.getSerializableExtra(IntroActivity.USER_ID);

        // Inflate the layout for this fragment
        mMessage = (EditText)findViewById(R.id.fragment_post_edittext_message);
        mSubmit = (Button)findViewById(R.id.fragment_post_button);
        mCancel = (Button)findViewById(R.id.fragment_post_cancel);
        Bundle args = savedInstanceState;
        mPost = new Post();
        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                post();
            }
        });
        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });

        if (savedInstanceState != null) {
            // Restore last state
            String message = savedInstanceState.getString(MESSAGE_KEY);
            mMessage.setText(message);
            Log.d("tag",message);
        }
        getSupportActionBar().setSubtitle(R.string.subtitle_new_post);
    }

    private void cancel(){
        this.finish();
    }

    private void post(){
        String message = mMessage.getText().toString();
        Log.d("PostActivity","Posting message:"+message);
        if(message.trim().isEmpty()){
            Toast.makeText(this,R.string.fragment_post_message_empty,Toast.LENGTH_SHORT).show();
            return;
        }
        mPost.setPostTime(new Date());
        mPost.setMessage(message);
        mPost.setPoster(mID);
        PostBoard.get(this).addPost(mPost);
        this.finish();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(MESSAGE_KEY,mMessage.getText().toString());
    }

}
