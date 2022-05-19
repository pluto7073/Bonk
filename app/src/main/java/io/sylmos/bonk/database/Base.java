package io.sylmos.bonk.database;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.util.HashMap;

import io.sylmos.bonk.utils.TaskExtender;

public abstract class Base {

    protected DatabaseManager manager;
    protected String dbPath;

    public Base(DatabaseManager manager, String dbPath) {
        this.manager = manager;
        this.dbPath = dbPath;
    }

    public abstract HashMap<String, Object> toHashMap();
    public abstract void loadFromHashMap(HashMap<String, Object> map);

    @NonNull
    public String toString() {
        JsonObject object = new JsonObject();
        toHashMap().forEach((s, o) -> {
            if (o instanceof JsonObject || o instanceof JsonArray) {
                object.add(s, (JsonElement) o);
            } else if (o instanceof Boolean) object.add(s, new JsonPrimitive((boolean) o));
            else if (o instanceof Number) object.add(s, new JsonPrimitive((Number) o));
            else if (o instanceof String) object.add(s, new JsonPrimitive((String) o));
        });
        return object.toString();
    }

    public TaskExtender<Void> refreshFromDB() {
        TaskExtender<Void> taskExtender = new TaskExtender<Void>("RefreshDataTask") {
            @Override
            public void complete() {
                manager.getValue(dbPath).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        HashMap<String, Object> map = (HashMap<String, Object>) task.getResult().getValue();
                        loadFromHashMap(map);
                        this.markComplete(true);
                    } else {
                        this.markComplete(false);
                    }
                });
            }
        };
        Thread t = new Thread(taskExtender::complete);
        t.setName(taskExtender.getName());
        t.start();
        return taskExtender;
    }

    public void saveInDB() {
        manager.setValue(dbPath, toHashMap());
    }

}
