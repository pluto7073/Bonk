package io.sylmos.bonk;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.firebase.storage.FileDownloadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

import io.sylmos.bonk.accounts.AccountManager;
import io.sylmos.bonk.database.DatabaseUser;
import io.sylmos.bonk.database.UserPostManager;
import io.sylmos.bonk.databinding.ActivityUserViewerBinding;
import io.sylmos.bonk.storage.StorageManager;
import io.sylmos.bonk.ui.home.PostsRecyclerAdapter;

public class UserViewerActivity extends AppCompatActivity {

    private ActivityUserViewerBinding binding;
    public static DatabaseUser currentViewedUser;

    public volatile ArrayList<String> loadedPosts = new ArrayList<>();
    public volatile int totalPostsLoaded = 0;

    public PostsRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserViewerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        if (currentViewedUser == null) {
            currentViewedUser = new DatabaseUser("0000", false);
            currentViewedUser.username = "Whoops... This is Awkward";
            currentViewedUser.about = "Something seems to have gone wrong in loading this user's profile :)";
        }
        setContentView(R.layout.activity_user_viewer);
        System.out.println(currentViewedUser.username);
        ((TextView) findViewById(R.id.userUsernameView)).setText(currentViewedUser.username);
        ((TextView) findViewById(R.id.userAboutView)).setText(currentViewedUser.about);
        currentViewedUser.refreshFromDB().then((s, r) -> currentViewedUser.avatar().then((successful, result) -> {
            Bitmap bitmap = BitmapFactory.decodeStream((InputStream) result);
            ((ImageView) findViewById(R.id.userPfpView)).setImageBitmap(bitmap);
        }));
        if (AccountManager.INSTANCE.user.isSubscribedTo(currentViewedUser.id)) {
            ((Button) findViewById(R.id.follow_btn)).setText(R.string.unfollow);
        }
        findViewById(R.id.follow_btn).setOnClickListener(this::followBtnPressed);
        findViewById(R.id.message_btn).setOnClickListener(this::messageBtnPressed);
        UserPostManager posts = currentViewedUser.posts();
        posts.refresh().then((successful, result) -> {
            if (!successful) return;
            loadedPosts = posts.getLast10Posts();
            totalPostsLoaded = loadedPosts.size();
            RecyclerView view = findViewById(R.id.user_posts_recycler);
            view.setLayoutManager(new LinearLayoutManager(UserViewerActivity.this));
            adapter = new PostsRecyclerAdapter(this, loadedPosts);
            view.setAdapter(adapter);
        });
    }

    public void followBtnPressed(View view) {
        if (AccountManager.INSTANCE.user.isSubscribedTo(currentViewedUser.id)) {
            AccountManager.INSTANCE.user.removeSubscription(currentViewedUser.id);
        } else {
            AccountManager.INSTANCE.user.addSubscription(currentViewedUser.id);
        }
    }

    public void messageBtnPressed(View view) {
        //TODO
    }

    public void finish() {
        currentViewedUser = null;
        super.finish();
    }

}