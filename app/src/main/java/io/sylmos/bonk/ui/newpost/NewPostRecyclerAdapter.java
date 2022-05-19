package io.sylmos.bonk.ui.newpost;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

import io.sylmos.bonk.R;

public class NewPostRecyclerAdapter extends RecyclerView.Adapter<NewPostRecyclerAdapter.ViewHolder> {

    private final List<File> data;
    private final LayoutInflater inflater;

    public NewPostRecyclerAdapter(Context context, List<File> data) {
        this.data = data;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public NewPostRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.new_post_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewPostRecyclerAdapter.ViewHolder holder, int position) {
        holder.imageName.setText(data.get(position).getName());
        try {
            FileInputStream inputStream = new FileInputStream(data.get(position));
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            holder.image.setImageBitmap(bitmap);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView imageName;
        ImageView image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageName = itemView.findViewById(R.id.new_post_row_image_name);
            image = itemView.findViewById(R.id.new_post_row_image);
        }

    }

}
