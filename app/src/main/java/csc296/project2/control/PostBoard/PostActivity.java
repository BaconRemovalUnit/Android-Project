package csc296.project2.control.PostBoard;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.EventListener;
import java.util.UUID;

import csc296.project2.R;
import csc296.project2.control.IntroActivity;
import csc296.project2.control.User.UserInfo;


public class PostActivity extends AppCompatActivity {
    public static final String SHOW_FAVORITE = "Show_Favorite";
    private static final String TAG = "PostActivity";
    private Menu menu;
    private PostListFragment mPostList;
    private UUID mID;
    private boolean mFavorite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate(Bundle) called");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        mID = (UUID)getIntent().getSerializableExtra(IntroActivity.USER_ID);
        mFavorite = getIntent().getBooleanExtra(SHOW_FAVORITE,false);

        mPostList = new PostListFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(SHOW_FAVORITE, mFavorite);
        bundle.putSerializable(IntroActivity.USER_ID, mID);
        mPostList.setArguments(bundle);

        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction()
                .add(R.id.frame_layout_post, mPostList, null)
                .commit();

        if(!mFavorite) {
            getSupportActionBar().setSubtitle(R.string.subtitle_post_list);
        }
        else{
            getSupportActionBar().setSubtitle(R.string.subtitle_favorite_list);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_post_activity, menu);
        if(mFavorite){
            MenuItem item = menu.findItem(R.id.menu_item_favorite);
            item.setIcon(R.drawable.activity_feed);
        }else{
            MenuItem item = menu.findItem(R.id.menu_item_favorite);
            item.setIcon(R.drawable.activity_favorite);
        }
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean handled;

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
                Log.d("TAG", mID.toString());
                getIntent().putExtra(SHOW_FAVORITE,!mFavorite);
                finish();
                startActivity(getIntent());
                handled = true;
                break;
            case R.id.menu_item_profile:
                Intent data3 = new Intent(this, UserInfo.class);
                Log.d("TAG", mID.toString());
                bundle.putSerializable(PostListFragment.USER_ID, mID);
                bundle.putSerializable(PostListFragment.POSTER_ID, mID);
                data3.putExtras(bundle);
                startActivity(data3);
                handled = true;
                break;
            default:
                handled = super.onOptionsItemSelected(item);
                break;
        }
        return handled;
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == Activity.RESULT_OK) {
            mPostList.updateUI();
        }
    }


}
