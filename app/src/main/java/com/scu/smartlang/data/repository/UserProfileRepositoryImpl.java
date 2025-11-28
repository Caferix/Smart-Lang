package com.scu.smartlang.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.scu.smartlang.data.mapper.UserDataMapper;
import com.scu.smartlang.data.remote.firebase.FirebaseAuth;
import com.scu.smartlang.data.remote.firebase.models.UserDto;
import com.scu.smartlang.domain.model.FriendRequest;
import com.scu.smartlang.domain.model.User;
import com.scu.smartlang.domain.repository.UserProfileRepository;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import android.net.Uri;
import java.util.UUID;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class UserProfileRepositoryImpl implements UserProfileRepository {

    private static final String USERS_COLLECTION = "users";
    private static final String PROFILE_IMAGES_PATH = "profile_images";
    private final FirebaseFirestore db;
    private final FirebaseStorage storage;
    private final UserDataMapper userMapper;
    private final FirebaseAuth auth;

    @Inject
    public UserProfileRepositoryImpl(FirebaseFirestore db, FirebaseStorage storage, UserDataMapper userMapper, FirebaseAuth auth) {
        this.db = db;
        this.storage = storage;
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
    public LiveData<Integer> getUnreadNotificationsCount(String uid) {
        MutableLiveData<Integer> countLiveData = new MutableLiveData<>();
        db.collection(USERS_COLLECTION).document(uid)
                .addSnapshotListener((snapshot, e) -> {
                    if (e != null) {
                        // Hata durumunda sayıyı 0 olarak kabul edebiliriz veya loglayabiliriz.
                        countLiveData.postValue(0);
                        return;
                    }

                    if (snapshot != null && snapshot.exists()) {
                        Long count = snapshot.getLong("unreadNotifications");
                        countLiveData.postValue(count != null ? count.intValue() : 0);
                    } else {
                        countLiveData.postValue(0);
                    }
                });
        return countLiveData;
    }

    @Override
    public CompletableFuture<Void> resetUnreadNotificationsCount(String uid) {
        return taskToFuture(db.collection(USERS_COLLECTION)
                .document(uid)
                .update("unreadNotifications", 0));
    }

    @Override
    public LiveData<List<FriendRequest>> getIncomingFriendRequests(String uid) {
        MutableLiveData<List<FriendRequest>> requestsLiveData = new MutableLiveData<>();
        Query query = db.collection("friend_requests")
                .whereEqualTo("toUid", uid)
                .whereEqualTo("status", "pending");

        query.addSnapshotListener((snapshots, e) -> {
            if (e != null) {
                // Handle error
                requestsLiveData.postValue(new ArrayList<>());
                return;
            }

            List<FriendRequest> requests = new ArrayList<>();
            if (snapshots != null) {
                for (DocumentSnapshot doc : snapshots.getDocuments()) {
                    FriendRequest request = doc.toObject(FriendRequest.class);
                    if (request != null) {
                        request.setId(doc.getId());
                        requests.add(request);
                    }
                }
            }
            requestsLiveData.postValue(requests);
        });

        return requestsLiveData;
    }

    @Override
    public CompletableFuture<String> uploadProfileImage(Uri imageUri) {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            return CompletableFuture.failedFuture(new IllegalStateException("Kullanıcı giriş yapmamış."));
        }
        String fileName = UUID.randomUUID().toString();
        StorageReference ref = storage.getReference().child(PROFILE_IMAGES_PATH + "/" + currentUser.getUid() + "/" + fileName);

        CompletableFuture<String> future = new CompletableFuture<>();
        ref.putFile(imageUri)
                .continueWithTask(task -> {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return ref.getDownloadUrl();
                })
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        future.complete(downloadUri.toString());
                    } else {
                        future.completeExceptionally(task.getException());
                    }
                });
        return future;
    }
}
