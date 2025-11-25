package com.scu.smartlang.data.remote.firebase.models;

import java.util.Date;

public class FriendRequestDto {
    public String id;
    public String fromUid;
    public String toUid;
    public String status;
    public Date createdAt;

    public FriendRequestDto() {}

    public FriendRequestDto(String id, String fromUid, String toUid, String status, Date createdAt) {
        this.id = id;
        this.fromUid = fromUid;
        this.toUid = toUid;
        this.status = status;
        this.createdAt = createdAt;
    }
}
