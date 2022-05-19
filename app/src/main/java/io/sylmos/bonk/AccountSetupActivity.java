package io.sylmos.bonk;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import java.io.IOException;

import io.sylmos.bonk.accounts.AccountManager;
import io.sylmos.bonk.storage.StorageManager;
import io.sylmos.bonk.utils.Utils;

public class AccountSetupActivity extends AppCompatActivity {

    private static final int PICK_RESULT = 2935625;
    private Bitmap pfp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_setup);
    }

    public void choosePfp(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_RESULT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_RESULT && resultCode == RESULT_OK && null != data) {
            Uri uri = data.getData();
            try {
                Bitmap image = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                ImageButton pfpBtn = findViewById(R.id.pfpChooser);
                pfpBtn.setImageBitmap(image);
                pfp = image;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void done(View view) {
        String fileName = System.currentTimeMillis() + AccountManager.INSTANCE.user.id + ".png";
        StorageManager.INSTANCE.saveFile("images/profilephotos/" + fileName, Utils.getInputStream(pfp));
        EditText usernameText = findViewById(R.id.usernameBox);
        EditText aboutBox = findViewById(R.id.aboutBox);
        String username = usernameText.getText().toString();
        String about = aboutBox.getText().toString();
        AccountManager.INSTANCE.user.username = username;
        AccountManager.INSTANCE.user.about = about;
        AccountManager.INSTANCE.user.pfp = "images/profilephotos/" + fileName;
        AccountManager.INSTANCE.user.saveInDB();

        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

}