package com.scu.smartlang.domain.model;

import java.util.Date;

public class FriendRequest {
    public enum Status { PENDING, ACCEPTED, REJECTED }

    private String id;
    private String fromUid;
    private String toUid;
    private Status status;
    private Date createdAt;
    private String senderName;
    private String senderProfileImageUrl;

    public FriendRequest() {}

    public FriendRequest(String id, String fromUid, String toUid, Status status, Date createdAt) {
        this.id = id;
        this.fromUid = fromUid;
        this.toUid = toUid;
        this.status = status;
        this.createdAt = createdAt;
    }

    public String getId() { return id; }
    public String getFromUid() { return fromUid; }
    public String getToUid() { return toUid; }
    public Status getStatus() { return status; }
    public Date getCreatedAt() { return createdAt; }

    public void setId(String id) { this.id = id; }
    public void setFromUid(String fromUid) { this.fromUid = fromUid; }
    public void setToUid(String toUid) { this.toUid = toUid; }
    public void setStatus(Status status) { this.status = status; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderProfileImageUrl() {
        return senderProfileImageUrl;
    }

    public void setSenderProfileImageUrl(String senderProfileImageUrl) {
        this.senderProfileImageUrl = senderProfileImageUrl;
    }
}
