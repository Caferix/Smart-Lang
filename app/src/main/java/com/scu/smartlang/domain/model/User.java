package com.scu.smartlang.domain.model;

import java.util.Date;
import com.google.firebase.firestore.ServerTimestamp;


public class User {
    private String uid;
    private String userName;
    private String email;
    private int xp;
    private byte level;
    private String profileImageUrl;
    private UserStats stats;

    public UserStats getStats() {
        return stats;
    }

    public void setStats(UserStats stats) {
        this.stats = stats;
    }

    @ServerTimestamp
    private Date createdAt;


    public User(){} // firestore icin bos constructor

    public User(String uid, String userName, String email, int xp, byte level, String profileImageUrl, Date createdAt) {
        this.uid = uid;
        this.userName = userName;
        this.email = email;
        this.xp = xp;
        this.level = level;
        this.profileImageUrl = profileImageUrl;
        this.createdAt = createdAt;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getXp() {
        return xp;
    }

    public void setXp(int xp) {
        this.xp = xp;
    }

    public byte getLevel() {
        return level;
    }

    public void setLevel(byte level) {
        this.level = level;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
