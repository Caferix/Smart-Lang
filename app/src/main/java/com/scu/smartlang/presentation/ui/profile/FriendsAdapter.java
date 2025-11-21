package com.scu.smartlang.presentation.ui.profile;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.scu.smartlang.R;
import java.util.List;

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.FriendViewHolder> {

    // Basit model (Şimdilik dummy data için)
    public static class FriendModel {
        String name;
        int level;
        public FriendModel(String name, int level) {
            this.name = name;
            this.level = level;
        }
    }

    private List<FriendModel> friendList;

    public FriendsAdapter(List<FriendModel> friendList) {
        this.friendList = friendList;
    }

    @NonNull
    @Override
    public FriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_friend, parent, false);
        return new FriendViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendViewHolder holder, int position) {
        FriendModel friend = friendList.get(position);
        holder.tvName.setText(friend.name);
        holder.tvLevel.setText("Level: " + friend.level);
    }

    @Override
    public int getItemCount() {
        return friendList != null ? friendList.size() : 0;
    }

    static class FriendViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvLevel;

        public FriendViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_friend_name);
            tvLevel = itemView.findViewById(R.id.tv_friend_level);
        }
    }
}