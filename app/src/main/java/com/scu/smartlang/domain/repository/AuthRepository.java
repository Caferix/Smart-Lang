package com.scu.smartlang.domain.repository;

import com.scu.smartlang.domain.model.User;
import java.util.concurrent.CompletableFuture;

public interface AuthRepository {
    CompletableFuture<User> createUserWithEmail(String email, String password, String userName);
    CompletableFuture<User> signInWithEmail(String email, String password);
    CompletableFuture<Void> signOut();
    CompletableFuture<Boolean> isUserSignedIn();
    CompletableFuture<String> getCurrentUserId();
    CompletableFuture<Void> sendPasswordResetEmail(String email);
    CompletableFuture<Void> updatePassword(String newPassword);

    CompletableFuture<Void> resendVerificationEmail();
    CompletableFuture<Void> reauthenticateAndUpdatePassword(String currentPassword, String newPassword);
}
