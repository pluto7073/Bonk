package io.sylmos.bonk;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

import org.w3c.dom.Text;

import io.sylmos.bonk.accounts.AccountManager;
import io.sylmos.bonk.databinding.ActivityMainBinding;

public class SignupActivity extends AppCompatActivity {

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

        setContentView(R.layout.activity_signup);
        findViewById(R.id.errorSignup).setVisibility(View.GONE);
        findViewById(R.id.noMatchPass).setVisibility(View.GONE);
    }

    public void onSignUpClicked(View view) {
        EditText emailText = findViewById(R.id.email2);
        EditText passwordText = findViewById(R.id.password2);
        EditText password2Text = findViewById(R.id.password3);
        if (!password2Text.getText().toString().equals(passwordText.getText().toString())) {
            TextView noMatchPass = findViewById(R.id.noMatchPass);
            TextView errorSignup = findViewById(R.id.errorSignup);
            noMatchPass.setVisibility(View.VISIBLE);
            errorSignup.setVisibility(View.GONE);
            return;
        }
        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();
        AccountManager.INSTANCE.createAccountWithUsernameAndPassword(email, password, this)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        next();
                    } else {
                        TextView noMatchPass = findViewById(R.id.noMatchPass);
                        TextView errorSignup = findViewById(R.id.errorSignup);
                        noMatchPass.setVisibility(View.GONE);
                        errorSignup.setVisibility(View.VISIBLE);
                    }
                });
    }

    public void onExistingAccClicked(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    public void next() {
        Intent intent = new Intent(this, AccountSetupActivity.class);
        startActivity(intent);
        finish();
    }

}