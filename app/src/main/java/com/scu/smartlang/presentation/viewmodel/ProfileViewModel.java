package com.scu.smartlang.presentation.viewmodel;

import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.Transformations;

import com.scu.smartlang.domain.model.User;
import com.scu.smartlang.domain.repository.AuthRepository;
import com.scu.smartlang.domain.usecase.social.GetCurrentUserProfileUseCase;
import com.scu.smartlang.domain.usecase.user.GetUnreadNotificationsCountUseCase;
import com.scu.smartlang.domain.usecase.user.ResetUnreadNotificationsCountUseCase;
import com.scu.smartlang.domain.usecase.user.UpdateUserProfileUseCase;
import com.scu.smartlang.domain.usecase.user.UploadProfileImageUseCase;
import com.scu.smartlang.presentation.ui.auth.AuthResultState;

import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class ProfileViewModel extends ViewModel {

    private final AuthRepository authRepository;
    private String currentUserId;
    private final GetCurrentUserProfileUseCase getCurrentUserProfileUseCase;
    private final GetUnreadNotificationsCountUseCase getUnreadNotificationsCountUseCase;
    private final ResetUnreadNotificationsCountUseCase resetUnreadNotificationsCountUseCase;
    private final UpdateUserProfileUseCase updateUserProfileUseCase;
    private final UploadProfileImageUseCase uploadProfileImageUseCase;

    private final MutableLiveData<AuthResultState> _userProfile = new MutableLiveData<>();
    public LiveData<AuthResultState> getUserProfile() { return _userProfile; }

    private LiveData<Integer> unreadNotificationsCount;


    @Inject
    public ProfileViewModel(AuthRepository authRepository,
                            GetCurrentUserProfileUseCase getCurrentUserProfileUseCase,
                            GetUnreadNotificationsCountUseCase getUnreadNotificationsCountUseCase,
                            ResetUnreadNotificationsCountUseCase resetUnreadNotificationsCountUseCase,
                            UpdateUserProfileUseCase updateUserProfileUseCase,
                            UploadProfileImageUseCase uploadProfileImageUseCase
                            ) {
        this.authRepository = authRepository;
        this.getCurrentUserProfileUseCase = getCurrentUserProfileUseCase;
        this.getUnreadNotificationsCountUseCase = getUnreadNotificationsCountUseCase;
        this.resetUnreadNotificationsCountUseCase = resetUnreadNotificationsCountUseCase;
        this.updateUserProfileUseCase = updateUserProfileUseCase;
        this.uploadProfileImageUseCase = uploadProfileImageUseCase;

        // Kullanıcı ID'si değiştiğinde, bildirim sayısını dinleyen LiveData'yı değiştir.
        unreadNotificationsCount = Transformations.switchMap(authRepository.getCurrentUserIdLiveData(), uid -> {
            if (uid != null) {
                return getUnreadNotificationsCountUseCase.execute(uid);
            }
            // Kullanıcı çıkış yapmışsa, bildirim sayısını 0 olarak ayarla.
            MutableLiveData<Integer> emptyData = new MutableLiveData<>();
            emptyData.setValue(0);
            return emptyData;
        });

        fetchUserProfile();

    }

    public void fetchUserProfile() {
        _userProfile.setValue(new AuthResultState.Loading());
        getCurrentUserProfileUseCase.execute().thenAccept(user -> {
            if (user != null) {
                _userProfile.postValue(new AuthResultState.Success(user));
                initializeNotificationObserver(user.getUid());
            } else {
                _userProfile.postValue(new AuthResultState.Error("User not found."));
            }
        }).exceptionally(throwable -> {
            _userProfile.postValue(new AuthResultState.Error(throwable.getMessage()));
            return null;
        });
    }

    private void initializeNotificationObserver(String uid) {
        unreadNotificationsCount = getUnreadNotificationsCountUseCase.execute(uid);
    }

    public void resetUnreadNotificationsCount() {
        authRepository.getCurrentUserId().thenAccept(uid -> {
            if (uid != null) {
                resetUnreadNotificationsCountUseCase.execute(uid);
            }
        });
    }

    public CompletableFuture<Void> updateUserProfile(User user) {
        return updateUserProfileUseCase.execute(user);
    }

    public CompletableFuture<String> uploadProfileImage(Uri imageUri) {
        return uploadProfileImageUseCase.execute(imageUri);
    }
}
