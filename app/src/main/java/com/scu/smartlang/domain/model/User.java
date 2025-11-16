package com.scu.smartlang.domain.model;

import java.util.Date;
import com.google.firebase.firestore.ServerTimestamp;


public class User {
    private String uid;
    private String userName;
    private String email;
    private boolean emailVerified; // EKLENDİ: Auth akışı için kritik
    private int xp;
    private int level;
    private String profileImageUrl;
    private UserStats stats; // YERİNDE DURUYOR

    public UserStats getStats() {
        return stats;
    }

    public void setStats(UserStats stats) {
        this.stats = stats;
    }

    @ServerTimestamp
    private Date createdAt;


    public User(){} // firestore icin bos constructor

    // Tam Kurucu Metot (Constructor) GÜNCELLENDİ (9 parametre)
    public User(String uid, String userName, String email, boolean emailVerified, int xp, int level, String profileImageUrl, Date createdAt, UserStats stats) {
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

    // Auth akışı için kritik
    public boolean isEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
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
}