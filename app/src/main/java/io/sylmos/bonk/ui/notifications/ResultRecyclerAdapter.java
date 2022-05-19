package io.sylmos.bonk.ui.notifications;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import io.sylmos.bonk.R;
import io.sylmos.bonk.UserViewerActivity;
import io.sylmos.bonk.database.DatabaseManager;
import io.sylmos.bonk.database.DatabaseUser;

public class ResultRecyclerAdapter extends RecyclerView.Adapter<ResultRecyclerAdapter.ViewHolder> {

    private List<String> data;
    private LayoutInflater inflater;
    private ItemClickListener clickListener;

    ResultRecyclerAdapter(Context context, List<String> data) {
        this.inflater = LayoutInflater.from(context);
        this.data = data;
    }

    @NonNull
    @Override
    public ResultRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.search_result_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ResultRecyclerAdapter.ViewHolder holder, int position) {
        String username = data.get(position);
        holder.usernameView.setText(username);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView usernameView;
        Button viewProfile;
        ImageView pfp;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameView = itemView.findViewById(R.id.result_username);
            viewProfile = itemView.findViewById(R.id.result_view_profile);
            pfp = itemView.findViewById(R.id.result_pfp);

            viewProfile.setOnClickListener(v -> {
                DatabaseManager.INSTANCE.getValue("users-by-tag/" + NotificationsFragment.baseTags.get(getAdapterPosition())).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        UserViewerActivity.currentViewedUser = new DatabaseUser((String) task.getResult().getValue(), true);
                        UserViewerActivity.currentViewedUser.refreshFromDB().then((successful, result) -> {
                            Intent intent = new Intent(itemView.getContext(), UserViewerActivity.class);
                            itemView.getContext().startActivity(intent);
                        });
                    }
                });
            });
        }

        @Override
        public void onClick(View v) {
            if (clickListener != null) clickListener.onItemClick(v, getAdapterPosition());
        }
    }

    String getItem(int id) {
        return data.get(id);
    }

    void setClickListener(ItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

}
