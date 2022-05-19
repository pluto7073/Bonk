package io.sylmos.bonk.database;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import io.sylmos.bonk.accounts.AccountManager;
import io.sylmos.bonk.storage.StorageManager;
import io.sylmos.bonk.utils.EmptyTask;
import io.sylmos.bonk.utils.TaskExtender;

public class PostManager extends ArrayList<Long> {

    public static final PostManager INSTANCE = new PostManager();

    PostManager() {}

    public TaskExtender<Void> refresh() {
        TaskExtender<Void> taskE = new TaskExtender<Void>("RefreshTask") {
            @Override
            public void complete() {
                DatabaseManager.INSTANCE.getValue("posts-order").addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        ArrayList<Long> idsList = (ArrayList<Long>) task.getResult().getValue();
                        PostManager.this.clear();
                        PostManager.this.addAll(idsList);
                        this.markComplete(true);
                    } else {
                        this.markComplete(false);
                    }
                });
            }
        };
        Thread t = new Thread(taskE::complete);
        t.setName(taskE.getName());
        t.start();
        return taskE;
    }

    private volatile HashMap<String, Boolean> activeTasks = new HashMap<>();

    public Post fetch(long id) {
        final String ATID = String.valueOf(System.currentTimeMillis());
        activeTasks.put(ATID, false);
        TaskExtender<Post> extender = new TaskExtender<Post>("GetPostTask") {
            @Override
            public void complete() {
                PostManager.this.refresh().then(((successful, result) -> {
                    if (successful) {
                        DatabaseManager.INSTANCE.getValue("posts/" + id).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                HashMap<String, Object> map = (HashMap<String, Object>) task.getResult().getValue();
                                this.setResult(new Post(map));
                                this.markComplete(true);
                            } else {
                                this.markComplete(false);
                            }
                            activeTasks.put(ATID, true);
                        });
                    }
                }));
            }
        };
        Thread t = new Thread(extender::complete);
        t.setName(extender.getName());
        t.start();
        while (!activeTasks.get(ATID));
        return extender.getResult();
    }

    public TaskExtender<Post> getPost(long id) {
        TaskExtender<Post> taskExtender = new TaskExtender<Post>("GetPostTask") {
            @Override
            public void complete() {
                PostManager.this.refresh().then((successful, result) -> {
                    if (successful) {
                        DatabaseManager.INSTANCE.getValue("posts/" + id).addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                HashMap<String, Object> map = (HashMap<String, Object>) task1.getResult().getValue();
                                this.setResult(new Post(map));
                                this.markComplete(true);
                            } else {
                                this.markComplete(false);
                            }
                        });
                    }
                });
            }
        };
        Thread t = new Thread(taskExtender::complete);
        t.setName(taskExtender.getName() + "-" + t.getId());
        t.start();
        return taskExtender;
    }

    public Task<DataSnapshot> getPost(int index) {
        return DatabaseManager.INSTANCE.getValue("posts/" + this.get(index));
    }

    public TaskExtender<Post> post(Post post, ArrayList<File> files) {
        TaskExtender<Post> extender = new TaskExtender<Post>("CreatePostTask") {
            @Override
            public void complete() {
                refresh().then((successful, result) -> {
                    if (successful) {
                        long id = post.id;
                        ArrayList<String> fs = new ArrayList<>();
                        for (File f : files) {
                            String newFileName = "posts/" + id + "-" + f.getName();
                            try {
                                StorageManager.INSTANCE.saveFile(newFileName, new FileInputStream(f));
                                fs.add(newFileName);
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                        }
                        post.files = fs;
                        HashMap<String, Object> postData = post.toHashMap();
                        DatabaseManager.INSTANCE.setValue("posts/" + id, postData);
                        PostManager.this.add(id);
                        PostManager.this.saveInDB();
                        setResult(post);
                        AccountManager.INSTANCE.user.posts().post(post, files);
                        markComplete(true);
                    } else {
                        markComplete(false);
                    }
                });
            }
        };
        Thread thread = new Thread(extender::complete);
        thread.setName(extender.getName());
        thread.start();
        return extender;
    }

    public void saveInDB() {
        DatabaseManager.INSTANCE.setValue("posts-order", this);
    }

    public ArrayList<String> getLast10Posts() {
        return get10Posts(0);
    }

    public ArrayList<String> get10Posts(int offset) {
        ArrayList<String> list = new ArrayList<>();
        int start = size() - offset - 1;
        for (int i = 0; i < 10; i++) {
            if (start - i < 0 || start - 1 >= size()) break;
            list.add(String.valueOf(this.get(start - i)));
        }
        return list;
    }

}
