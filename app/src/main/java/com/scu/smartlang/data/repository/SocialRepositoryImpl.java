package com.scu.smartlang.data.repository;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.scu.smartlang.data.mapper.UserDataMapper;
import com.scu.smartlang.data.remote.firebase.models.UserDto;
import com.scu.smartlang.domain.model.Friend;
import com.scu.smartlang.domain.model.FriendRequest;
import com.scu.smartlang.domain.model.User;
import com.scu.smartlang.domain.repository.SocialRepository;
import com.scu.smartlang.domain.repository.UserProfileRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class SocialRepositoryImpl implements SocialRepository {

    private static final String USERS_COLLECTION = "users";
    private final FirebaseFirestore db;
    private final UserDataMapper userMapper;
    private final UserProfileRepository userProfileRepository;

    @Inject
    public SocialRepositoryImpl(FirebaseFirestore db, UserDataMapper userMapper, UserProfileRepository userProfileRepository) {
        this.db = db;
        this.userMapper = userMapper;
        this.userProfileRepository = userProfileRepository;
    }

    private <T> CompletableFuture<T> taskToFuture(Task<T> task) {
        CompletableFuture<T> future = new CompletableFuture<>();
        task.addOnSuccessListener(future::complete)
                .addOnFailureListener(future::completeExceptionally);
        return future;
    }

    @Override
    public CompletableFuture<Void> sendFriendRequest(String fromUid, String toUid) {
        return taskToFuture(
                db.collection(USERS_COLLECTION).document(fromUid)
                        .collection("friends")
                        .whereEqualTo("uid", toUid)
                        .limit(1)
                        .get()
        ).thenCompose(friendCheck -> {
            if (!friendCheck.isEmpty()) {
                CompletableFuture<Void> failed = new CompletableFuture<>();
                failed.completeExceptionally(new Exception("Bu kullanıcı zaten arkadaşınız"));
                return failed;
            }

            return taskToFuture(
                    db.collection(USERS_COLLECTION).document(toUid)
                            .collection("friendRequests")
                            .whereEqualTo("fromUid", fromUid)
                            .whereEqualTo("status", "PENDING")
                            .limit(1)
                            .get()
            ).thenCompose(requestCheck -> {
                if (!requestCheck.isEmpty()) {
                    CompletableFuture<Void> failed = new CompletableFuture<>();
                    failed.completeExceptionally(new Exception("Zaten bekleyen bir istek var"));
                    return failed;
                }

                return taskToFuture(db.collection(USERS_COLLECTION).document(fromUid).get())
                        .thenCompose(fromDoc -> {
                            UserDto fromUser = fromDoc.toObject(UserDto.class);
                            String senderName = fromUser != null && fromUser.getUserName() != null ? fromUser.getUserName() : "Bir kullanıcı";

                            Map<String, Object> request = new HashMap<>();
                            request.put("fromUid", fromUid);
                            request.put("toUid", toUid);
                            request.put("status", "PENDING");
                            request.put("senderName", senderName);
                            request.put("createdAt", FieldValue.serverTimestamp());

                            return taskToFuture(
                                    db.collection(USERS_COLLECTION).document(toUid)
                                            .collection("friendRequests")
                                            .add(request)
                            ).thenCompose(docRef -> taskToFuture(
                                    db.collection(USERS_COLLECTION).document(toUid)
                                            .update("unreadNotifications", FieldValue.increment(1))
                            ));
                        });
            });
        });
    }

    @Override
    public CompletableFuture<Void> acceptFriendRequest(String requestId, String acceptorUid) {
        DocumentReference requestRef = db.collection(USERS_COLLECTION)
                .document(acceptorUid)
                .collection("friendRequests")
                .document(requestId);

        return taskToFuture(requestRef.get())
                .thenCompose(ds -> {
                    if (!ds.exists()) {
                        CompletableFuture<Void> failed = new CompletableFuture<>();
                        failed.completeExceptionally(new Exception("İstek bulunamadı"));
                        return failed;
                    }

                    String fromUid = ds.getString("fromUid");

                    Map<String, Object> friendForAcceptor = new HashMap<>();
                    friendForAcceptor.put("uid", fromUid);
                    friendForAcceptor.put("createdAt", FieldValue.serverTimestamp());

                    Map<String, Object> friendForRequester = new HashMap<>();
                    friendForRequester.put("uid", acceptorUid);
                    friendForRequester.put("createdAt", FieldValue.serverTimestamp());

                    CompletableFuture<Void> deleteRequest = taskToFuture(requestRef.delete());

                    CompletableFuture<DocumentReference> addToAcceptor = taskToFuture(
                            db.collection(USERS_COLLECTION).document(acceptorUid)
                                    .collection("friends").add(friendForAcceptor)
                    );

                    CompletableFuture<DocumentReference> addToRequester = taskToFuture(
                            db.collection(USERS_COLLECTION).document(fromUid)
                                    .collection("friends").add(friendForRequester)
                    );

                    return CompletableFuture.allOf(deleteRequest, addToAcceptor, addToRequester);
                });
    }

    @Override
    public CompletableFuture<List<FriendRequest>> getIncomingFriendRequests(String uid) {
        return taskToFuture(
                db.collection(USERS_COLLECTION)
                        .document(uid)
                        .collection("friendRequests")
                        .whereEqualTo("toUid", uid)
                        .whereEqualTo("status", "PENDING")
                        .get()
        ).thenApply(qs -> {
            List<FriendRequest> list = new ArrayList<>();
            for (DocumentSnapshot ds : qs.getDocuments()) {
                FriendRequest req = ds.toObject(FriendRequest.class);
                if (req != null) {
                    req.setId(ds.getId());
                    list.add(req);
                }
            }
            return list;
        });
    }

    @Override
    public CompletableFuture<List<Friend>> getFriends(String uid) {
        return taskToFuture(db.collection(USERS_COLLECTION)
                .document(uid)
                .collection("friends")
                .get())
                .thenCompose(qs -> {
                    if (qs.isEmpty()) {
                        return CompletableFuture.completedFuture(new ArrayList<>());
                    }
                    List<CompletableFuture<Friend>> futures = new ArrayList<>();
                    for (DocumentSnapshot ds : qs.getDocuments()) {
                        String friendUid = ds.getString("uid");
                        if (friendUid != null) {
                            futures.add(userProfileRepository.getUserProfile(friendUid).thenApply(user -> {
                                if (user == null) return null;
                                return new Friend(user.getUid(), user.getUserName(), user.getLevel(), user.getProfileImageUrl());
                            }));
                        }
                    }
                    return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                            .thenApply(v -> {
                                List<Friend> res = new ArrayList<>();
                                for (CompletableFuture<Friend> f : futures) {
                                    try {
                                        Friend fr = f.get();
                                        if (fr != null) res.add(fr);
                                    } catch (Exception ignored) {}
                                }
                                return res;
                            });
                });
    }

    @Override
    public CompletableFuture<List<User>> getLeaderboard(int limit) {
        Query query = db.collection(USERS_COLLECTION)
                .orderBy("xp", Query.Direction.DESCENDING)
                .orderBy("level", Query.Direction.DESCENDING)
                .limit(limit);
        return taskToFuture(query.get())
                .thenApply(qs -> {
                    List<User> list = new ArrayList<>();
                    for (DocumentSnapshot ds : qs.getDocuments()) {
                        UserDto dto = ds.toObject(UserDto.class);
                        if (dto != null) {
                            list.add(userMapper.mapToDomain(dto));
                        }
                    }
                    return list;
                });
    }

    @Override
    public CompletableFuture<User> getUserById(String uid) {
        return userProfileRepository.getUserProfile(uid);
    }

    @Override
    public CompletableFuture<List<User>> searchUsersByName(String query) {
        return taskToFuture(
                db.collection(USERS_COLLECTION)
                        .orderBy("userName")
                        .startAt(query)
                        .endAt(query + "\uf8ff")
                        .limit(20)
                        .get()
        ).thenApply(qs -> {
            List<User> users = new ArrayList<>();
            for (DocumentSnapshot ds : qs.getDocuments()) {
                UserDto dto = ds.toObject(UserDto.class);
                if (dto != null) {
                    users.add(userMapper.mapToDomain(dto));
                }
            }
            return users;
        });
    }

    @Override
    public CompletableFuture<String> checkFriendshipStatus(String currentUid, String otherUid) {
        return taskToFuture(
                db.collection(USERS_COLLECTION).document(currentUid)
                        .collection("friends").whereEqualTo("uid", otherUid).limit(1).get()
        ).thenCompose(friendSnapshot -> {
            if (!friendSnapshot.isEmpty()) {
                return CompletableFuture.completedFuture("FRIENDS");
            }
            return taskToFuture(
                    db.collection(USERS_COLLECTION).document(otherUid)
                            .collection("friendRequests").whereEqualTo("fromUid", currentUid).limit(1).get()
            ).thenCompose(sentRequestSnapshot -> {
                if (!sentRequestSnapshot.isEmpty()) {
                    return CompletableFuture.completedFuture("REQUEST_SENT");
                }
                return taskToFuture(
                        db.collection(USERS_COLLECTION).document(currentUid)
                                .collection("friendRequests").whereEqualTo("fromUid", otherUid).limit(1).get()
                ).thenCompose(receivedRequestSnapshot -> {
                    if (!receivedRequestSnapshot.isEmpty()) {
                        return CompletableFuture.completedFuture("REQUEST_RECEIVED");
                    }
                    return CompletableFuture.completedFuture("NONE");
                });
            });
        });
    }
}
