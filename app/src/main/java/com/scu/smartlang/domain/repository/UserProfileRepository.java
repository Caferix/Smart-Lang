package com.scu.smartlang.domain.repository;

import androidx.lifecycle.LiveData;

import com.scu.smartlang.domain.model.FriendRequest;
import com.scu.smartlang.domain.model.User;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import android.net.Uri;

public interface UserProfileRepository {
    CompletableFuture<User> getUserProfile(String uid);
    CompletableFuture<User> getCurrentUserProfile();
    CompletableFuture<Void> updateUserProfile(User user);
    LiveData<Integer> getUnreadNotificationsCount(String uid);
    CompletableFuture<Void> resetUnreadNotificationsCount(String uid);
    LiveData<List<FriendRequest>> getIncomingFriendRequests(String uid);
    CompletableFuture<String> uploadProfileImage(Uri imageUri);
}
