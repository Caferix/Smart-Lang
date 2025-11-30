package com.scu.smartlang.presentation.ui.search;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.scu.smartlang.R;
import com.scu.smartlang.domain.model.User;

import java.util.List;

public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.ViewHolder> {

    private List<User> users;
    private final OnUserClickListener listener;

    public interface OnUserClickListener {
        void onUserClick(String userId);
    }

    public SearchResultAdapter(List<User> users, OnUserClickListener listener) {
        this.users = users;
        this.listener = listener;
    }

    public void updateList(List<User> newList) {
        this.users = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_2, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = users.get(position);
        holder.tvName.setText(user.getUserName());
        holder.tvLevel.setText("Level " + user.getLevel());
        holder.itemView.setOnClickListener(v -> listener.onUserClick(user.getUid()));
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvLevel;

        ViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(android.R.id.text1);
            tvLevel = itemView.findViewById(android.R.id.text2);
        }
    }
}
