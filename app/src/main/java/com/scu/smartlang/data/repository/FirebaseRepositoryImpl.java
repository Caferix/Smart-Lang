package com.scu.smartlang.data.repository;

import android.os.Build;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.scu.smartlang.data.mapper.UserDataMapper;
import com.scu.smartlang.data.remote.firebase.FirebaseAuth;
import com.scu.smartlang.data.remote.firebase.models.UserDto;
import com.scu.smartlang.domain.model.User;
import com.scu.smartlang.domain.repository.FirebaseRepository;
import java.util.concurrent.CompletableFuture;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class FirebaseRepositoryImpl implements FirebaseRepository {

    private static final String USERS_COLLECTION = "users";
    private final FirebaseAuth auth;
    private final FirebaseFirestore db;
    private final UserDataMapper userMapper;

    @Inject
    public FirebaseRepositoryImpl(
            FirebaseAuth auth,
            FirebaseFirestore db,
            UserDataMapper userMapper
    ) {
        this.auth = auth;
        this.db = db;
        this.userMapper = userMapper;
    }

    // converter Task to CompletableFuture
    private <T> CompletableFuture<T> taskToFuture(Task<T> task) {
        if (task == null) {
            CompletableFuture<T> failed = new CompletableFuture<>();
            failed.completeExceptionally(new IllegalArgumentException("Task was null"));
            return failed;
        }
        CompletableFuture<T> future = new CompletableFuture<>();
        task.addOnSuccessListener(result -> future.complete(result))
                .addOnFailureListener(future::completeExceptionally);
        return future;
    }

    @Override
    public CompletableFuture<Boolean> isUserSignedIn() {
        return CompletableFuture.completedFuture(auth.getCurrentUser() != null);
    }


    @Override
    public CompletableFuture<User> createUserWithEmail(String email, String password, String userName) {
        return taskToFuture(auth.createUserWithEmailAndPassword(email, password))
                .thenCompose(authResult -> {
                    FirebaseUser firebaseUser = authResult.getUser();
                    if (firebaseUser == null) {
                        CompletableFuture<User> failed = new CompletableFuture<>();
                        failed.completeExceptionally(
                                new IllegalStateException("Firebase user is null after create")
                        );
                        return failed;
                    }

                    // sending verification email
                    CompletableFuture<Void> emailVerificationFuture =
                            taskToFuture(firebaseUser.sendEmailVerification());

                    // after sending verification mail save user to firestore
                    return emailVerificationFuture.thenCompose(aVoid -> {
                        // Build domain user
                        User user = new User();
                        user.setUid(firebaseUser.getUid());
                        user.setEmail(firebaseUser.getEmail());
                        user.setUserName(userName);
                        user.setXp(0);
                        user.setLevel(1);
                        user.setProfileImageUrl(null);

                        // mapping & save
                        UserDto dto = userMapper.mapToDto(user);

                        return taskToFuture(
                                db.collection(USERS_COLLECTION)
                                        .document(firebaseUser.getUid())
                                        .set(dto)
                        ).thenApply(v -> user);
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
                        failed.completeExceptionally(
                                new IllegalStateException("Firebase user is null after sign in"));
                        return failed;
                    }

                    // update user (reload)
                    return taskToFuture(firebaseUser.reload())
                            .thenCompose(aVoid -> {

                                // is email verified?
                                if (!firebaseUser.isEmailVerified()) {

                                    // throw exception
                                    CompletableFuture<User> failed = new CompletableFuture<>();
                                    failed.completeExceptionally(
                                            new IllegalStateException("Email is not verified"));
                                    return failed;
                                }

                                // get verified profile
                                return getUserProfile(firebaseUser.getUid());
                            });
                });
    }

    @Override
    public CompletableFuture<Void> signOut() {
        // Firebase signOut is synchronous
        try {
            auth.signOut();
            return CompletableFuture.completedFuture(null);
        } catch (Exception e) {
            CompletableFuture<Void> failed = new CompletableFuture<>();
            failed.completeExceptionally(e);
            return failed;
        }
    }

    @Override
    public CompletableFuture<User> getUserProfile(String uid) {
        return taskToFuture(db.collection(USERS_COLLECTION)
                .document(uid)
                .get())
                .thenApply(documentSnapshot -> {
                    if (documentSnapshot == null || !documentSnapshot.exists()) {
                        return null;
                    }
                    UserDto dto = documentSnapshot.toObject(UserDto.class);
                    return userMapper.mapToDomain(dto);
                });
    }
    @Override
    public CompletableFuture<String> getCurrentUserId() {
        FirebaseUser user = auth.getCurrentUser();
        return CompletableFuture.completedFuture(user != null ? user.getUid() : null);
    }

    @Override
    public CompletableFuture<User> getCurrentUserProfile() {
        FirebaseUser current = auth.getCurrentUser();
        if (current == null) {
            return CompletableFuture.completedFuture(null);
        }
        return getUserProfile(current.getUid());
    }

    @Override
    public CompletableFuture<Void> updateUserProfile(User user) {
        if (user == null || user.getUid() == null || user.getUid().isEmpty()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                return CompletableFuture.failedFuture(
                        new IllegalArgumentException("User or UID is null")
                );
            } else {
                CompletableFuture<Void> failed = new CompletableFuture<>();
                failed.completeExceptionally(new IllegalArgumentException("User or UID is null"));
                return failed;
            }
        }

        UserDto dto = userMapper.mapToDto(user);
        return taskToFuture(db.collection(USERS_COLLECTION)
                .document(user.getUid())
                .set(dto));
    }

    @Override
    public CompletableFuture<Void> sendPasswordResetEmail(String email) {
        return taskToFuture(auth.sendPasswordResetEmail(email));
    }

    @Override
    public CompletableFuture<Void> updatePassword(String newPassword) {
        FirebaseUser user = auth.getCurrentUser();

        if (user == null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                return CompletableFuture.failedFuture(
                        new IllegalStateException("No user signed in")
                );
            } else {
                CompletableFuture<Void> failed = new CompletableFuture<>();
                failed.completeExceptionally(new IllegalStateException("No user signed in"));
                return failed;
            }
        }

        if (newPassword == null || newPassword.isEmpty()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                return CompletableFuture.failedFuture(
                        new IllegalArgumentException("New password cannot be null or empty")
                );
            } else {
                CompletableFuture<Void> failed = new CompletableFuture<>();
                failed.completeExceptionally(new IllegalArgumentException("New password cannot be null or empty"));
                return failed;
            }
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

        // if already verified do nothing
        if (user.isEmailVerified()) {
            return CompletableFuture.completedFuture(null);
        }

        // resend verification email
        return taskToFuture(user.sendEmailVerification());
    }

}
