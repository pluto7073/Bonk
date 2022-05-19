package io.sylmos.bonk.utils;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

public abstract class TaskExtender<T> extends Task<T> {

    public interface OnCompleteTask {
        void onComplete(boolean successful, Object result);
    }

    private volatile T result;
    private volatile boolean complete = false;
    private volatile boolean successful;
    public volatile List<OnCompleteTask> completeTasks = new ArrayList<>();
    private final String name;

    public TaskExtender(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setResult(T result) {
        this.result = result;
    }

    public void markComplete(boolean successful) {
        complete = true;
        this.successful = successful;
        for (OnCompleteTask t : completeTasks) {
            t.onComplete(this.successful, this.getResult());
        }
    }

    public Task<T> then(OnCompleteTask onCompleteTask) {
        completeTasks.add(onCompleteTask);
        return this;
    }

    public abstract void complete();

    @NonNull
    @Override
    public Task<T> addOnFailureListener(@NonNull OnFailureListener onFailureListener) {
        return null;
    }

    @NonNull
    @Override
    public Task<T> addOnFailureListener(@NonNull Activity activity, @NonNull OnFailureListener onFailureListener) {
        return null;
    }

    @NonNull
    @Override
    public Task<T> addOnFailureListener(@NonNull Executor executor, @NonNull OnFailureListener onFailureListener) {
        return null;
    }

    @NonNull
    @Override
    public Task<T> addOnSuccessListener(@NonNull OnSuccessListener<? super T> onSuccessListener) {
        return null;
    }

    @NonNull
    @Override
    public Task<T> addOnSuccessListener(@NonNull Activity activity, @NonNull OnSuccessListener<? super T> onSuccessListener) {
        return null;
    }

    @NonNull
    @Override
    public Task<T> addOnSuccessListener(@NonNull Executor executor, @NonNull OnSuccessListener<? super T> onSuccessListener) {
        return null;
    }

    @Nullable
    @Override
    public Exception getException() {
        return null;
    }

    @Override
    public T getResult() {
        return result;
    }

    @Override
    public <X extends Throwable> T getResult(@NonNull Class<X> aClass) throws X {
        return null;
    }

    @Override
    public boolean isCanceled() {
        return false;
    }

    @Override
    public boolean isComplete() {
        return complete;
    }

    @Override
    public boolean isSuccessful() {
        return successful;
    }

}
