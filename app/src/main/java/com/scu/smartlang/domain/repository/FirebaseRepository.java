package com.scu.smartlang.domain.repository;

import com.scu.smartlang.domain.model.Friend;
import com.scu.smartlang.domain.model.FriendRequest;
import com.scu.smartlang.domain.model.User;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface FirebaseRepository {
    // Authentication operations
    CompletableFuture<User> createUserWithEmail(String email, String password, String userName);
    CompletableFuture<User> signInWithEmail(String email, String password);
    CompletableFuture<Void> signOut();
    CompletableFuture<Boolean> isUserSignedIn();
    CompletableFuture<String> getCurrentUserId();

    // User profile operations
    CompletableFuture<User> getCurrentUserProfile();
    CompletableFuture<User> getUserProfile(String uid);
    CompletableFuture<Void> updateUserProfile(User user);

    //Password operations
    CompletableFuture<Void> sendPasswordResetEmail(String email);
    CompletableFuture<Void> updatePassword(String newPassword);
    CompletableFuture<Void> resendVerificationEmail();
    CompletableFuture<Void> reauthenticateAndUpdatePassword (String currentPassword, String newPassword);

    // Friendship operations
    CompletableFuture<Void> sendFriendRequest(String fromUid, String toUid);
    CompletableFuture<Void> acceptFriendRequest(String requestId, String acceptorUid);
    CompletableFuture<List<FriendRequest>> getIncomingFriendRequests(String uid);
    CompletableFuture<List<Friend>> getFriends(String uid);
    CompletableFuture<List<User>> getLeaderboard(int limit);
    CompletableFuture<User> getUserById(String uid);
    CompletableFuture<List<User>> searchUsersByName(String query);
    CompletableFuture<Integer> getUnreadNotificationsCount(String uid);
    CompletableFuture<String> checkFriendshipStatus(String currentUid, String otherUid);
}
