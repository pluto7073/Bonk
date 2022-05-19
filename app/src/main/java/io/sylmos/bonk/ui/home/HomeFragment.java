package io.sylmos.bonk.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.tasks.Task;

import java.util.ArrayList;

import io.sylmos.bonk.database.Post;
import io.sylmos.bonk.database.PostManager;
import io.sylmos.bonk.databinding.FragmentHomeBinding;
import io.sylmos.bonk.utils.TaskExtender;

public class HomeFragment extends Fragment {

    public volatile static ArrayList<String> loadedPosts;
    public volatile static int totalPostsLoaded = 0;

    private volatile FragmentHomeBinding binding;
    private volatile PostsRecyclerAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);

        PostManager.INSTANCE.refresh().then((successful, result) ->  {
            loadedPosts = PostManager.INSTANCE.getLast10Posts();
            totalPostsLoaded = loadedPosts.size();
            RecyclerView recyclerView = binding.postsRecycler;
            recyclerView.setLayoutManager(new LinearLayoutManager(HomeFragment.this.getContext()));
            adapter = new PostsRecyclerAdapter(HomeFragment.this.getContext(), loadedPosts);
            recyclerView.setAdapter(adapter);
        });

        binding.homeRefresh.setOnRefreshListener(() -> {
            Log.i("Bonk.HomeActivity.HomeFragment", "refresh action started on homeRefresh");

            preformRefresh();
        });

        return binding.getRoot();
    }

    public void preformRefresh() {
        PostManager.INSTANCE.refresh().then((successful, result) -> {
            binding.homeRefresh.setRefreshing(false);
            loadedPosts = PostManager.INSTANCE.getLast10Posts();
            int tempSize = loadedPosts.size();
            while (tempSize < totalPostsLoaded) {
                loadedPosts.addAll(PostManager.INSTANCE.get10Posts(tempSize));
                tempSize = loadedPosts.size();
            }
            RecyclerView recyclerView = binding.postsRecycler;
            recyclerView.setLayoutManager(new LinearLayoutManager(HomeFragment.this.getContext()));
            adapter = new PostsRecyclerAdapter(HomeFragment.this.getContext(), loadedPosts);
            recyclerView.setAdapter(adapter);
        });
        Runnable runnable = () -> {
            try {
                Thread.sleep(5100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (binding.homeRefresh.isRefreshing()) binding.homeRefresh.setRefreshing(false);
        };

        (new Thread(runnable)).start();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}