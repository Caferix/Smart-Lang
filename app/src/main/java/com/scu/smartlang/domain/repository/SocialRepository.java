package com.scu.smartlang.domain.repository;

import com.scu.smartlang.domain.model.Friend;
import com.scu.smartlang.domain.model.FriendRequest;
import com.scu.smartlang.domain.model.User;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface SocialRepository {
    CompletableFuture<Void> sendFriendRequest(String fromUid, String toUid);
    CompletableFuture<Void> acceptFriendRequest(String requestId, String acceptorUid);
    CompletableFuture<List<FriendRequest>> getIncomingFriendRequests(String uid);
    CompletableFuture<List<Friend>> getFriends(String uid);
    CompletableFuture<List<User>> getLeaderboard(int limit);
    CompletableFuture<User> getUserById(String uid);
    CompletableFuture<List<User>> searchUsersByName(String query);
    CompletableFuture<String> checkFriendshipStatus(String currentUid, String otherUid);
}
