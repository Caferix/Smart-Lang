package com.scu.smartlang.presentation.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.scu.smartlang.domain.usecase.user.LoginUserUseCase;
import com.scu.smartlang.domain.usecase.user.RegisterUserUseCase;
import com.scu.smartlang.presentation.ui.auth.AuthResultState;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class UserViewModel extends ViewModel {

        private final RegisterUserUseCase registerUserUseCase;
        private final LoginUserUseCase loginUserUseCase;

        // --- LiveData ---
        // UI (Activity/Fragment) bu LiveData'yı gözlemleyecek (observe)

        // Değiştirilebilir (Mutable) versiyon, sadece ViewModel içinde kullanılır
        private final MutableLiveData<AuthResultState> _authResult = new MutableLiveData<>();

        // Değiştirilemez (Immutable) versiyon, UI'a bu sunulur
        public LiveData<AuthResultState> getAuthResult() {
            return _authResult;
        }

        @Inject
        public UserViewModel(
                RegisterUserUseCase registerUserUseCase,
                LoginUserUseCase loginUserUseCase
                // Diğer UseCase'ler buraya eklenebilir (örn: SignOutUseCase)
        ) {
            this.registerUserUseCase = registerUserUseCase;
            this.loginUserUseCase = loginUserUseCase;
        }

        /**
         * UI (RegisterActivity) tarafından çağrılacak metot.
         */
        public void registerUser(String email, String password, String userName) {
            // 1. UI'a "Yükleniyor" durumunu bildir
            _authResult.setValue(new AuthResultState.Loading());

            // 2. UseCase'i çağır
            registerUserUseCase.execute(email, password, userName)
                    .thenAccept(user -> {
                        // 3a. BAŞARILI: Arka planda çalışabilir, bu yüzden postValue() kullan
                        _authResult.postValue(new AuthResultState.Success(user));
                    })
                    .exceptionally(throwable -> {
                        // 3b. HATA: Arka planda çalışabilir, bu yüzden postValue() kullan
                        _authResult.postValue(new AuthResultState.Error(throwable.getLocalizedMessage()));
                        return null; // exceptionally bloğu null dönmeli
                    });
        }


          // UI (LoginFragment) tarafından çağrılacak metot.

        public void loginUser(String email, String password) {
            // 1. Yükleniyor (durum bildirimi)
            _authResult.setValue(new AuthResultState.Loading());

            // 2. UseCase'i çağır
            loginUserUseCase.execute(email, password)
                    .thenAccept(user -> {
                        // 3a. BAŞARILI
                        _authResult.postValue(new AuthResultState.Success(user));
                    })
                    .exceptionally(throwable -> {
                        // 3b. HATA
                        _authResult.postValue(new AuthResultState.Error(throwable.getLocalizedMessage()));
                        return null;
                    });
        }
}
