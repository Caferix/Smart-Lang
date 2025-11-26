package com.scu.smartlang.domain.repository;

import androidx.lifecycle.LiveData;

import com.google.firebase.auth.FirebaseUser;
import com.scu.smartlang.domain.model.User;
import java.util.concurrent.CompletableFuture;

public interface AuthRepository {
    CompletableFuture<User> createUserWithEmail(String email, String password, String userName);
    CompletableFuture<User> signInWithEmail(String email, String password);
    CompletableFuture<Void> signOut();
    FirebaseUser getCurrentUser();
    CompletableFuture<Boolean> isUserSignedIn();
    CompletableFuture<String> getCurrentUserId();
    LiveData<String> getCurrentUserIdLiveData();
    CompletableFuture<Void> sendPasswordResetEmail(String email);
    CompletableFuture<Void> updatePassword(String newPassword);

    CompletableFuture<Void> resendVerificationEmail();
    CompletableFuture<Void> reauthenticateAndUpdatePassword(String currentPassword, String newPassword);
}
