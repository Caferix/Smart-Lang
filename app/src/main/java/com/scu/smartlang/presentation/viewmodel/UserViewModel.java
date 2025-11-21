package com.scu.smartlang.presentation.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.FirebaseFirestore;
import com.scu.smartlang.domain.model.Friend;
import com.scu.smartlang.domain.model.FriendRequest;
import com.scu.smartlang.domain.model.User;
import com.scu.smartlang.domain.repository.FirebaseRepository; // Repository'yi kullanacağız
import com.scu.smartlang.domain.usecase.user.AcceptFriendRequestUseCase;
import com.scu.smartlang.domain.usecase.user.GetCurrentUserProfileUseCase;
import com.scu.smartlang.domain.usecase.user.GetFriendRequestsUseCase;
import com.scu.smartlang.domain.usecase.user.GetFriendsUseCase;
import com.scu.smartlang.domain.usecase.user.GetUserByIdUseCase;
import com.scu.smartlang.domain.usecase.user.LoginUserUseCase;
import com.scu.smartlang.domain.usecase.user.RegisterUserUseCase;
import com.scu.smartlang.domain.usecase.user.ResendVerificationEmailUseCase;
import com.scu.smartlang.domain.usecase.user.SendFriendRequestUseCase;
import com.scu.smartlang.domain.usecase.user.SignOutUserUseCase;
import com.scu.smartlang.presentation.ui.auth.AuthResultState;

import java.util.List;
import java.util.concurrent.CancellationException;

import javax.inject.Inject;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class UserViewModel extends ViewModel {

    private final RegisterUserUseCase registerUserUseCase;
    private final LoginUserUseCase loginUserUseCase;
    private final GetCurrentUserProfileUseCase getCurrentUserProfileUseCase;
    private final SignOutUserUseCase signOutUserUseCase;
    private final ResendVerificationEmailUseCase resendVerificationEmailUseCase;
    private final FirebaseRepository firebaseRepository;
    private final MutableLiveData<AuthResultState> _authResult = new MutableLiveData<>();
    public LiveData<AuthResultState> getAuthResult() { return _authResult; }
    private final MutableLiveData<AuthResultState> _userProfile = new MutableLiveData<>();
    public LiveData<AuthResultState> getUserProfile() { return _userProfile; }
    private final GetFriendsUseCase getFriendsUseCase;
    private final GetFriendRequestsUseCase getFriendRequestsUseCase;
    private final SendFriendRequestUseCase sendFriendRequestUseCase;
    private final AcceptFriendRequestUseCase acceptFriendRequestUseCase;
    private final MutableLiveData<List<Friend>> friends = new MutableLiveData<>();
    private final MutableLiveData<List<FriendRequest>> incomingRequests = new MutableLiveData<>();
    private final GetUserByIdUseCase getUserByIdUseCase;
    private final MutableLiveData<User> viewedUser = new MutableLiveData<>();
    private final MutableLiveData<List<User>> searchResults = new MutableLiveData<>();
    @Inject
    public UserViewModel(
            FirebaseRepository firebaseRepository, // Inject ediyoruz
            RegisterUserUseCase registerUserUseCase,
            LoginUserUseCase loginUserUseCase,
            GetCurrentUserProfileUseCase getCurrentUserProfileUseCase,
            SignOutUserUseCase signOutUserUseCase,
            ResendVerificationEmailUseCase resendVerificationEmailUseCase,
            GetFriendsUseCase getFriendsUseCase,
            GetFriendRequestsUseCase getFriendRequestsUseCase,
            SendFriendRequestUseCase sendFriendRequestUseCase,
            AcceptFriendRequestUseCase acceptFriendRequestUseCase,
            GetUserByIdUseCase getUserByIdUseCase

    ) {
        this.firebaseRepository = firebaseRepository;
        this.registerUserUseCase = registerUserUseCase;
        this.loginUserUseCase = loginUserUseCase;
        this.getCurrentUserProfileUseCase = getCurrentUserProfileUseCase;
        this.signOutUserUseCase = signOutUserUseCase;
        this.resendVerificationEmailUseCase = resendVerificationEmailUseCase;
        this.getFriendsUseCase = getFriendsUseCase;
        this.getFriendRequestsUseCase = getFriendRequestsUseCase;
        this.sendFriendRequestUseCase = sendFriendRequestUseCase;
        this.acceptFriendRequestUseCase = acceptFriendRequestUseCase;
        this.getUserByIdUseCase = getUserByIdUseCase;
    }

    public void registerUser(String email, String password, String userName) {
        _authResult.setValue(new AuthResultState.Loading());
        registerUserUseCase.execute(email, password, userName)
                .thenAccept(user -> signOutUserUseCase.execute()
                        .thenRun(() -> _authResult.postValue(new AuthResultState.EmailNotVerified()))
                        .exceptionally(e -> {
                            _authResult.postValue(new AuthResultState.EmailNotVerified());
                            return null;
                        }))
                .exceptionally(throwable -> {
                    _authResult.postValue(new AuthResultState.Error(getRootCause(throwable).getLocalizedMessage()));
                    return null;
                });
    }

    public void loginUser(String email, String password) {
        _authResult.setValue(new AuthResultState.Loading());
        loginUserUseCase.execute(email, password)
                .thenAccept(basicUser -> getCurrentUserProfileUseCase.execute()
                        .thenAccept(fullUser -> {
                            if (fullUser != null) {
                                _userProfile.postValue(new AuthResultState.Success(fullUser));
                                _authResult.postValue(new AuthResultState.Success(fullUser));
                            } else {
                                _authResult.postValue(new AuthResultState.Error("Profil bulunamadı."));
                            }
                        }))
                .exceptionally(throwable -> {
                    Throwable cause = getRootCause(throwable);
                    if (cause instanceof IllegalStateException && "Email is not verified".equals(cause.getMessage())) {
                        _authResult.postValue(new AuthResultState.EmailNotVerified());
                    } else {
                        _authResult.postValue(new AuthResultState.Error(cause.getLocalizedMessage()));
                    }
                    return null;
                });
    }

    public void fetchUserProfile() {
        _userProfile.setValue(new AuthResultState.Loading());
        getCurrentUserProfileUseCase.execute()
                .thenAccept(user -> {
                    if (user != null) {
                        _userProfile.postValue(new AuthResultState.Success(user));
                    } else {
                        _userProfile.postValue(new AuthResultState.SignedOut());
                    }
                })
                .exceptionally(throwable -> {
                    _userProfile.postValue(new AuthResultState.SignedOut());
                    return null;
                });
    }

    // --- EKSİK OLAN GÜNCELLEME METOTLARI ---

    // 1. Profil (İsim vb.) Güncelleme
    public void updateUserProfile(User user) {
        firebaseRepository.updateUserProfile(user)
                .thenRun(() -> {
                    // Başarılı olursa yerel veriyi güncelle
                    _userProfile.postValue(new AuthResultState.Success(user));
                })
                .exceptionally(throwable -> {
                    _authResult.postValue(new AuthResultState.Error("Güncelleme hatası: " + throwable.getMessage()));
                    return null;
                });
    }

    // 2. Oyun İlerlemesi (XP/Level) Güncelleme
    public void updateUserProgress(int newLevel, int xpInNewLevel) {
        // Mevcut kullanıcıyı al
        AuthResultState state = _userProfile.getValue();
        if (state instanceof AuthResultState.Success) {
            User currentUser = ((AuthResultState.Success) state).getUser();
            if (currentUser != null) {
                currentUser.setLevel(newLevel);
                currentUser.setXp(xpInNewLevel);

                // Repository üzerinden güncelle (Clean Architecture)
                updateUserProfile(currentUser);
            }
        }
    }
    public void updateUserName(String newName) {
        // Mevcut kullanıcıyı al
        AuthResultState state = _userProfile.getValue();
        if (state instanceof AuthResultState.Success) {
            User currentUser = ((AuthResultState.Success) state).getUser();
            if (currentUser != null) {
                currentUser.setUserName(newName);

                // Repository üzerinden güncelleme
                firebaseRepository.updateUserProfile(currentUser)
                        .thenRun(() -> {
                            // Başarılı olursa yerel veriyi ve UI'ı güncelle
                            _userProfile.postValue(new AuthResultState.Success(currentUser));
                        })
                        .exceptionally(throwable -> {
                            _authResult.postValue(new AuthResultState.Error("İsim güncellenemedi: " + throwable.getMessage()));
                            return null;
                        });
            }
        }
    }

    public void signOut() {
        signOutUserUseCase.execute();
    }

    public void resendVerificationEmail() {
        resendVerificationEmailUseCase.execute()
                .thenAccept(aVoid -> _authResult.postValue(new AuthResultState.ResendEmailSuccess()));
    }

    public void clearAuthResultState() {
        _authResult.setValue(null);
    }

    public LiveData<List<Friend>> getFriends() { return friends; }
    public LiveData<List<FriendRequest>> getIncomingRequests() { return incomingRequests; }

    public void fetchFriends(String uid) {
        getFriendsUseCase.execute(uid).thenAccept(friends::postValue);
    }

    public void fetchIncomingRequests(String uid) {
        getFriendRequestsUseCase.execute(uid).thenAccept(incomingRequests::postValue);
    }

    public void sendFriendRequest(String fromUid, String toUid) {
        sendFriendRequestUseCase.execute(fromUid, toUid);
    }

    public void acceptFriendRequest(String requestId, String acceptorUid) {
        acceptFriendRequestUseCase.execute(requestId, acceptorUid)
                .thenRun(() -> {
                    // refresh lists after acceptance
                    fetchFriends(acceptorUid);
                    fetchIncomingRequests(acceptorUid);
                });
    }

    public LiveData<User> getViewedUser() {
        return viewedUser;
    }

    public void fetchUserById(String userId) {
        getUserByIdUseCase.execute(userId).thenAccept(user -> {
            if (user != null) {
                viewedUser.postValue(user);
            }
        }).exceptionally(throwable -> {
            Log.e("UserViewModel", "fetchUserById failed", throwable);
            return null;
        });
    }

    public LiveData<List<User>> getSearchResults() {
        return searchResults;
    }

    public void searchUsers(String query) {
        firebaseRepository.searchUsersByName(query).thenAccept(users -> {
            searchResults.postValue(users);
        });
    }

    public void clearNotificationBadge() {
        firebaseRepository.getCurrentUserId().thenAccept(uid -> {
            if (uid != null) {
                FirebaseFirestore.getInstance()
                        .collection("users")
                        .document(uid)
                        .update("unreadNotifications", 0)
                        .addOnFailureListener(e ->
                                android.util.Log.e("UserViewModel", "Badge sıfırlanamadı", e));
            }
        });
    }
    private Throwable getRootCause(Throwable throwable) {
        if (throwable instanceof CancellationException || throwable.getCause() == null) {
            return throwable;
        }
        return throwable.getCause();
    }
}