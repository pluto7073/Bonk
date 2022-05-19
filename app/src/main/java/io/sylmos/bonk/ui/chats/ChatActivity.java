package io.sylmos.bonk.ui.chats;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import io.sylmos.bonk.R;
import io.sylmos.bonk.database.Chat;
import io.sylmos.bonk.databinding.ActivityChatBinding;

public class ChatActivity extends AppCompatActivity {

    public static final String CONVERSATION_ID = "io.sylmos.bonk.CONVERSATION_ID";

    private String convoId = "";

    private Chat chat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        convoId = getIntent().getStringExtra(CONVERSATION_ID);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        setContentView(R.layout.activity_chat);

        ((ImageButton) findViewById(R.id.back_btn_chats)).setOnClickListener(this::onBackPressed);

        chat = new Chat(convoId, this);
        chat.refreshFromDB().then((successful, result) -> {
            if (successful) {
                ((TextView) findViewById(R.id.chat_title_text)).setText(chat.name);
            }
        });
    }

    public void onBackPressed(View view) {
        finish();
    }

}