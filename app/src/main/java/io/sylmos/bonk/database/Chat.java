package io.sylmos.bonk.database;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.sylmos.bonk.HomeActivity;
import io.sylmos.bonk.MainActivity;
import io.sylmos.bonk.R;
import io.sylmos.bonk.utils.ChildChangedListener;
import io.sylmos.bonk.utils.Utils;
import io.sylmos.bonk.utils.ValueChangedListener;

public class Chat extends Base {

    private static boolean notifChannelCreated = false;
    public static String CHANNEL_ID = "786r5ftghjiu8t6ufy";
    public static HashMap<String, HashMap<String, Object>> chatsCache = new HashMap<>();

    public String id, name;
    public List<String> last5Messages;
    public List<String> members;

    private final Context context;
    private MessageManager messageManager = null;

    public Chat(String id, Context context) {
        super(DatabaseManager.INSTANCE, "chats/" + id);
        this.id = id;
        this.context = context;
        if (chatsCache.containsKey(id)) {
            loadFromHashMap(chatsCache.get(id));
        } else {
            addListeners();
            refreshFromDB();
        }
    }

    public MessageManager messageManager() {
        if (messageManager == null) {
            messageManager = new MessageManager(id);
        }
        return messageManager;
    }

    @Override
    public HashMap<String, Object> toHashMap() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("name", name);
        map.put("last-5-messages", Utils.listToHashMap(last5Messages));
        map.put("members", Utils.listToHashMap(members));
        return map;
    }

    @Override
    public void loadFromHashMap(HashMap<String, Object> map) {
        this.id = String.valueOf(map.get("id"));
        this.name = (String) map.get("name");
        this.last5Messages = (List<String>) map.get("last-5-messages");
        this.members = (List<String>) map.get("members");
        chatsCache.put(id, map);
    }

    private void addListeners() {
        manager.getReference(dbPath).addChildEventListener(new ChildChangedListener() {
            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (snapshot.getKey().equals("name")) {
                    name = (String) snapshot.getValue();
                }
            }
        });
        manager.getReference(dbPath).child("members").addValueEventListener((ValueChangedListener) snapshot -> {
            members = (List<String>) snapshot.getValue();
        });
        manager.getReference("messages/" + id).addChildEventListener(new ChildChangedListener() {
            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                last5Messages.set(0, last5Messages.get(1));
                last5Messages.set(1, last5Messages.get(2));
                last5Messages.set(2, last5Messages.get(3));
                last5Messages.set(3, last5Messages.get(4));
                last5Messages.set(4, snapshot.getKey());
                Message message = new Message((HashMap<String, Object>) snapshot.getValue());
                //Notification
                System.out.println(":)))");
                DatabaseManager.INSTANCE.getValue("users/" + message.author).addOnCompleteListener(task -> {
                    Intent intent = new Intent(context, HomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);

                    NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                            .setSmallIcon(R.mipmap.ic_launcher_round)
                            .setContentTitle(message.author)
                            .setContentText(message.content)
                            .setStyle(new NotificationCompat.BigTextStyle().bigText(message.content))
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                            .setContentIntent(pendingIntent)
                            .setAutoCancel(true);
                    NotificationManagerCompat noMa = NotificationManagerCompat.from(context);

                    noMa.notify(Integer.parseInt(message.id), builder.build());
                });
            }
        });
    }

    public static void createNotificationChannel() {
        if (notifChannelCreated) return;
        CharSequence name = MainActivity.dis.getString(R.string.channel_name);
        String desc = MainActivity.dis.getString(R.string.channel_desc);
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
        channel.setDescription(desc);

        NotificationManager nfManager = MainActivity.dis.getSystemService(NotificationManager.class);
        nfManager.createNotificationChannel(channel);
        notifChannelCreated = true;
    }

}
