package com.scu.smartlang.domain.repository;

import com.scu.smartlang.domain.model.User;
import java.util.concurrent.CompletableFuture;

public interface UserProfileRepository {
    CompletableFuture<User> getUserProfile(String uid);
    CompletableFuture<User> getCurrentUserProfile();
    CompletableFuture<Void> updateUserProfile(User user);
    CompletableFuture<Integer> getUnreadNotificationsCount(String uid);
}
