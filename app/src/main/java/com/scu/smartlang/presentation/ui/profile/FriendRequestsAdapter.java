package com.scu.smartlang.presentation.ui.profile;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.scu.smartlang.R;
import com.scu.smartlang.domain.model.FriendRequest;

import java.util.List;

public class FriendRequestsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<FriendRequest> requests;
    private final OnRequestActionListener listener;

    public interface OnRequestActionListener {
        void onAccept(String requestId);
        void onViewProfile(String userId);
    }

    public FriendRequestsAdapter(List<FriendRequest> requests, OnRequestActionListener listener) {
        this.requests = requests;
        this.listener = listener;
    }

    public void updateList(List<FriendRequest> newList) {
        this.requests = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_friend_request, parent, false);
        return new RecyclerView.ViewHolder(view) {};
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        FriendRequest request = requests.get(position);

        TextView tvFromUser = holder.itemView.findViewById(R.id.tv_request_from);
        Button btnAccept = holder.itemView.findViewById(R.id.btn_accept_request);

        String display = request.getSenderName() != null
                ? request.getSenderName()
                : request.getFromUid();
        tvFromUser.setText(display);

        btnAccept.setOnClickListener(v -> listener.onAccept(request.getId()));
        holder.itemView.setOnClickListener(v -> listener.onViewProfile(request.getFromUid()));
    }

    @Override
    public int getItemCount() {
        return requests == null ? 0 : requests.size();
    }
}
