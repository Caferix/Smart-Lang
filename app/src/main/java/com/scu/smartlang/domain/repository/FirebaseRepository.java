package com.scu.smartlang.domain.repository;

import com.scu.smartlang.domain.model.User;

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
}
