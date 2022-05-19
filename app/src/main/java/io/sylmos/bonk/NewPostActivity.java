package io.sylmos.bonk;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import io.sylmos.bonk.accounts.AccountManager;
import io.sylmos.bonk.database.Post;
import io.sylmos.bonk.database.PostManager;
import io.sylmos.bonk.databinding.ActivityNewPostBinding;
import io.sylmos.bonk.ui.newpost.NewPostRecyclerAdapter;
import io.sylmos.bonk.utils.Utils;

public class NewPostActivity extends AppCompatActivity {

    private static final int PICK_RESULT = 924783;
    private static final int CAMERA_RESULT = 98573523;

    private ActivityNewPostBinding binding;
    private ArrayList<File> files;
    private NewPostRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityNewPostBinding.inflate(getLayoutInflater());
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        files = new ArrayList<>();

        setContentView(R.layout.activity_new_post);

        RecyclerView recyclerView = findViewById(R.id.new_post_files_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new NewPostRecyclerAdapter(this, files);
        recyclerView.setAdapter(adapter);
    }

    public void createNewPost(View view) {
        EditText postC = findViewById(R.id.new_post_content);
        String content = postC.getText().toString();
        HashMap<String, Object> postData = new HashMap<>();
        long id = System.currentTimeMillis() / 1000L;
        postData.put("id", id);
        postData.put("text", content);
        postData.put("author", AccountManager.INSTANCE.user.id);
        postData.put("files", new ArrayList<String>());
        View loading = getLayoutInflater().inflate(R.layout.posting_loading_layout, (ViewGroup) view.getRootView(), false);
        PopupWindow window = new PopupWindow(loading, 400, 400, true);

        window.showAtLocation(view.getRootView(), Gravity.CENTER, getWindow().getAttributes().width / 2, getWindow().getAttributes().height / 2);
        PostManager.INSTANCE.post(new Post(postData), files).then((successful, result) -> {
            if (successful) {
                window.dismiss();
                finish();
            } else {
                Toast.makeText(this, "An error occurred, please try again", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            try {
                switch (requestCode) {
                    case CAMERA_RESULT:
                        Bitmap cameraImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                        File cameraFile = Utils.saveBitmap(cameraImage, new File(getFilesDir(), "posts/camera-" + System.currentTimeMillis()));
                        addImage(cameraFile);
                        break;
                    case PICK_RESULT:
                        Bitmap pickImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                        File pickFile = Utils.saveBitmap(pickImage, new File(getFilesDir(), "posts/gallery-" + System.currentTimeMillis()));
                        addImage(pickFile);
                        break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void addImage(File file) {
        files.add(file);
        RecyclerView recyclerView = findViewById(R.id.new_post_files_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new NewPostRecyclerAdapter(this, files);
        recyclerView.setAdapter(adapter);
    }

    public void attachCameraImage(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA_RESULT);
    }

    public void attachImage(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_RESULT);
    }

}