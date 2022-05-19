package io.sylmos.bonk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

import io.sylmos.bonk.accounts.AccountManager;
import io.sylmos.bonk.databinding.ActivityMainBinding;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        View mControlsView = binding.fullscreenContentControls;
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.GONE);
        setContentView(R.layout.activity_login);
        findViewById(R.id.errorLogin).setVisibility(View.GONE);
    }

    public void onSignInClicked(View view) {
        EditText emailText = findViewById(R.id.email);
        EditText passwordText = findViewById(R.id.password);
        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();
        AccountManager.INSTANCE.signInWithUsernameAndPassword(email, password, this)
                .addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                next();
            } else {
                TextView errorLogin = findViewById(R.id.errorLogin);
                errorLogin.setVisibility(View.VISIBLE);
            }
        });
    }

    public void onNoAccClicked(View view) {
        Intent intent = new Intent(this, SignupActivity.class);
        startActivity(intent);
        finish();
    }

    private void next() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

}