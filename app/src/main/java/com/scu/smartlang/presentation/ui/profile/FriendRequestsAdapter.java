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

public class FriendRequestsAdapter extends RecyclerView.Adapter<FriendRequestsAdapter.ViewHolder> {

    private List<FriendRequest> requests;
    private final OnAcceptClickListener listener;

    public interface OnAcceptClickListener {
        void onAccept(String requestId);
    }

    public FriendRequestsAdapter(List<FriendRequest> requests, OnAcceptClickListener listener) {
        this.requests = requests;
        this.listener = listener;
    }

    public void updateList(List<FriendRequest> newList) {
        this.requests = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_friend_request, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FriendRequest request = requests.get(position);
        holder.tvFromUser.setText("Kullanıcı: " + request.getFromUid());
        holder.btnAccept.setOnClickListener(v -> listener.onAccept(request.getId()));
    }

    @Override
    public int getItemCount() {
        return requests.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvFromUser;
        Button btnAccept;

        ViewHolder(View itemView) {
            super(itemView);
            tvFromUser = itemView.findViewById(R.id.tv_request_from);
            btnAccept = itemView.findViewById(R.id.btn_accept_request);
        }
    }
}
