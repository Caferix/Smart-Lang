package com.scu.smartlang.presentation.ui.aichat.model;

public class ChatMessage {
    private String message;
    private boolean isUserMessage;

    public ChatMessage(String message, boolean isUserMessage) {
        this.message = message;
        this.isUserMessage = isUserMessage;
    }

    public String getMessage() {
        return message;
    }

    public boolean isUserMessage() {
        return isUserMessage;
    }
}
