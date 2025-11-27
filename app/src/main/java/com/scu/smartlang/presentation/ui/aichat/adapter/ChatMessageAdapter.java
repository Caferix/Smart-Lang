package com.scu.smartlang.presentation.ui.aichat.adapter;

import android.os.Build;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.scu.smartlang.R;
import com.scu.smartlang.presentation.ui.aichat.model.ChatMessage;

import java.util.List;

public class ChatMessageAdapter extends RecyclerView.Adapter<ChatMessageAdapter.ChatMessageViewHolder> {

    private static final int VIEW_TYPE_USER = 1;
    private static final int VIEW_TYPE_AI = 2;

    private List<ChatMessage> chatMessages;

    public ChatMessageAdapter(List<ChatMessage> chatMessages) {
        this.chatMessages = chatMessages;
    }

    @Override
    public int getItemViewType(int position) {
        if (chatMessages.get(position).isUserMessage()) {
            return VIEW_TYPE_USER;
        } else {
            return VIEW_TYPE_AI;
        }
    }

    @NonNull
    @Override
    public ChatMessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == VIEW_TYPE_AI) {
            // AI messages (left)
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_message_ai, parent, false);
        } else {
            // User messages (right)
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_message_user, parent, false);
        }
        return new ChatMessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatMessageViewHolder holder, int position) {
        ChatMessage chatMessage = chatMessages.get(position);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            holder.tvMessage.setText(Html.fromHtml(chatMessage.getMessage(), Html.FROM_HTML_MODE_LEGACY));
        } else {
            holder.tvMessage.setText(Html.fromHtml(chatMessage.getMessage()));
        }
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    static class ChatMessageViewHolder extends RecyclerView.ViewHolder {
        TextView tvMessage;

        public ChatMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMessage = itemView.findViewById(R.id.tv_message);
        }
    }
}
