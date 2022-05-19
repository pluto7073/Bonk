package io.sylmos.bonk.ui.notifications;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import io.sylmos.bonk.database.DatabaseManager;
import io.sylmos.bonk.databinding.FragmentNotificationsBinding;
import io.sylmos.bonk.utils.Utils;

public class NotificationsFragment extends Fragment implements ResultRecyclerAdapter.ItemClickListener {

    private FragmentNotificationsBinding binding;
    private ResultRecyclerAdapter adapter;
    // EX: itsmossyouhoes:0001
    public List<String> tags;
    public static List<String> baseTags;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        NotificationsViewModel notificationsViewModel =
                new ViewModelProvider(this).get(NotificationsViewModel.class);

        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        DatabaseManager.INSTANCE.getValue("users-by-tag").addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                HashMap<String, Object> tagsMap = (HashMap<String, Object>) task.getResult().getValue();
                tags = new ArrayList<>(tagsMap.keySet());
                baseTags = tags;
                RecyclerView recyclerView = binding.searchResults;
                recyclerView.setLayoutManager(new LinearLayoutManager(NotificationsFragment.this.getContext()));
                adapter = new ResultRecyclerAdapter(NotificationsFragment.this.getContext(), tags);
                adapter.setClickListener(NotificationsFragment.this);
                recyclerView.setAdapter(adapter);
                binding.searchBar.addTextChangedListener(new TextWatcher() {
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                    public void afterTextChanged(Editable s) {}

                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        List<String> filteredList = Utils.listToLowerCase(new ArrayList<>(tags), Locale.ROOT);
                        List<String> stringsToRemove = new ArrayList<>();
                        for (String user : filteredList) {
                            if (!user.contains(s.toString().toLowerCase(Locale.ROOT))) stringsToRemove.add(user);
                        }
                        for (String r : stringsToRemove) {
                            filteredList.remove(r);
                        }

                        recyclerView.setLayoutManager(new LinearLayoutManager(NotificationsFragment.this.getContext()));
                        adapter = new ResultRecyclerAdapter(NotificationsFragment.this.getContext(), filteredList);
                        adapter.setClickListener(NotificationsFragment.this);
                        recyclerView.setAdapter(adapter);
                    }
                });
            }
        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onItemClick(View view, int position) {

    }
}