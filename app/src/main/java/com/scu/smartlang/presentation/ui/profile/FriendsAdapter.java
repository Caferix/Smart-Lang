package com.scu.smartlang.presentation.ui.profile;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.scu.smartlang.R;
import com.scu.smartlang.domain.model.Friend;

import java.util.List;

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.FriendViewHolder> {

    private List<Friend> friends;
    private final OnFriendClickListener listener;

    // Tıklama olaylarını dinlemek için arayüz
    public interface OnFriendClickListener {
        void onFriendClick(Friend friend);
    }

    public FriendsAdapter(List<Friend> friends, OnFriendClickListener listener) {
        this.friends = friends;
        this.listener = listener;
    }

    public void updateList(List<Friend> newList) {
        this.friends = newList;
        notifyDataSetChanged();
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
        Friend friend = friends.get(position);
        holder.bind(friend, listener);
    }

    @Override
    public int getItemCount() {
        return friends != null ? friends.size() : 0;
    }

    static class FriendViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvLevel;

        FriendViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_friend_name);
            tvLevel = itemView.findViewById(R.id.tv_friend_level);
        }

        // Veriyi bağla ve tıklama dinleyicisini ayarla
        public void bind(final Friend friend, final OnFriendClickListener listener) {
            tvName.setText(friend.getUserName());
            tvLevel.setText("Level " + friend.getLevel());
            itemView.setOnClickListener(v -> listener.onFriendClick(friend));
        }
    }
}
