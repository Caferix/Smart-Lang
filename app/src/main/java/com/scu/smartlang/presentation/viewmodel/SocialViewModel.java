package com.scu.smartlang.presentation.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.scu.smartlang.domain.model.Friend;
import com.scu.smartlang.domain.model.FriendRequest;
import com.scu.smartlang.domain.model.User;
import com.scu.smartlang.domain.repository.AuthRepository;
import com.scu.smartlang.domain.repository.SocialRepository;
import com.scu.smartlang.domain.usecase.social.AcceptFriendRequestUseCase;
import com.scu.smartlang.domain.usecase.social.GetFriendRequestsUseCase;
import com.scu.smartlang.domain.usecase.social.GetFriendsUseCase;
import com.scu.smartlang.domain.usecase.social.GetUserByIdUseCase;
import com.scu.smartlang.domain.usecase.social.RejectFriendRequestUseCase;
import com.scu.smartlang.domain.usecase.social.RemoveFriendUseCase;
import com.scu.smartlang.domain.usecase.social.SendFriendRequestUseCase;
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
    private final RemoveFriendUseCase removeFriendUseCase;
    private final RejectFriendRequestUseCase rejectFriendRequestUseCase;

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

    // UI'ı reaktif olarak güncellemek için kullanılacak LiveData
    private final MutableLiveData<Void> _friendshipActionCompleted = new MutableLiveData<>();
    public LiveData<Void> getFriendshipActionCompleted() {
        return _friendshipActionCompleted;
    }

    @Inject
    public SocialViewModel(SocialRepository socialRepository, AuthRepository authRepository,
                           GetFriendsUseCase getFriendsUseCase, GetFriendRequestsUseCase getFriendRequestsUseCase,
                           SendFriendRequestUseCase sendFriendRequestUseCase, AcceptFriendRequestUseCase acceptFriendRequestUseCase,
                           GetUserByIdUseCase getUserByIdUseCase, RemoveFriendUseCase removeFriendUseCase,
                           RejectFriendRequestUseCase rejectFriendRequestUseCase) {
        this.socialRepository = socialRepository;
        this.authRepository = authRepository;
        this.getFriendsUseCase = getFriendsUseCase;
        this.getFriendRequestsUseCase = getFriendRequestsUseCase;
        this.sendFriendRequestUseCase = sendFriendRequestUseCase;
        this.acceptFriendRequestUseCase = acceptFriendRequestUseCase;
        this.getUserByIdUseCase = getUserByIdUseCase;
        this.removeFriendUseCase = removeFriendUseCase;
        this.rejectFriendRequestUseCase = rejectFriendRequestUseCase;
    }

    public void fetchFriends(String uid) {
        getFriendsUseCase.execute(uid).thenAccept(_friends::postValue);
    }

    public void fetchIncomingRequests(String uid) {
        getFriendRequestsUseCase.execute(uid).thenAccept(_incomingRequests::postValue);
    }

    public void sendFriendRequest(String fromUid, String toUid) {
        sendFriendRequestUseCase.execute(fromUid, toUid)
                .thenRun(() -> _friendshipActionCompleted.postValue(null));
    }

    public void acceptFriendRequest(String requestId, String acceptorUid, String requesterUid) {
        acceptFriendRequestUseCase.execute(requestId, acceptorUid, requesterUid)
                .thenRun(() -> _friendshipActionCompleted.postValue(null));
    }

    public void rejectFriendRequest(String requestId, String recipientUid) {
        rejectFriendRequestUseCase.execute(requestId, recipientUid)
                .thenRun(() -> _friendshipActionCompleted.postValue(null));
    }

    public void removeFriend(String currentUserId, String friendToRemoveId) {
        removeFriendUseCase.execute(currentUserId, friendToRemoveId)
                .thenRun(() -> _friendshipActionCompleted.postValue(null));
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
