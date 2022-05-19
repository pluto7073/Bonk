package io.sylmos.bonk.ui.dashboard;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import io.sylmos.bonk.R;
import io.sylmos.bonk.accounts.AccountManager;

public class EditProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        AccountManager.INSTANCE.user.refreshFromDB().then((successful, result) -> {
            if (!successful) return;
            EditText username, aboutMe;
            username = findViewById(R.id.change_username_box);
            aboutMe = findViewById(R.id.change_about_box);
            username.setText(AccountManager.INSTANCE.user.username);
            aboutMe.setText(AccountManager.INSTANCE.user.about);
        });
    }

    public void saveChanges(View view) {
        EditText username = findViewById(R.id.change_username_box),
                aboutMe = findViewById(R.id.change_about_box),
                password = findViewById(R.id.confirm_password);
        String usernameT = username.getText().toString(),
                passwordT = password.getText().toString();
        if (!usernameT.equals(AccountManager.INSTANCE.user.username)) {
            if (!AccountManager.INSTANCE.user.changeUsername(usernameT, passwordT, this)) {
                Toast.makeText(this, "Incorrect Password", Toast.LENGTH_LONG).show();
                return;
            }
        }
        AccountManager.INSTANCE.user.about = aboutMe.getText().toString();
        AccountManager.INSTANCE.user.saveInDB();
        finish();
    }

    public void logOut(View view) {
        AccountManager.INSTANCE.logOut(this);
    }

}