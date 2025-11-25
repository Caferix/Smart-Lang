package com.scu.smartlang.data.repository;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseUser;
import com.scu.smartlang.data.mapper.UserDataMapper;
import com.scu.smartlang.data.remote.firebase.FirebaseAuth;
import com.scu.smartlang.domain.model.User;
import com.scu.smartlang.domain.repository.AuthRepository;
import com.scu.smartlang.domain.repository.UserProfileRepository;
import java.util.concurrent.CompletableFuture;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class AuthRepositoryImpl implements AuthRepository {

    private final FirebaseAuth auth;
    private final UserProfileRepository userProfileRepository;
    private final UserDataMapper userMapper;

    @Inject
    public AuthRepositoryImpl(FirebaseAuth auth, UserProfileRepository userProfileRepository, UserDataMapper userMapper) {
        this.auth = auth;
        this.userProfileRepository = userProfileRepository;
        this.userMapper = userMapper;
    }

    private <T> CompletableFuture<T> taskToFuture(Task<T> task) {
        CompletableFuture<T> future = new CompletableFuture<>();
        if (task == null) {
            future.completeExceptionally(new IllegalArgumentException("Task was null"));
            return future;
        }
        task.addOnSuccessListener(future::complete)
                .addOnFailureListener(future::completeExceptionally);
        return future;
    }

    @Override
    public CompletableFuture<User> createUserWithEmail(String email, String password, String userName) {
        return taskToFuture(auth.createUserWithEmailAndPassword(email, password))
                .thenCompose(authResult -> {
                    FirebaseUser firebaseUser = authResult.getUser();
                    if (firebaseUser == null) {
                        CompletableFuture<User> failed = new CompletableFuture<>();
                        failed.completeExceptionally(new IllegalStateException("Firebase user is null after create"));
                        return failed;
                    }

                    return taskToFuture(firebaseUser.sendEmailVerification()).thenCompose(aVoid -> {
                        User user = new User();
                        user.setUid(firebaseUser.getUid());
                        user.setEmail(firebaseUser.getEmail());
                        user.setUserName(userName);
                        user.setXp(0);
                        user.setLevel(1);
                        user.setEmailVerified(false);

                        return userProfileRepository.updateUserProfile(user).thenApply(v -> user);
                    });
                });
    }

    @Override
    public CompletableFuture<User> signInWithEmail(String email, String password) {
        return taskToFuture(auth.signInWithEmailAndPassword(email, password))
                .thenCompose(authResult -> {
                    FirebaseUser firebaseUser = authResult.getUser();
                    if (firebaseUser == null) {
                        CompletableFuture<User> failed = new CompletableFuture<>();
                        failed.completeExceptionally(new IllegalStateException("Firebase user is null after sign in"));
                        return failed;
                    }

                    return taskToFuture(firebaseUser.reload()).thenCompose(aVoid -> {
                        if (!firebaseUser.isEmailVerified()) {
                            CompletableFuture<User> failed = new CompletableFuture<>();
                            failed.completeExceptionally(new IllegalStateException("Email is not verified"));
                            return failed;
                        }
                        return userProfileRepository.getUserProfile(firebaseUser.getUid());
                    });
                });
    }

    @Override
    public CompletableFuture<Void> signOut() {
        auth.signOut();
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletableFuture<Boolean> isUserSignedIn() {
        return CompletableFuture.completedFuture(auth.getCurrentUser() != null);
    }

    @Override
    public CompletableFuture<String> getCurrentUserId() {
        FirebaseUser user = auth.getCurrentUser();
        return CompletableFuture.completedFuture(user != null ? user.getUid() : null);
    }

    @Override
    public CompletableFuture<Void> sendPasswordResetEmail(String email) {
        return taskToFuture(auth.sendPasswordResetEmail(email));
    }

    @Override
    public CompletableFuture<Void> updatePassword(String newPassword) {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            CompletableFuture<Void> failed = new CompletableFuture<>();
            failed.completeExceptionally(new IllegalStateException("No user signed in"));
            return failed;
        }
        return taskToFuture(user.updatePassword(newPassword));
    }

    @Override
    public CompletableFuture<Void> resendVerificationEmail() {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            CompletableFuture<Void> failed = new CompletableFuture<>();
            failed.completeExceptionally(new IllegalStateException("No user is currently signed in."));
            return failed;
        }
        if (user.isEmailVerified()) {
            return CompletableFuture.completedFuture(null);
        }
        return taskToFuture(user.sendEmailVerification());
    }

    @Override
    public CompletableFuture<Void> reauthenticateAndUpdatePassword(String currentPassword, String newPassword) {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null || user.getEmail() == null) {
            CompletableFuture<Void> failed = new CompletableFuture<>();
            failed.completeExceptionally(new IllegalStateException("User not signed in or has no email."));
            return failed;
        }

        AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), currentPassword);
        return taskToFuture(user.reauthenticate(credential))
                .thenCompose(aVoid -> taskToFuture(user.updatePassword(newPassword)));
    }
}
