package com.scu.smartlang.presentation.ui.profile;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.scu.smartlang.R;
import com.scu.smartlang.domain.model.FriendRequest;

import java.util.List;

public class FriendRequestsAdapter extends RecyclerView.Adapter<FriendRequestsAdapter.RequestViewHolder> {

    private List<FriendRequest> requests;
    private final OnRequestActionListener listener;

    public interface OnRequestActionListener {
        void onAccept(String requestId, String requesterUid);
        void onViewProfile(String userId);
        void onReject(String requestId);
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
    public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_friend_request, parent, false);
        return new RequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestViewHolder holder, int position) {
        FriendRequest request = requests.get(position);

        String display = request.getSenderName() != null && !request.getSenderName().isEmpty()
                ? request.getSenderName()
                : request.getFromUid();
        holder.tvFromUser.setText(display);

        // Profil resmini Glide ile yükle (URL varsa)
        if (request.getSenderProfileImageUrl() != null && !request.getSenderProfileImageUrl().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(request.getSenderProfileImageUrl())
                    .circleCrop()
                    .placeholder(R.drawable.ic_person_24dp)
                    .into(holder.ivProfileImage);
        } else {
            holder.ivProfileImage.setImageResource(R.drawable.ic_person_24dp);
        }

        holder.btnAccept.setOnClickListener(v -> listener.onAccept(request.getId(), request.getFromUid()));
        holder.btnReject.setOnClickListener(v -> listener.onReject(request.getId()));
        holder.itemView.setOnClickListener(v -> listener.onViewProfile(request.getFromUid()));
    }

    @Override
    public int getItemCount() {
        return requests == null ? 0 : requests.size();
    }

    public static class RequestViewHolder extends RecyclerView.ViewHolder {
        final TextView tvFromUser;
        final Button btnAccept;
        final Button btnReject;
        final ImageView ivProfileImage;

        public RequestViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFromUser = itemView.findViewById(R.id.tv_request_from);
            btnAccept = itemView.findViewById(R.id.btn_accept_request);
            btnReject = itemView.findViewById(R.id.btn_reject_request);
            ivProfileImage = itemView.findViewById(R.id.iv_profile_image);
        }
    }
}
