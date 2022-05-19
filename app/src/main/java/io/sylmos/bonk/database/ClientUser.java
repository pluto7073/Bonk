package io.sylmos.bonk.database;

import android.content.Context;
import android.widget.Toast;

import io.sylmos.bonk.accounts.AccountManager;

public class ClientUser extends DatabaseUser {

    public ClientUser(String id, boolean existing) {
        super(id, existing);
    }

    public boolean changeUsername(String username, String password, Context context) {
        if (!AccountManager.INSTANCE.getPassword().equals(password)) return false;
        if (!AccountManager.INSTANCE.user.id.equals(this.id)) return false;
        if (System.currentTimeMillis() < this.lastUsernameChange + 1209600000L) {
            Toast.makeText(context, "You have changed your username too recently", Toast.LENGTH_SHORT).show();
            return true;
        }
        this.username = username;
        this.lastUsernameChange = System.currentTimeMillis();
        return true;
    }

    public void addSubscription(String id) {
        this.following.add(id);
        this.saveInDB();
    }

    public void removeSubscription(String id) {
        this.following.remove(id);
        this.saveInDB();
    }

    public boolean isSubscribedTo(String id) {
        return this.following.contains(id);
    }

}
