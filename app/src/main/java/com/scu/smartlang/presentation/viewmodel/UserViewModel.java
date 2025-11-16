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
            GetCurrentUserProfileUseCase getCurrentUserProfileUseCase,
            SignOutUserUseCase signOutUserUseCase,
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
                    // Kayıt başarılı, hemen signOut yap (unverified olduğu için).
                    signOutUserUseCase.execute()
                            .thenRun(() -> {
                                // Sonra doğrulama gerektiğini söyle.
                                _authResult.postValue(new AuthResultState.EmailNotVerified());
                            })
                            .exceptionally(e -> {
                                // SignOut hatası olursa logla ama devam et.
                                System.err.println("Register signOut error: " + getRootCause(e).getLocalizedMessage());
                                _authResult.postValue(new AuthResultState.EmailNotVerified());
                                return null;
                            });
                })
                .exceptionally(throwable -> {
                    Throwable cause = getRootCause(throwable);
                    _authResult.postValue(new AuthResultState.Error(cause.getLocalizedMessage()));
                    return null;
                });
    }

    // Giriş işlemini başlat.
    public void loginUser(String email, String password) {
        _authResult.setValue(new AuthResultState.Loading());

        loginUserUseCase.execute(email, password)
                .thenAccept(basicUser -> {
                    // Adım 1: Login başarılı. Şimdi tam profil verilerini çekiyoruz.
                    getCurrentUserProfileUseCase.execute()
                            .thenAccept(fullUser -> {
                                // Adım 2: Tam profil verisi (userName dahil) çekildi.
                                if (fullUser != null) {
                                    // HomeFragment'ın beklediği _userProfile LiveData'sını güncelle
                                    _userProfile.postValue(new AuthResultState.Success(fullUser));
                                    // SignInFragment'ın navigasyon için beklediği _authResult'ı güncelle
                                    _authResult.postValue(new AuthResultState.Success(fullUser));
                                } else {
                                    // Eğer login başarılı olduysa ama profil çekilemezse (nadir)
                                    _authResult.postValue(new AuthResultState.Error("Giriş başarılı ancak profil verisi bulunamadı."));
                                }
                            })
                            .exceptionally(profileFetchError -> {
                                // Profil verisi çekme sırasında hata
                                Throwable cause = getRootCause(profileFetchError);
                                _authResult.postValue(new AuthResultState.Error("Giriş başarılı ama profil yükleme hatası: " + cause.getLocalizedMessage()));
                                return null;
                            });
                })
                .exceptionally(throwable -> {
                    // Orijinal login hatası (kullanıcı adı/şifre yanlış vb.)
                    Throwable cause = getRootCause(throwable);
                    // E-posta Doğrulanmadı kontrolü
                    if (cause instanceof IllegalStateException && "Email is not verified".equals(cause.getMessage())) {
                        _authResult.postValue(new AuthResultState.EmailNotVerified());
                    } else {
                        _authResult.postValue(new AuthResultState.Error(cause.getLocalizedMessage()));
                    }
                    return null;
                });
    }


       // Oturum açmış kullanıcının profilini çeker.
      // Uygulama başlangıcında e-posta doğrulama kontrolü burada yapılır.
    public void fetchUserProfile() {
        _userProfile.setValue(new AuthResultState.Loading());

        getCurrentUserProfileUseCase.execute()
                .thenAccept(user -> {
                    if (user != null) {
                        // User modeli, FirebaseUser dan isEmailVerified durumunu çekip getirmelidir.
                        if (user.isEmailVerified()) {
                            // Oturum var ve mail doğrulanmış. Ana sayfaya yönlendir.
                            _userProfile.postValue(new AuthResultState.Success(user));
                        } else {
                            // Oturum var ama mail doğrulanmamış.
                            // 1. Firebase'in kalıcı oturumunu sonlandır (force signOut).
                            signOutUserUseCase.execute()
                                    .thenRun(() -> {
                                        // 2. UI'a kullanıcının doğrulanmadığını bildir.
                                        // SignInFragment'ın formu göstermesini sağlar.
                                        _userProfile.postValue(new AuthResultState.EmailNotVerified());
                                    })
                                    .exceptionally(e -> {
                                        // Oturumu kapatma başarısız olsa bile kullanıcıyı bilgilendir.
                                        System.err.println("Unverified user sign out error: " + getRootCause(e).getLocalizedMessage());
                                        // Hata gönderme yerine, yine de doğrulanmama durumunu gönderiyoruz.
                                        _userProfile.postValue(new AuthResultState.EmailNotVerified());
                                        return null;
                                    });
                        }
                    } else {
                        // Kullanıcı bulunamazsa (oturum yok)
                        _userProfile.postValue(new AuthResultState.SignedOut());
                    }
                })
                .exceptionally(throwable -> {
                    // Oturum kontrolü sırasında bir hata oluşursa, oturum yok kabul et.
                    _userProfile.postValue(new AuthResultState.SignedOut());
                    return null;
                });
    }


    // Kullanıcının oturumunu kapatır.
    public void signOut() {
        signOutUserUseCase.execute()
                .exceptionally(throwable -> {
                    System.err.println("Sign out error: " + getRootCause(throwable).getLocalizedMessage());
                    return null;
                });
    }

    public void resendVerificationEmail() {
        _authResult.setValue(new AuthResultState.Loading());

        resendVerificationEmailUseCase.execute()
                .thenAccept(aVoid -> {
                    _authResult.postValue(new AuthResultState.ResendEmailSuccess());
                })
                .exceptionally(throwable -> {
                    Throwable cause = getRootCause(throwable);
                    _authResult.postValue(new AuthResultState.Error(cause.getLocalizedMessage()));
                    return null;
                });
    }

    public void clearAuthResultState() {
        // null olarak ayarlamak, yeni Fragment yüklendiğinde eski sonucun (örneğin EmailNotVerified)
        // tekrar tetiklenmesini engeller.
        _authResult.setValue(null);
    }

    private Throwable getRootCause(Throwable throwable) {
        if (throwable instanceof CancellationException || throwable.getCause() == null) {
            return throwable;
        }
        return throwable.getCause();
    }
}