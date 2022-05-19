package io.sylmos.bonk.utils;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.concurrent.Executor;

public class EmptyTask<T> extends TaskExtender<T> {

    public EmptyTask() {
        super("EmptyTask");
    }

    @Override
    public void complete() {
        markComplete(true);
    }
}
