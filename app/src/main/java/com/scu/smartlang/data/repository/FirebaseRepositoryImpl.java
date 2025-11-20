package com.scu.smartlang.data.repository;

import android.os.Build;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
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

                        user.setEmailVerified(firebaseUser.isEmailVerified());

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

                                // get verified profile from firestore
                                return getUserProfile(firebaseUser.getUid())
                                        .thenCompose(domainUser -> {
                                            // If no Firestore doc, create minimal user and persist it
                                            if (domainUser == null) {
                                                User u = new User();
                                                u.setUid(firebaseUser.getUid());
                                                u.setEmail(firebaseUser.getEmail());
                                                u.setUserName(firebaseUser.getDisplayName());
                                                u.setProfileImageUrl(firebaseUser.getPhotoUrl() != null ? firebaseUser.getPhotoUrl().toString() : null);
                                                u.setXp(0);
                                                u.setLevel(1);
                                                u.setEmailVerified(true); // now verified

                                                // persist new doc
                                                return updateUserProfile(u)
                                                        .thenApply(v -> u);
                                            } else {
                                                // If Firestore doc exists but emailVerified isn't set, update it
                                                if (!Boolean.TRUE.equals(domainUser.isEmailVerified())) {
                                                    domainUser.setEmailVerified(true);
                                                    // persist the updated flag
                                                    return updateUserProfile(domainUser)
                                                            .thenApply(v -> domainUser);
                                                } else {
                                                    return CompletableFuture.completedFuture(domainUser);
                                                }
                                            }
                                        });
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

        // Fetch Firestore profile; then attach the FirebaseUser emailVerified flag.
        return getUserProfile(current.getUid()).thenApply(user -> {
            if (user == null) {
                // If no Firestore doc, return a minimal User built from FirebaseUser
                User u = new User();
                u.setUid(current.getUid());
                u.setEmail(current.getEmail());
                u.setUserName(current.getDisplayName());
                u.setProfileImageUrl(current.getPhotoUrl() != null ? current.getPhotoUrl().toString() : null);
                u.setXp(0);
                u.setLevel(1);
                u.setEmailVerified(current.isEmailVerified()); // ensure flag is set
                return u;
            } else {
                // Attach verification flag to existing domain user
                user.setEmailVerified(current.isEmailVerified());
                return user;
            }
        });
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

    @Override
    public CompletableFuture<Void> reauthenticateAndUpdatePassword(String currentPassword, String newPassword) {
        CompletableFuture<Void> future = new CompletableFuture<>();

        FirebaseUser user = auth.getCurrentUser();
        if (user == null || user.getEmail() == null) {
            future.completeExceptionally(new Exception("Kullanıcı oturum açmamış"));
            return future;
        }

        // 1. Kullanıcıyı eski şifresiyle yeniden doğrula
        AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), currentPassword);

        user.reauthenticate(credential)
                .addOnSuccessListener(aVoid -> {
                    // 2. Doğrulama başarılı - şimdi yeni şifreyi set et
                    user.updatePassword(newPassword)
                            .addOnSuccessListener(aVoid2 -> future.complete(null))
                            .addOnFailureListener(future::completeExceptionally);
                })
                .addOnFailureListener(e -> {
                    // Eski şifre yanlış veya başka bir hata
                    future.completeExceptionally(e);
                });

        return future;
    }


}
