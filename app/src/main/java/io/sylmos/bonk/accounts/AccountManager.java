package io.sylmos.bonk.accounts;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import io.sylmos.bonk.LoginActivity;
import io.sylmos.bonk.database.ClientUser;
import io.sylmos.bonk.database.DatabaseManager;
import io.sylmos.bonk.database.DatabaseUser;

public class AccountManager {

    public static final AccountManager INSTANCE = new AccountManager();
    private final FirebaseAuth auth;
    public boolean loggedIn = false;
    public FirebaseUser fbUser;
    public ClientUser user;
    private String password;

    private AccountManager() {
        auth = FirebaseAuth.getInstance();
    }

    public Task<AuthResult> loadAccountInfo(Activity context) {
        StringBuilder builder = new StringBuilder();
        try {
            FileInputStream ais = context.openFileInput("accounts.json");
            InputStreamReader reader = new InputStreamReader(ais);
            BufferedReader bReader = new BufferedReader(reader);
            String line = bReader.readLine();
            while (line != null) {
                builder.append(line).append('\n');
                line = bReader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
            loggedIn = false;
            return null;
        }
        String content = builder.toString();
        JsonArray object;
        try {
            object = JsonParser.parseString(content).getAsJsonArray();
        } catch (IllegalStateException ignored) {
            return null;
        }
        JsonObject userInfo = object.get(0).getAsJsonObject();
        String username = userInfo.get("username").getAsString();
        String password = userInfo.get("password").getAsString();
        return signInWithUsernameAndPassword(username, password, context);
    }

    public Task<AuthResult> createAccountWithUsernameAndPassword(String username, String password, Context context) {
        Task<AuthResult> task = auth.createUserWithEmailAndPassword(username, password);
        task.addOnCompleteListener((Activity) context, task1 -> {
            if (task1.isSuccessful()) {
                fbUser = auth.getCurrentUser();
                loggedIn = true;
                assert fbUser != null;
                user = new ClientUser(fbUser.getUid(), false);
                user.email = username;
                DatabaseManager.INSTANCE.getValue("tag").addOnCompleteListener(task2 -> {
                    if (task2.isSuccessful()) {
                        Object currentTag = task2.getResult().getValue();
                        user.tag = currentTag.toString();
                        currentTag = incrementTag(currentTag.toString());
                        DatabaseManager.INSTANCE.setValue("tag", currentTag);
                    }
                });

                JsonObject object = new JsonObject();
                object.add("username", new JsonPrimitive(username));
                object.add("password", new JsonPrimitive(password));
                this.password = password;
                JsonArray array = new JsonArray();
                array.add(object);
                try (FileWriter writer = new FileWriter(new File(context.getFilesDir(), "accounts.json"))) {
                    writer.write(array.toString());
                    writer.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                fbUser = null;
                loggedIn = false;
            }
        });
        return task;
    }

    public Task<AuthResult> signInWithUsernameAndPassword(String username, String password, Context context) {
        Task<AuthResult> task = auth.signInWithEmailAndPassword(username, password);
        task.addOnCompleteListener((Activity) context, task1 -> {
            if (task1.isSuccessful()) {
                fbUser = auth.getCurrentUser();
                loggedIn = true;
                assert fbUser != null;
                user = new ClientUser(fbUser.getUid(), true);
                user.refreshFromDB();
                JsonObject object = new JsonObject();
                object.add("username", new JsonPrimitive(username));
                object.add("password", new JsonPrimitive(password));
                this.password = password;
                JsonArray array = new JsonArray();
                array.add(object);
                try (FileWriter writer = new FileWriter(new File(context.getFilesDir(), "accounts.json"))) {
                    writer.write(array.toString());
                    writer.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                fbUser = null;
                loggedIn = false;
            }
        });
        return task;
    }

    public void logOut(Activity currentActivity) {
        auth.signOut();
        File file = new File(currentActivity.getFilesDir(), "accounts.json");
        file.delete();
        Intent intent = new Intent(currentActivity, LoginActivity.class);
        currentActivity.startActivity(intent);
        currentActivity.finishAffinity();
    }

    private String incrementTag(String tag) {
        int tagInt = Integer.parseInt(tag);
        tagInt++;
        if (tagInt >= 10000) tagInt = 1;
        if (tagInt < 10) tag = "000" + tagInt;
        else if (tagInt < 100) tag = "00" + tagInt;
        else if (tagInt < 1000) tag = "0" + tagInt;
        else tag = "" + tagInt;
        return tag;
    }

    public String getPassword() {
        return password;
    }

}
