package io.sylmos.bonk.database;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;

import io.sylmos.bonk.utils.TaskExtender;

public class MessageManager {

    public String channelId;
    public int position;
    public int totalMessages;
    public final DatabaseManager manager;

    private ArrayList<String> ids;
    private ArrayList<Message> messages;

    public MessageManager(String channelId) {
        this.manager = DatabaseManager.INSTANCE;
        this.ids = new ArrayList<>();
        this.channelId = channelId;
        this.position = 0;

    }

    public void addNext20() {
        int tempPosition = position;
        while (!(tempPosition < 0) && (tempPosition >= position - 20)) {
            TaskExtender<Object> extender = manager.getValueAsync("messages/" + channelId + ids.get(tempPosition));
        }
    }

    public TaskExtender<Void> reloadMessages() {
        TaskExtender<Void> extender = new TaskExtender<Void>("ReloadMessagesTask") {
            @Override
            public void complete() {
                manager.getValue("messages/" + channelId).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                    } else {
                        this.markComplete(false);
                    }
                });
            }
        };
        Thread thread = new Thread(extender::complete);
        thread.setName(extender.getName());
        thread.start();
        return extender;
    }

}
