package io.sylmos.bonk.database;

import android.content.Context;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.storage.FileDownloadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import io.sylmos.bonk.accounts.AccountManager;
import io.sylmos.bonk.storage.StorageManager;
import io.sylmos.bonk.utils.EmptyTask;
import io.sylmos.bonk.utils.TaskExtender;

public class DatabaseUser extends Base {

    public static HashMap<String, HashMap<String, Object>> usersCache = new HashMap<>();
    public static volatile HashMap<String, File> avatarsCache = new HashMap<>();

    public String id;
    public String username;
    public String pfp;
    public String email;
    public String tag;
    public String about;
    public ArrayList<String> chats;
    public ArrayList<String> posts;

    protected long lastUsernameChange = 0L;
    protected ArrayList<String> following;
    protected ArrayList<String> followers;

    private String oldUsername;
    private UserPostManager postManager;

    public boolean existing;

    public DatabaseUser(String id, boolean existing) {
        super(DatabaseManager.INSTANCE, "users/" + id);
        this.id = id;
        this.existing = existing;
        this.chats = new ArrayList<>();
        this.posts = new ArrayList<>();
        this.following = new ArrayList<>();
        this.followers = new ArrayList<>();
        if (usersCache.containsKey(id)) {
            loadFromHashMap(usersCache.get(id));
        } else refreshFromDB();
    }

    public int followersCount() {
        return followers.size();
    }

    public int followingCount() {
        return following.size();
    }

    public UserPostManager posts() {
        if (postManager == null) {
            postManager = new UserPostManager(this);
        }
        return postManager;
    }

    public InputStream fetchAvatar() {
        TaskExtender<InputStream> avatarTask = avatar();
        while (true) {
            if (avatarTask.isComplete()) break;
        }
        return avatarTask.getResult();
    }

    public TaskExtender<InputStream> avatar() {
        TaskExtender<InputStream> taskExtender = new TaskExtender<InputStream>("GetAvatarTask") {
            @Override
            public void complete() {
                StorageManager.INSTANCE.downloadFile(pfp).then((successful, result) -> {
                    if (successful) {
                        try {
                            this.setResult(new FileInputStream((File) result));
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        this.markComplete(true);
                    } else {
                        this.markComplete(false);
                    }
                });
            }
        };
        Thread thread = new Thread(taskExtender::complete);
        thread.setName(taskExtender.getName());
        thread.start();
        return taskExtender;
    }

    public HashMap<String, Object> toHashMap() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("username", username);
        map.put("pfp", pfp);
        map.put("email", email);
        map.put("tag", tag);
        map.put("about", about);
        map.put("chats", chats);
        map.put("lastUsernameChange", lastUsernameChange);
        map.put("following", following);
        map.put("followers", followers);
        map.put("posts", posts);
        return map;
    }

    public void loadFromHashMap(HashMap<String, Object> map) {
        id = (String) map.get("id");
        username = (String) map.get("username");
        pfp = (String) map.get("pfp");
        email = (String) map.get("email");
        tag = (String) map.get("tag");
        about = (String) map.get("about");
        chats = (ArrayList<String>) map.get("chats");
        if (map.containsKey("lastUsernameChange")) lastUsernameChange = (long) map.get("lastUsernameChange");
        if (map.containsKey("followers")) followers = (ArrayList<String>) map.get("followers");
        if (map.containsKey("following")) following = (ArrayList<String>) map.get("following");
        posts = (ArrayList<String>) map.get("posts");
        oldUsername = username;

    }

    public void saveInDB() {
        manager.setValue("users/" + id, toHashMap());
        manager.delete("users-by-tag/" + oldUsername + ":" + tag);
        manager.setValue("users-by-tag/" + username + ":" + tag, id);
        oldUsername = username;
        existing = true;
    }

    public TaskExtender<Void> refreshFromDB() {
        if (!existing) return new EmptyTask<>();
        TaskExtender<Void> taskExtender = new TaskExtender<Void>("UserRefreshTask") {
            @Override
            public void complete() {
                manager.getValue("users/" + id).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        HashMap<String, Object> map = (HashMap<String, Object>) task.getResult().getValue();
                        loadFromHashMap(map);
                        usersCache.put(id, map);
                        this.markComplete(true);
                    } else {
                        this.markComplete(false);
                    }
                });
            }
        };
        Thread thread = new Thread(taskExtender::complete);
        thread.setName(taskExtender.getName());
        thread.start();
        return taskExtender;
    }

    public void follow() {
        if (AccountManager.INSTANCE.user.id.equals(this.id)) return;
        followers.add(AccountManager.INSTANCE.user.id);
        saveInDB();
        AccountManager.INSTANCE.user.addSubscription(this.id);
    }

    public void unfollow() {
        if (AccountManager.INSTANCE.user.id.equals(this.id)) return;
        followers.remove(AccountManager.INSTANCE.user.id);
        saveInDB();
        AccountManager.INSTANCE.user.removeSubscription(this.id);
    }

}
