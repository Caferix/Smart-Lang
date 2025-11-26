package com.scu.smartlang.data.repository;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.scu.smartlang.data.mapper.UserDataMapper;
import com.scu.smartlang.data.remote.firebase.models.UserDto;
import com.scu.smartlang.domain.model.Friend;
import com.scu.smartlang.domain.model.FriendRequest;
import com.scu.smartlang.domain.model.User;
import com.scu.smartlang.domain.repository.AuthRepository;
import com.scu.smartlang.domain.repository.SocialRepository;
import com.scu.smartlang.domain.repository.UserProfileRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import io.reactivex.rxjava3.core.Observable;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class SocialRepositoryImpl implements SocialRepository {

    private static final String USERS_COLLECTION = "users";
    private final FirebaseFirestore db;
    private final UserDataMapper userMapper;
    private final UserProfileRepository userProfileRepository;
    private final AuthRepository authRepository;

    @Inject
    public SocialRepositoryImpl(FirebaseFirestore db, UserDataMapper userMapper, UserProfileRepository userProfileRepository, AuthRepository authRepository) {
        this.db = db;
        this.userMapper = userMapper;
        this.userProfileRepository = userProfileRepository;
        this.authRepository = authRepository;
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

                return taskToFuture(
                        db.collection(USERS_COLLECTION).document(fromUid).get()
                ).thenCompose(fromDoc -> {
                    UserDto fromUser = fromDoc.toObject(UserDto.class);
                    String senderName = fromUser != null && fromUser.getUserName() != null
                            ? fromUser.getUserName()
                            : "Bir kullanıcı";

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
                    ).thenCompose(docRef ->
                            taskToFuture(
                                    db.collection(USERS_COLLECTION).document(toUid)
                                            .update("unreadNotifications", FieldValue.increment(1))
                            )
                    );
                });
            });
        });
    }

    @Override
    public CompletableFuture<Void> acceptFriendRequest(String requestId, String acceptorUid, String requesterUid) {
        DocumentReference requestRef = db.collection(USERS_COLLECTION)
                .document(acceptorUid)
                .collection("friendRequests")
                .document(requestId);

        return taskToFuture(db.runTransaction(transaction -> {
            if (requesterUid == null) {
                throw new FirebaseFirestoreException("Requester UID cannot be null.",
                        FirebaseFirestoreException.Code.INVALID_ARGUMENT);
            }

            // 1. Add sender to acceptor's friends subcollection
            DocumentReference acceptorFriendRef = db.collection(USERS_COLLECTION).document(acceptorUid)
                    .collection("friends").document(requesterUid);
            Map<String, Object> acceptorFriendData = new HashMap<>();
            acceptorFriendData.put("uid", requesterUid);
            transaction.set(acceptorFriendRef, acceptorFriendData);

            // 2. Add acceptor to sender's friends subcollection
            DocumentReference senderFriendRef = db.collection(USERS_COLLECTION).document(requesterUid)
                    .collection("friends").document(acceptorUid);
            Map<String, Object> senderFriendData = new HashMap<>();
            senderFriendData.put("uid", acceptorUid);
            transaction.set(senderFriendRef, senderFriendData);

            // 3. Delete the friend request after accepting it
            transaction.delete(requestRef);

            return null;
        }));
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
                        .collection("friends").document(otherUid).get()
        ).thenCompose(friendSnapshot -> {
            if (friendSnapshot.exists()) {
                return CompletableFuture.completedFuture("FRIENDS");
            }
            // Check for a PENDING request sent by me
            return taskToFuture(
                    db.collection(USERS_COLLECTION).document(otherUid)
                            .collection("friendRequests")
                            .whereEqualTo("fromUid", currentUid)
                            .whereEqualTo("status", "PENDING") // Check for PENDING status
                            .limit(1).get()
            ).thenCompose(sentRequestSnapshot -> {
                if (!sentRequestSnapshot.isEmpty()) {
                    return CompletableFuture.completedFuture("REQUEST_SENT");
                }
                // Check for a PENDING request received from them
                return taskToFuture(
                        db.collection(USERS_COLLECTION).document(currentUid)
                                .collection("friendRequests")
                                .whereEqualTo("fromUid", otherUid)
                                .whereEqualTo("status", "PENDING") // Check for PENDING status
                                .limit(1).get()
                ).thenCompose(receivedRequestSnapshot -> {
                    if (!receivedRequestSnapshot.isEmpty()) {
                        return CompletableFuture.completedFuture("REQUEST_RECEIVED");
                    }
                    return CompletableFuture.completedFuture("NONE");
                });
            });
        });
    }


    @Override
    public CompletableFuture<Void> removeFriend(String currentUserId, String friendToRemoveId) {
        // İki kullanıcının da "friends" koleksiyonundan birbirlerini sil
        Task<Void> remove1 = db.collection("users").document(currentUserId)
                .collection("friends").document(friendToRemoveId).delete();
        Task<Void> remove2 = db.collection("users").document(friendToRemoveId)
                .collection("friends").document(currentUserId).delete();

        // İki işlem de tamamlandığında CompletableFuture'ı tamamla
        return taskToFuture(Tasks.whenAll(remove1, remove2));
    }

    @Override
    public CompletableFuture<Void> rejectFriendRequest(String requestId, String recipientUid) {
        return taskToFuture(
                db.collection(USERS_COLLECTION)
                        .document(recipientUid)
                        .collection("friendRequests")
                        .document(requestId)
                        .delete()
        );
    }

    @Override
    public Observable<List<User>> getFriendLeaderboard() {
        return Observable.create(emitter -> {
            String currentUid = authRepository.getCurrentUser().getUid();
            if (currentUid == null) {
                emitter.onError(new Exception("User not logged in"));
                return;
            }

            db.collection(USERS_COLLECTION).document(currentUid).collection("friends")
                    .addSnapshotListener((friendsSnapshot, error) -> {
                        if (error != null) {
                            emitter.onError(error);
                            return;
                        }

                        if (friendsSnapshot == null) {
                            emitter.onNext(new ArrayList<>());
                            return;
                        }

                        List<String> friendUids = new ArrayList<>();
                        for (DocumentSnapshot doc : friendsSnapshot.getDocuments()) {
                            friendUids.add(doc.getId());
                        }
                        friendUids.add(currentUid);


                        if (friendUids.isEmpty()) {
                            emitter.onNext(new ArrayList<>());
                            return;
                        }

                        db.collection(USERS_COLLECTION)
                                .whereIn("uid", friendUids)
                                .addSnapshotListener((usersSnapshot, usersError) -> {
                                    if (usersError != null) {
                                        emitter.onError(usersError);
                                        return;
                                    }

                                    if (usersSnapshot == null) {
                                        emitter.onNext(new ArrayList<>());
                                        return;
                                    }

                                    List<User> leaderboard = new ArrayList<>();
                                    for (DocumentSnapshot userDoc : usersSnapshot.getDocuments()) {
                                        User user = userDoc.toObject(User.class);
                                        if(user != null){
                                            leaderboard.add(user);
                                        }
                                    }

                                    leaderboard.sort((u1, u2) -> Integer.compare(u2.getXp(), u1.getXp()));
                                    emitter.onNext(leaderboard);
                                });
                    });
        });
    }
}
