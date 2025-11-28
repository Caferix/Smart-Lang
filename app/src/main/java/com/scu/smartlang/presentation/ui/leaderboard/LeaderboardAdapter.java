package com.scu.smartlang.presentation.ui.leaderboard;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.scu.smartlang.R;
import com.scu.smartlang.domain.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardAdapter.LeaderboardViewHolder> {

    private List<User> userList = new ArrayList<>();

    @NonNull
    @Override
    public LeaderboardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_leaderboard, parent, false);
        return new LeaderboardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LeaderboardViewHolder holder, int position) {
        User user = userList.get(position);
        holder.bind(user, position + 1);
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public void setUsers(List<User> users) {
        this.userList = users;
        notifyDataSetChanged();
    }

    static class LeaderboardViewHolder extends RecyclerView.ViewHolder {
        TextView tvRank, tvName, tvLevel, tvXp;
        ImageView ivProfileImage;

        public LeaderboardViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRank = itemView.findViewById(R.id.tv_rank);
            tvName = itemView.findViewById(R.id.tv_name);
            tvLevel = itemView.findViewById(R.id.tv_level);
            tvXp = itemView.findViewById(R.id.tv_xp);
            ivProfileImage = itemView.findViewById(R.id.iv_profile_image);
        }

        public void bind(User user, int rank) {
            tvRank.setText(String.valueOf(rank));
            tvName.setText(user.getUserName());
            tvLevel.setText(String.format(Locale.getDefault(), "Level %d", user.getLevel()));
            tvXp.setText(String.format(Locale.getDefault(), "%d XP", user.getXp()));

            Glide.with(itemView.getContext())
                    .load(user.getProfileImageUrl())
                    .placeholder(R.drawable.ic_person_24dp)
                    .circleCrop()
                    .into(ivProfileImage);
        }
    }
}
