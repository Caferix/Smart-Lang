package com.scu.smartlang.data.remote.firebase.models;

import com.google.firebase.firestore.ServerTimestamp;
import java.util.Date;

// Firebase Firestore'dan okunan veya yazılan veri yapısı
public class UserDto {
    private String uid;
    private String userName;
    private String email;
    private boolean emailVerified;
    private int xp;
    private int level;
    private String profileImageUrl;
    private UserStatsDto stats;

    @ServerTimestamp
    private Date createdAt;

    // Firestore için boş kurucu (constructor)
    public UserDto() {}

    // Tam kurucu metot
    public UserDto(String uid, String userName, String email, boolean emailVerified, int xp,
                   int level, String profileImageUrl, Date createdAt, UserStatsDto stats) {
        this.uid = uid;
        this.userName = userName;
        this.email = email;
        this.emailVerified = emailVerified;
        this.xp = xp;
        this.level = level;
        this.profileImageUrl = profileImageUrl;
        this.createdAt = createdAt;
        this.stats = stats;
    }

    // Getters and Setters

    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public boolean isEmailVerified() { return emailVerified; }
    public void setEmailVerified(boolean emailVerified) { this.emailVerified = emailVerified; }

    public int getXp() { return xp; }
    public void setXp(int xp) { this.xp = xp; }

    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }

    public String getProfileImageUrl() { return profileImageUrl; }
    public void setProfileImageUrl(String profileImageUrl) { this.profileImageUrl = profileImageUrl; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public UserStatsDto getStats() { return stats; }
    public void setStats(UserStatsDto stats) { this.stats = stats; }
}