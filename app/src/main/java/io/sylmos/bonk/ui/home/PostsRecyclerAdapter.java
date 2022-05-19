package io.sylmos.bonk.ui.home;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.InputStream;
import java.util.List;

import io.sylmos.bonk.PostViewerActivity;
import io.sylmos.bonk.R;
import io.sylmos.bonk.database.DatabaseUser;
import io.sylmos.bonk.database.Post;
import io.sylmos.bonk.database.PostManager;
import io.sylmos.bonk.storage.StorageManager;
import io.sylmos.bonk.utils.Utils;

public class PostsRecyclerAdapter extends RecyclerView.Adapter<PostsRecyclerAdapter.ViewHolder> {

    private final List<String> data;
    private final LayoutInflater inflater;
    private ItemClickListener clickListener;

    public PostsRecyclerAdapter(Context context, List<String> data) {
        this.inflater = LayoutInflater.from(context);
        this.data = data;
    }

    @NonNull
    @Override
    public PostsRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.post_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostsRecyclerAdapter.ViewHolder holder, int position) {
        PostManager.INSTANCE.getPost(Long.parseLong(data.get(position))).then((successful, result) -> {
            Post post = (Post) result;

            //Preview
            String postPreview = Utils.limitTextLength(post.text, 25);
            holder.postRowText.setText(postPreview);

            //User stuff
            DatabaseUser user = new DatabaseUser(post.author, true);
            user.refreshFromDB().then((successful1, result1) -> {
                String displayTag = user.username + ":" + user.tag;
                holder.postRowUsername.setText(displayTag);
                user.avatar().then((successful2, result2) -> {
                    Bitmap image = BitmapFactory.decodeStream((InputStream) result2);
                    holder.postRowPfp.setImageBitmap(image);

                });
            });
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView postRowPfp;
        TextView postRowUsername, postRowText;
        Button expandPost;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            postRowPfp = itemView.findViewById(R.id.post_row_pfp);
            postRowUsername = itemView.findViewById(R.id.post_row_username);
            postRowText = itemView.findViewById(R.id.post_row_text);
            expandPost = itemView.findViewById(R.id.post_row_expand_btn);
            expandPost.setOnClickListener(v -> PostManager.INSTANCE.getPost(Long.parseLong(data.get(getAdapterPosition()))).then((successful, result) -> {
                if (successful) {
                    PostViewerActivity.currentPost = (Post) result;
                    Intent intent = new Intent(itemView.getContext(), PostViewerActivity.class);
                    itemView.getContext().startActivity(intent);
                }
            }));
        }

        @Override
        public void onClick(View v) {
            if (clickListener != null) clickListener.onItemClick(v, getAdapterPosition());
        }

    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

}
