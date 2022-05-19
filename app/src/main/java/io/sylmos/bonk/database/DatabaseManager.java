package io.sylmos.bonk.database;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.atomic.AtomicReference;

import io.sylmos.bonk.utils.TaskExtender;

public class DatabaseManager {

    public static final DatabaseManager INSTANCE = new DatabaseManager();

    private final FirebaseDatabase database;

    public static volatile boolean finished = false;

    private DatabaseManager() {
        database = FirebaseDatabase.getInstance();
    }

    public void setValue(String path, Object value) {
        DatabaseReference ref = database.getReference();
        ref.child(path).setValue(value);
    }

    public Task<DataSnapshot> getValue(String path) {
        return database.getReference(path).get();
    }

    public TaskExtender<Object> getValueAsync(String path) {
        TaskExtender<Object> extender = new TaskExtender<Object>("GetValueTask") {
            @Override
            public void complete() {
                database.getReference(path).get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        this.setResult(task.getResult().getValue());
                        this.markComplete(true);
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

    public void delete(String path) {
        database.getReference(path).removeValue();
    }

    public DatabaseReference getReference(String path) {
        return database.getReference(path);
    }

}
