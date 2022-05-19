package io.sylmos.bonk.database;

import java.io.File;
import java.util.ArrayList;

import io.sylmos.bonk.accounts.AccountManager;
import io.sylmos.bonk.utils.TaskExtender;

public class UserPostManager extends PostManager {

    private final String uid;
    private final DatabaseUser user;

    public UserPostManager(DatabaseUser user) {
        this.uid = user.id;
        this.user = user;
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
                        if (list == null) {
                            this.markComplete(false);
                            return;
                        }
                        UserPostManager.this.clear();
                        UserPostManager.this.addAll(list);
                        this.markComplete(true);
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
        TaskExtender<Post> extender = new TaskExtender<Post>("PostToUserPostsTask") {
            @Override
            public void complete() {
                UserPostManager.this.add(post.id);
                AccountManager.INSTANCE.user.posts.add(String.valueOf(post.id));
                AccountManager.INSTANCE.user.saveInDB();
            }
        };
        Thread thread = new Thread(extender::complete);
        thread.setName(extender.getName() + "-" + thread.getId());
        thread.start();
        return extender;
    }

}
