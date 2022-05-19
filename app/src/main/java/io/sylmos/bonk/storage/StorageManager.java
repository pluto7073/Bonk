package io.sylmos.bonk.storage;

import android.content.Context;
import android.net.Uri;
import android.os.StrictMode;

import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StreamDownloadTask;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

import io.sylmos.bonk.MainActivity;
import io.sylmos.bonk.utils.TaskExtender;
import io.sylmos.bonk.utils.Utils;

public class StorageManager {

    public static final StorageManager INSTANCE = new StorageManager();

    private final FirebaseStorage storage;

    private final HashMap<String, String> downloadedFiles = new HashMap<>();

    private StorageManager() {
        storage = FirebaseStorage.getInstance();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);
    }

    public void saveFile(String folder, String filename, Uri file) {
        StorageReference folderRef = storage.getReference(folder);
        StorageReference fileRef = folderRef.child(filename);
        UploadTask uploadTask = fileRef.putFile(file);
        while(!uploadTask.isComplete());
    }

    public void saveFile(String filename, InputStream file) {
        StorageReference fileRef = storage.getReference(filename);
        UploadTask uploadTask = fileRef.putStream(file);
        while(!uploadTask.isComplete());
    }

    public TaskExtender<File> downloadFile(String file) {
        TaskExtender<File> extender = new TaskExtender<File>("FileDownloadTask") {
            @Override
            public void complete() {
                if (/*downloadedFiles.containsKey(file)*/0 == 1) {
                    this.setResult(new File(downloadedFiles.get(file)));
                    this.markComplete(true);
                } else {
                    File f = new File(MainActivity.dis.getFilesDir(), file);
                    f.getParentFile().mkdirs();
                    StorageReference fileRef = storage.getReference(file);
                    try {
                        f.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                        this.markComplete(false);
                    }
                    fileRef.getFile(f).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            downloadedFiles.put(file, f.getAbsolutePath());
                            f.deleteOnExit();
                            this.setResult(f);
                            this.markComplete(true);
                        } else {
                            this.markComplete(false);
                        }
                    });
                }
            }
        };
        Thread thread = new Thread(extender::complete);
        thread.setName(extender.getName() + "-" + thread.getId());
        thread.start();
        return extender;
    }

    public Object[] getFile(String filename) {
        StorageReference fileRef = storage.getReference(filename);
        File file = new File(MainActivity.dis.getFilesDir(), filename);
        try {
            file.getParentFile().mkdirs();
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        FileDownloadTask task = fileRef.getFile(file);
        return new Object[] {task, file};
    }

}
