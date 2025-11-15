package com.scu.smartlang.presentation.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.scu.smartlang.domain.usecase.user.GetCurrentUserProfileUseCase;
import com.scu.smartlang.domain.usecase.user.LoginUserUseCase;
import com.scu.smartlang.domain.usecase.user.RegisterUserUseCase;
import com.scu.smartlang.domain.usecase.user.SignOutUserUseCase; // Yeni UseCase
import com.scu.smartlang.presentation.ui.auth.AuthResultState;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class UserViewModel extends ViewModel {

    private final RegisterUserUseCase registerUserUseCase;
    private final LoginUserUseCase loginUserUseCase;
    private final GetCurrentUserProfileUseCase getCurrentUserProfileUseCase; // Yeni UseCase
    private final SignOutUserUseCase signOutUserUseCase; // Yeni UseCase

    // --- LiveData ---

    // Giriş/Kayıt işlemleri için LiveData
    private final MutableLiveData<AuthResultState> _authResult = new MutableLiveData<>();
    public LiveData<AuthResultState> getAuthResult() {
        return _authResult;
    }

    // Kullanıcı Profili (Home Fragment için) LiveData
    private final MutableLiveData<AuthResultState> _userProfile = new MutableLiveData<>();
    public LiveData<AuthResultState> getUserProfile() {
        return _userProfile;
    }


    @Inject
    public UserViewModel(
            RegisterUserUseCase registerUserUseCase,
            LoginUserUseCase loginUserUseCase,
            GetCurrentUserProfileUseCase getCurrentUserProfileUseCase, // Inject
            SignOutUserUseCase signOutUserUseCase // Inject
    ) {
        this.registerUserUseCase = registerUserUseCase;
        this.loginUserUseCase = loginUserUseCase;
        this.getCurrentUserProfileUseCase = getCurrentUserProfileUseCase; // Ata
        this.signOutUserUseCase = signOutUserUseCase; // Ata
    }

    /**
     * Kayıt işlemini başlatır.
     */
    public void registerUser(String email, String password, String userName) {
        _authResult.setValue(new AuthResultState.Loading());

        registerUserUseCase.execute(email, password, userName)
                .thenAccept(user -> _authResult.postValue(new AuthResultState.Success(user)))
                .exceptionally(throwable -> {
                    _authResult.postValue(new AuthResultState.Error(throwable.getLocalizedMessage()));
                    return null;
                });
    }

    /**
     * Giriş işlemini başlatır.
     */
    public void loginUser(String email, String password) {
        _authResult.setValue(new AuthResultState.Loading());

        loginUserUseCase.execute(email, password)
                .thenAccept(user -> _authResult.postValue(new AuthResultState.Success(user)))
                .exceptionally(throwable -> {
                    _authResult.postValue(new AuthResultState.Error(throwable.getLocalizedMessage()));
                    return null;
                });
    }


    /**
     * Oturum açmış kullanıcının profilini çeker.
     */
    public void fetchUserProfile() {
        _userProfile.setValue(new AuthResultState.Loading());

        getCurrentUserProfileUseCase.execute()
                .thenAccept(user -> {
                    if (user != null) {
                        _userProfile.postValue(new AuthResultState.Success(user));
                    } else {
                        // Kullanıcı bulunamazsa hata durumuna düşür
                        _userProfile.postValue(new AuthResultState.Error("Kullanıcı profili bulunamadı."));
                    }
                })
                .exceptionally(throwable -> {
                    _userProfile.postValue(new AuthResultState.Error(throwable.getLocalizedMessage()));
                    return null;
                });
    }

    /**
     * Kullanıcının oturumunu kapatır.
     * SettingsFragment'ta doğrudan çağrılacaktır.
     */
    public void signOut() {
        // UI'da (SettingsFragment) sonucu gözlemlemeyeceğimiz için sadece işlemi çağırıyoruz.
        // Başarısızlık durumunda loglama veya ek bir hata yönetimi eklenebilir.
        signOutUserUseCase.execute()
                .exceptionally(throwable -> {
                    // Hata durumunu yönet (Örn: Loglama)
                    System.err.println("Sign out error: " + throwable.getLocalizedMessage());
                    return null;
                });
    }
}