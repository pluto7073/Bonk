package io.sylmos.bonk.ui.chats;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import io.sylmos.bonk.accounts.AccountManager;
import io.sylmos.bonk.databinding.ChatsFragmentBinding;

public class ChatsFragment extends Fragment implements ChatRecyclerAdapter.ItemClickListener {

    private ChatsFragmentBinding binding;
    private ChatRecyclerAdapter adapter;

    public ArrayList<String> chats;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = ChatsFragmentBinding.inflate(getLayoutInflater());
        AccountManager.INSTANCE.user.refreshFromDB().then((successful, result) -> {
            chats = AccountManager.INSTANCE.user.chats;
            RecyclerView recyclerView = binding.chats;
            recyclerView.setLayoutManager(new LinearLayoutManager(ChatsFragment.this.getContext()));
            adapter = new ChatRecyclerAdapter(this.getContext(), chats);
            adapter.setClickListener(this);
            recyclerView.setAdapter(adapter);
        });
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onItemClick(View view, int position) {
        Intent intent = new Intent(getContext(), ChatActivity.class);
        intent.putExtra(ChatActivity.CONVERSATION_ID, String.valueOf(chats.get(position)));
        startActivity(intent);
    }

}