package io.sylmos.bonk.ui.dashboard;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.storage.FileDownloadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import io.sylmos.bonk.accounts.AccountManager;
import io.sylmos.bonk.databinding.FragmentDashboardBinding;
import io.sylmos.bonk.storage.StorageManager;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        DashboardViewModel dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        AccountManager.INSTANCE.user.refreshFromDB().then((successful, result) -> {
            ImageView pfp = binding.pfpView;
            TextView user = binding.usernameProfile;
            TextView abt = binding.aboutProfile;

            AccountManager.INSTANCE.user.avatar().then((s, r) -> {
                Bitmap pfpImg = BitmapFactory.decodeStream((InputStream) r);
                pfp.setImageBitmap(pfpImg);
            });

            user.setText(AccountManager.INSTANCE.user.username);
            abt.setText(AccountManager.INSTANCE.user.about);
        });
        binding.editprofile.setOnClickListener(view -> {
            Intent intent = new Intent(getContext(), EditProfileActivity.class);
            startActivity(intent);
        });
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}