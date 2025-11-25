package com.scu.smartlang.data.repository;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.scu.smartlang.data.mapper.UserDataMapper;
import com.scu.smartlang.data.remote.firebase.FirebaseAuth;
import com.scu.smartlang.data.remote.firebase.models.UserDto;
import com.scu.smartlang.domain.model.User;
import com.scu.smartlang.domain.repository.UserProfileRepository;
import java.util.concurrent.CompletableFuture;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class UserProfileRepositoryImpl implements UserProfileRepository {

    private static final String USERS_COLLECTION = "users";
    private final FirebaseFirestore db;
    private final UserDataMapper userMapper;
    private final FirebaseAuth auth;

    @Inject
    public UserProfileRepositoryImpl(FirebaseFirestore db, UserDataMapper userMapper, FirebaseAuth auth) {
        this.db = db;
        this.userMapper = userMapper;
        this.auth = auth;
    }

    private <T> CompletableFuture<T> taskToFuture(Task<T> task) {
        CompletableFuture<T> future = new CompletableFuture<>();
        task.addOnSuccessListener(future::complete)
                .addOnFailureListener(future::completeExceptionally);
        return future;
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
            CompletableFuture<Void> failedFuture = new CompletableFuture<>();
            failedFuture.completeExceptionally(new IllegalArgumentException("User or UID is null"));
            return failedFuture;
        }
        UserDto dto = userMapper.mapToDto(user);
        return taskToFuture(db.collection(USERS_COLLECTION)
                .document(user.getUid())
                .set(dto));
    }

    @Override
    public CompletableFuture<Integer> getUnreadNotificationsCount(String uid) {
        CompletableFuture<Integer> future = new CompletableFuture<>();
        db.collection("users").document(uid).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists() && documentSnapshot.contains("unreadNotifications")) {
                        Long count = documentSnapshot.getLong("unreadNotifications");
                        future.complete(count != null ? count.intValue() : 0);
                    } else {
                        future.complete(0);
                    }
                })
                .addOnFailureListener(future::completeExceptionally);
        return future;
    }
}
