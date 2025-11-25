package com.scu.smartlang.domain.model;

public class Friend {
    private String uid;
    private String userName;
    private int level;
    private String profileImageUrl;

    public Friend() {}

    public Friend(String uid, String userName, int level, String profileImageUrl) {
        this.uid = uid;
        this.userName = userName;
        this.level = level;
        this.profileImageUrl = profileImageUrl;
    }

    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }
    public String getProfileImageUrl() { return profileImageUrl; }
    public void setProfileImageUrl(String profileImageUrl) { this.profileImageUrl = profileImageUrl; }
}
