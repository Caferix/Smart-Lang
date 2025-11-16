package com.scu.smartlang.presentation.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.scu.smartlang.domain.usecase.user.GetCurrentUserProfileUseCase;
import com.scu.smartlang.domain.usecase.user.LoginUserUseCase;
import com.scu.smartlang.domain.usecase.user.RegisterUserUseCase;
import com.scu.smartlang.domain.usecase.user.ResendVerificationEmailUseCase;
import com.scu.smartlang.domain.usecase.user.SignOutUserUseCase;
import com.scu.smartlang.presentation.ui.auth.AuthResultState;
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
            SignOutUserUseCase signOutUserUseCase, // Inject
            ResendVerificationEmailUseCase resendVerificationEmailUseCase
    ) {
        this.registerUserUseCase = registerUserUseCase;
        this.loginUserUseCase = loginUserUseCase;
        this.getCurrentUserProfileUseCase = getCurrentUserProfileUseCase;
        this.signOutUserUseCase = signOutUserUseCase;
        this.resendVerificationEmailUseCase = resendVerificationEmailUseCase;
    }

     // Kayıt işlemini başlatır.

    public void registerUser(String email, String password, String userName) {
        _authResult.setValue(new AuthResultState.Loading());

        registerUserUseCase.execute(email, password, userName)
                .thenAccept(user -> {
                    // Kayıt başarılı ama giriş yapılmadı
                    // kullanıcıya doğrulama gerektiğini söyle
                    _authResult.postValue(new AuthResultState.EmailNotVerified());
                })
                .exceptionally(throwable -> {
                    // Throwable'ın asıl sebebini (cause) al
                    Throwable cause = getRootCause(throwable);
                    _authResult.postValue(new AuthResultState.Error(cause.getLocalizedMessage()));
                    return null;
                });
    }


     // Giriş işlemini başlat.
    public void loginUser(String email, String password) {
        _authResult.setValue(new AuthResultState.Loading());

        loginUserUseCase.execute(email, password)
                .thenAccept(user -> _authResult.postValue(new AuthResultState.Success(user)))
                .exceptionally(throwable -> {
                    // HATA YÖNETİMİNİ GÜNCELLE

                    // CompletableFuture'un fırlattığı asıl hatayı al
                    Throwable cause = getRootCause(throwable);

                    // "Email is not verified" hatasını kontrol et
                    if (cause instanceof IllegalStateException &&
                            "Email is not verified".equals(cause.getMessage())) {

                        // yeni state i gönder
                        _authResult.postValue(new AuthResultState.EmailNotVerified());

                    } else {
                        // Diğer hatalar (yanlış şifre vs.)
                        _authResult.postValue(new AuthResultState.Error(cause.getLocalizedMessage()));
                    }
                    return null;
                });
    }

     // Oturum açmış kullanıcının profilini çeker.
    public void fetchUserProfile() {
        _userProfile.setValue(new AuthResultState.Loading());

        getCurrentUserProfileUseCase.execute()
                .thenAccept(user -> {
                    if (user != null) {
                        _userProfile.postValue(new AuthResultState.Success(user));
                    } else {
                        // Kullanıcı bulunamazsa hata
                        _userProfile.postValue(new AuthResultState.Error("Kullanıcı profili bulunamadı."));
                    }
                })
                .exceptionally(throwable -> {
                    _userProfile.postValue(new AuthResultState.Error(throwable.getLocalizedMessage()));
                    return null;
                });
    }


     // Kullanıcının oturumunu kapatır.
     // SettingsFragment'ta doğrudan çağrılacak.

    public void signOut() {
        // UI'da (SettingsFragment) sonucu gözlemlemeyeceğimiz için sadece işlemi çağırıyoruz.
        // Başarısızlık durumunda loglama veya ek bir hata yönetimi eklenebilir.
        signOutUserUseCase.execute()
                .exceptionally(throwable -> {
                    // Hata durumunu yönetimi
                    System.err.println("Sign out error: " + getRootCause(throwable).getLocalizedMessage());
                    return null;
                });
    }

    public void resendVerificationEmail() {
        resendVerificationEmailUseCase.execute()
                .thenAccept(aVoid -> {
                    // UI'a e-postanın gönderildiğini bildir
                    _authResult.postValue(new AuthResultState.ResendEmailSuccess());
                })
                .exceptionally(throwable -> {
                    // Hata durumunu yönet
                    Throwable cause = getRootCause(throwable);
                    _authResult.postValue(new AuthResultState.Error(cause.getLocalizedMessage()));
                    return null;
                });
    }

    private Throwable getRootCause(Throwable throwable) {
        if (throwable instanceof CancellationException || throwable.getCause() == null) {
            return throwable;
        }
        return throwable.getCause();
    }
}