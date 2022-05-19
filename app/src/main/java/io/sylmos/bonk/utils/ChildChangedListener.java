package io.sylmos.bonk.utils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

public interface ChildChangedListener extends ChildEventListener {
    @Override
    default void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}

    @Override
    default void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}

    @Override
    default void onChildRemoved(@NonNull DataSnapshot snapshot) {}

    @Override
    default void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}

    @Override
    default void onCancelled(@NonNull DatabaseError error) {
        error.toException().printStackTrace();
    }

}
