package io.sylmos.bonk;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.util.ArrayList;

import io.sylmos.bonk.database.DatabaseUser;
import io.sylmos.bonk.database.Post;
import io.sylmos.bonk.databinding.ActivityPostViewerBinding;
import io.sylmos.bonk.ui.postviewer.PostViewerRecyclerAdapter;

public class PostViewerActivity extends AppCompatActivity {

    public static Post currentPost;

    private ActivityPostViewerBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_viewer);

        binding = ActivityPostViewerBinding.inflate(getLayoutInflater());

        RecyclerView recyclerView = findViewById(R.id.post_files_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        if (currentPost.files == null) currentPost.files = new ArrayList<>();
        PostViewerRecyclerAdapter adapter = new PostViewerRecyclerAdapter(this, currentPost.files);
        recyclerView.setAdapter(adapter);

        DatabaseUser user = new DatabaseUser(currentPost.author, true);
        user.avatar().then((successful1, result1) -> {
            InputStream stream = (InputStream) result1;
            Bitmap bitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeStream(stream), 150, 150, false);
            ((ImageButton) findViewById(R.id.post_pfp)).setImageBitmap(bitmap);
        });
        ((TextView) findViewById(R.id.post_username)).setText(user.username);
        ((TextView) findViewById(R.id.post_content)).setText(currentPost.text);
    }
}