package io.sylmos.bonk.utils;

import androidx.annotation.NonNull;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public interface ValueChangedListener extends ValueEventListener {

    @Override
    default void onCancelled(@NonNull DatabaseError error) {
        error.toException().printStackTrace();
    }

}
