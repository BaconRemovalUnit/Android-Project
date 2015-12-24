package csc296.project2.control.PostBoard;


import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import csc296.project2.R;
import csc296.project2.control.User.UserInfo;
import csc296.project2.control.Utils.ImageLoader;
import csc296.project2.control.Utils.PhotoUtils;
import csc296.project2.model.Post.Post;
import csc296.project2.model.Post.PostBoard;
import csc296.project2.model.User.User;
import csc296.project2.model.User.Users;

/**
 * A simple {@link Fragment} subclass.
 */
public class ViewPostDialog extends DialogFragment {

    private TextView mUsername;
    private TextView mMessage;
    private TextView mPostDate;
    private ImageView mSelfie;
    private User mUser;
    private User mPoster;

    public ViewPostDialog() {
        // Required empty public constructor
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Log.d("Here", "onCreateDialog called");
        final LayoutInflater inflater = LayoutInflater.from(getActivity());
        View view = inflater.inflate(R.layout.fragment_view_post, null);
        Bundle args = getArguments();
        if(args != null) {
            Post post = PostBoard.get(getContext()).getPost((UUID) args.getSerializable(PostListFragment.POST_ID));
            mPoster = Users.get(getContext()).getUser(post.getPoster());
            mUser = Users.get(getContext()).getUser((UUID)args.getSerializable(PostListFragment.USER_ID));
            mSelfie = (ImageView) view.findViewById(R.id.fragment_view_post_selfie);
            mMessage = (TextView)view.findViewById(R.id.fragment_view_post_message);
            mPostDate = (TextView)view.findViewById(R.id.fragment_view_post_date);
            mUsername = (TextView)view.findViewById(R.id.fragment_view_post_username);


            String URL = mPoster.getPhoto();
            if(URL.startsWith("http")){
                new ImageLoader(mSelfie).execute(URL);
            }
            else {
                Bitmap photo = PhotoUtils.getScaledBitmap(mPoster.getPhoto(), mSelfie.getWidth(), mSelfie.getHeight());
                mSelfie.setImageBitmap(photo);
            }

            mUsername.setText(mPoster.getUserName());
            mPostDate.setText(post.getPostTime().toString());
            mMessage.setText(post.getMessage());

            mSelfie.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), UserInfo.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(PostListFragment.USER_ID,mUser.getId());
                    bundle.putSerializable(PostListFragment.POSTER_ID, mPoster.getId());
                    intent.putExtras(bundle);
                    startActivity(intent);

                }
            });

        }
        return new AlertDialog.Builder(getContext())
                .setView(view)
                .create();
    }


}
