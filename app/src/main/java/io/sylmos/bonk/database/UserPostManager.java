package io.sylmos.bonk.database;

import java.io.File;
import java.util.ArrayList;

import io.sylmos.bonk.utils.TaskExtender;

public class UserPostManager extends PostManager {

    private final String uid;

    public UserPostManager(String uid) {
        this.uid = uid;
    }

    @Override
    public TaskExtender<Void> refresh() {
        TaskExtender<Void> extender = new TaskExtender<Void>("RefreshUserPostsTask") {
            @Override
            public void complete() {
                DatabaseUser user = new DatabaseUser(uid, true);
                DatabaseManager.INSTANCE.getValue(user.dbPath + "/posts").addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        ArrayList<Long> list = (ArrayList<Long>) task.getResult().getValue();
                        UserPostManager.this.clear();
                        UserPostManager.this.addAll(list);
                    } else {
                        this.markComplete(false);
                    }
                });
            }
        };
        Thread thread = new Thread(extender::complete);
        thread.setName(extender.getName() + "-" + thread.getId());
        thread.start();
        return extender;
    }

    @Override
    public TaskExtender<Post> post(Post post, ArrayList<File> files) {
        TaskExtender<Post> extender = new TaskExtender<Post>("InstantFailTask") {
            @Override
            public void complete() {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                this.markComplete(false);
            }
        };
        Thread thread = new Thread(extender::complete);
        thread.setName(extender.getName() + "-" + thread.getId());
        thread.start();
        return extender;
    }

}
