package com.scu.smartlang.data.remote.firebase.models;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class UserDto {
    private String uid;
    private String userName;
    private String email;
    private int xp;
    private int level;
    private String profileImageUrl;
    private UserStatsDto stats;

    @ServerTimestamp
    private Date createdAt;

    public UserDto() {} // Required for Firestore

    public UserDto(String uid, String userName, String email, int xp,
                   int level, String profileImageUrl, Date createdAt) {
        this.uid = uid;
        this.userName = userName;
        this.email = email;
        this.xp = xp;
        this.level = level;
        this.profileImageUrl = profileImageUrl;
        this.createdAt = createdAt;
    }

    // Getters and Setters
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

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
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

    public UserStatsDto getStats() {
        return stats;
    }

    public void setStats(UserStatsDto stats) {
        this.stats = stats;
    }

}
