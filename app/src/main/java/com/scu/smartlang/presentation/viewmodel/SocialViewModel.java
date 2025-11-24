package com.scu.smartlang.presentation.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.scu.smartlang.domain.model.Friend;
import com.scu.smartlang.domain.model.FriendRequest;
import com.scu.smartlang.domain.model.User;
import com.scu.smartlang.domain.repository.AuthRepository;
import com.scu.smartlang.domain.repository.SocialRepository;
import com.scu.smartlang.domain.usecase.user.AcceptFriendRequestUseCase;
import com.scu.smartlang.domain.usecase.user.GetFriendRequestsUseCase;
import com.scu.smartlang.domain.usecase.user.GetFriendsUseCase;
import com.scu.smartlang.domain.usecase.user.GetUserByIdUseCase;
import com.scu.smartlang.domain.usecase.user.SendFriendRequestUseCase;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import javax.inject.Inject;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class SocialViewModel extends ViewModel {

    private final SocialRepository socialRepository;
    private final AuthRepository authRepository;
    private final GetFriendsUseCase getFriendsUseCase;
    private final GetFriendRequestsUseCase getFriendRequestsUseCase;
    private final SendFriendRequestUseCase sendFriendRequestUseCase;
    private final AcceptFriendRequestUseCase acceptFriendRequestUseCase;
    private final GetUserByIdUseCase getUserByIdUseCase;

    private final MutableLiveData<List<Friend>> _friends = new MutableLiveData<>();
    public LiveData<List<Friend>> getFriends() { return _friends; }

    private final MutableLiveData<List<FriendRequest>> _incomingRequests = new MutableLiveData<>();
    public LiveData<List<FriendRequest>> getIncomingRequests() { return _incomingRequests; }

    private final MutableLiveData<User> _viewedUser = new MutableLiveData<>();
    public LiveData<User> getViewedUser() { return _viewedUser; }

    private final MutableLiveData<List<User>> _searchResults = new MutableLiveData<>();
    public LiveData<List<User>> getSearchResults() { return _searchResults; }

    private final MutableLiveData<String> _friendshipStatus = new MutableLiveData<>();
    public LiveData<String> getFriendshipStatus() { return _friendshipStatus; }

    @Inject
    public SocialViewModel(SocialRepository socialRepository, AuthRepository authRepository, GetFriendsUseCase getFriendsUseCase, GetFriendRequestsUseCase getFriendRequestsUseCase, SendFriendRequestUseCase sendFriendRequestUseCase, AcceptFriendRequestUseCase acceptFriendRequestUseCase, GetUserByIdUseCase getUserByIdUseCase) {
        this.socialRepository = socialRepository;
        this.authRepository = authRepository;
        this.getFriendsUseCase = getFriendsUseCase;
        this.getFriendRequestsUseCase = getFriendRequestsUseCase;
        this.sendFriendRequestUseCase = sendFriendRequestUseCase;
        this.acceptFriendRequestUseCase = acceptFriendRequestUseCase;
        this.getUserByIdUseCase = getUserByIdUseCase;
    }

    public void fetchFriends(String uid) {
        getFriendsUseCase.execute(uid).thenAccept(_friends::postValue);
    }

    public void fetchIncomingRequests(String uid) {
        getFriendRequestsUseCase.execute(uid).thenAccept(_incomingRequests::postValue);
    }

    public CompletableFuture<Void> sendFriendRequest(String fromUid, String toUid) {
        return sendFriendRequestUseCase.execute(fromUid, toUid)
                .thenRun(() -> checkFriendshipStatus(fromUid, toUid)); // Durumu güncelle
    }

    public void acceptFriendRequest(String requestId, String acceptorUid) {
        acceptFriendRequestUseCase.execute(requestId, acceptorUid)
                .thenRun(() -> fetchFriends(acceptorUid)); // Arkadaş listesini yenile
    }

    public void checkFriendshipStatus(String currentUid, String otherUid) {
        socialRepository.checkFriendshipStatus(currentUid, otherUid)
                .thenAccept(_friendshipStatus::postValue);
    }

    public void fetchUserById(String userId) {
        getUserByIdUseCase.execute(userId).thenAccept(_viewedUser::postValue);
    }

    public void searchUsers(String query) {
        if (query == null || query.trim().isEmpty()) {
            _searchResults.postValue(new ArrayList<>());
            return;
        }
        socialRepository.searchUsersByName(query).thenAccept(_searchResults::postValue);
    }

    public CompletableFuture<String> getCurrentUserId() {
        return authRepository.getCurrentUserId();
    }

    public void clearSocialData() {
        _friends.postValue(new ArrayList<>());
        _incomingRequests.postValue(new ArrayList<>());
        _viewedUser.postValue(null);
        _searchResults.postValue(new ArrayList<>());
        _friendshipStatus.postValue(null);
    }
}
