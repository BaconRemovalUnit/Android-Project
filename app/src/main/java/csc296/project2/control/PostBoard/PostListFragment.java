package csc296.project2.control.PostBoard;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import csc296.project2.R;
import csc296.project2.control.IntroActivity;
import csc296.project2.control.Utils.ImageLoader;
import csc296.project2.control.Utils.PhotoUtils;
import csc296.project2.database.Favorite.FavoriteDatabaseSchema;
import csc296.project2.model.Favorite.Favorite;
import csc296.project2.model.Favorite.FavoriteList;
import csc296.project2.model.Post.*;
import csc296.project2.model.User.User;
import csc296.project2.model.User.Users;


/**
 * A simple {@link Fragment} subclass.
 */
public class PostListFragment extends Fragment {
    private static final String TAG = "PostListFragment";
    public static final String POST_ID = "Post_ID";
    public static final String USER_ID = "User_ID";
    public static final String POSTER_ID = "Poster_ID";

    private PostBoard mPostBoard;
    private Users mUsers;
    private RecyclerView mRecyclerView;
    private PostAdapter mAdapter;
    private UUID mUserID;
    private Boolean mShowFavorite;

    public PostListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mPostBoard = PostBoard.get(getContext());
        mUsers = Users.get(getContext());
        mUsers.getUsers();
        mUserID = (UUID)getArguments().getSerializable(IntroActivity.USER_ID);
        mShowFavorite = getArguments().getBoolean(PostActivity.SHOW_FAVORITE);
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_postlist, container, false);

        mRecyclerView = (RecyclerView)view.findViewById(R.id.recycler_view_posts);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        updateUI();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    public void updateUI() {
        if(!mShowFavorite) {
            List<Post> posts = mPostBoard.getPosts();
            Log.d(TAG, posts.toString());
            if (mAdapter == null) {
                mAdapter = new PostAdapter(posts);
                mRecyclerView.setAdapter(mAdapter);
            } else {
                mAdapter.setPosts(posts);
            }
        }else{

            FavoriteList.get(getContext()).getFavorites();
            List<Post> allPosts = new LinkedList<Post>();
            List<Favorite> favorites = FavoriteList.get(getContext()).getUserFavorites(mUserID);
            for(Favorite f :favorites){
                List<Post> posts = mPostBoard.getUserPosts(f.getFollowing());
                allPosts.addAll(posts);
            }
            Collections.sort(allPosts, new Comparator<Post>() {
                @Override
                public int compare(Post lhs, Post rhs) {
                   return rhs.getPostTime().compareTo(lhs.getPostTime());
                }
            });
            Log.d(TAG, allPosts.toString());
            if (mAdapter == null) {
                mAdapter = new PostAdapter(allPosts);
                mRecyclerView.setAdapter(mAdapter);
            } else {
                mAdapter.setPosts(allPosts);
            }
        }
    }

    private class PostAdapter extends RecyclerView.Adapter<PostViewHolder> {
        private static final String TAG = "PostAdapter";
        private List<Post> mPosts;

        public PostAdapter(List<Post> posts) {
            mPosts = posts;
        }

        @Override
        public PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Log.d(TAG, "onCreateViewHolder() called");
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View itemView = inflater.inflate(R.layout.view_post, parent, false);
            return new PostViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(PostViewHolder holder, int position) {
            Log.d(TAG, "onBindViewHolder(holder," + position +") called");
            Post movie = mPosts.get(position);
            holder.bind(movie);
        }

        @Override
        public int getItemCount() {
            return mPosts.size();
        }

        /**
         * This method is called whenever the set of posts changes.  This could mean that a movie
         * was added, removed, or updated.
         *
         * @param posts The current set of posts.
         */
        public void setPosts(List<Post> posts) {
            mPosts = posts;
            notifyDataSetChanged();
        }
    }

    private class PostViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final TextView mUser;
        private final TextView mDate;
        private final TextView mMessage;
        private final ImageView mProfilePicture;
        private Post mPost;

        public PostViewHolder(View itemView) {
            super(itemView);
            mUser = (TextView)itemView.findViewById(R.id.view_post_id);
            mDate = (TextView)itemView.findViewById(R.id.view_post_date);
            mMessage = (TextView)itemView.findViewById(R.id.view_post_message);
            mProfilePicture = (ImageView)itemView.findViewById(R.id.view_post_image);
            itemView.setOnClickListener(this);
        }

        public void bind(Post post) {
            mPost = post;
            User poster = Users.get(getContext()).getUser(post.getPoster());

            String URL = poster.getPhoto();

            if(URL.startsWith("http")){

//                new ImageLoader(mProfilePicture).execute(URL);
                Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.default_user_image);
                mProfilePicture.setImageBitmap(bm);
            }
            else {
                Bitmap photo = PhotoUtils.getScaledBitmap(poster.getPhoto(), mProfilePicture.getWidth(), mProfilePicture.getHeight());
                mProfilePicture.setImageBitmap(photo);

            }

            mUser.setText(poster.getUserName());
            mDate.setText(post.getPostTime().toString());
            mMessage.setText(post.getMessage());
        }

        @Override
        public void onClick(View v) {
            Bundle bundle = new Bundle();
            bundle.putSerializable(POST_ID, mPost.getID());
            bundle.putSerializable(USER_ID,mUserID);
// set Fragmentclass Arguments
            ViewPostDialog dialog = new ViewPostDialog();
            dialog.setArguments(bundle);
            dialog.show(getFragmentManager(), "ViewPostDialog");
        }

        public void updateUI() {
            List<Post> posts = mPostBoard.getPosts();
            Log.d(TAG, posts.toString());
            if(mAdapter == null) {
                mAdapter = new PostAdapter(posts);
                mRecyclerView.setAdapter(mAdapter);
            }
            else {
                mAdapter.setPosts(posts);
            }
        }
    }
}
