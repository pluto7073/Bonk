package io.sylmos.bonk.ui.postviewer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FileDownloadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

import io.sylmos.bonk.R;
import io.sylmos.bonk.storage.StorageManager;

public class PostViewerRecyclerAdapter extends RecyclerView.Adapter<PostViewerRecyclerAdapter.ViewHolder> {

    private final List<String> data;
    private final LayoutInflater inflater;

    public PostViewerRecyclerAdapter(Context context, List<String> data) {
        this.inflater = LayoutInflater.from(context);
        this.data = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.post_files_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Object[] downloadInfo = StorageManager.INSTANCE.getFile(data.get(position));
        ((FileDownloadTask) downloadInfo[0]).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                try {
                    InputStream stream = new FileInputStream((File) downloadInfo[1]);
                    Bitmap bitmap = BitmapFactory.decodeStream(stream);
                    holder.image.setImageBitmap(bitmap);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.post_file);
            image.setOnLongClickListener(v -> {
                //TODO: Menu
                return true;
            });
        }

    }

}
