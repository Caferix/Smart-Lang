package com.scu.smartlang.presentation.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.scu.smartlang.domain.model.User;
import com.scu.smartlang.domain.repository.AuthRepository;
import com.scu.smartlang.domain.usecase.social.GetCurrentUserProfileUseCase;
import com.scu.smartlang.domain.usecase.user.GetUnreadNotificationsCountUseCase;
import com.scu.smartlang.domain.usecase.user.UpdateUserProfileUseCase;
import com.scu.smartlang.presentation.ui.auth.AuthResultState;
import java.util.concurrent.CompletableFuture;
import javax.inject.Inject;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class ProfileViewModel extends ViewModel {

    private final GetCurrentUserProfileUseCase getCurrentUserProfileUseCase;
    private final UpdateUserProfileUseCase updateUserProfileUseCase;
    private final GetUnreadNotificationsCountUseCase getUnreadNotificationsCountUseCase;
    private final AuthRepository authRepository;

    private final MutableLiveData<AuthResultState> _userProfile = new MutableLiveData<>();
    public LiveData<AuthResultState> getUserProfile() { return _userProfile; }

    private final MutableLiveData<Integer> _unreadNotifications = new MutableLiveData<>();
    public LiveData<Integer> getUnreadNotifications() { return _unreadNotifications; }

    @Inject
    public ProfileViewModel(
            GetCurrentUserProfileUseCase getCurrentUserProfileUseCase,
            UpdateUserProfileUseCase updateUserProfileUseCase,
            GetUnreadNotificationsCountUseCase getUnreadNotificationsCountUseCase,
            AuthRepository authRepository) {
        this.getCurrentUserProfileUseCase = getCurrentUserProfileUseCase;
        this.updateUserProfileUseCase = updateUserProfileUseCase;
        this.getUnreadNotificationsCountUseCase = getUnreadNotificationsCountUseCase;
        this.authRepository = authRepository;
    }

    public void fetchUserProfile() {
        _userProfile.setValue(new AuthResultState.Loading());
        getCurrentUserProfileUseCase.execute()
                .whenComplete((user, throwable) -> {
                    if (throwable != null) {
                        _userProfile.postValue(new AuthResultState.Error(throwable.getLocalizedMessage()));
                    } else {
                        _userProfile.postValue(new AuthResultState.Success(user));
                    }
                });
    }

    public CompletableFuture<Void> updateUserProfile(User user) {
        return updateUserProfileUseCase.execute(user);
    }

    public void fetchUnreadNotificationsCount() {
        authRepository.getCurrentUserId().thenCompose(uid -> {
            if (uid != null) {
                return getUnreadNotificationsCountUseCase.execute(uid);
            }
            CompletableFuture<Integer> future = new CompletableFuture<>();
            future.complete(0);
            return future;
        }).thenAccept(_unreadNotifications::postValue);
    }

    public void clearProfileData() {
        _userProfile.postValue(null);
    }
}
