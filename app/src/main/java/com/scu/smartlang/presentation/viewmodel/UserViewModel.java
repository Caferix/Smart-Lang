// KODUN BAŞLANGICI
package com.scu.smartlang.presentation.viewmodel;

// --- Gerekli importlar ---
import android.util.Log; // -> DOĞRU Log sınıfını import ettik.
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;         // -> YENİ: Firebase Auth importu
import com.google.firebase.firestore.FirebaseFirestore; // -> YENİ: Firestore importu
import com.scu.smartlang.domain.usecase.user.GetCurrentUserProfileUseCase;
import com.scu.smartlang.domain.usecase.user.LoginUserUseCase;
import com.scu.smartlang.domain.usecase.user.RegisterUserUseCase;
import com.scu.smartlang.domain.usecase.user.ResendVerificationEmailUseCase;
import com.scu.smartlang.domain.usecase.user.SignOutUserUseCase;
import com.scu.smartlang.presentation.ui.auth.AuthResultState;

import java.util.HashMap; // -> YENİ: HashMap importu
import java.util.Map;
import java.util.concurrent.CancellationException;
import javax.inject.Inject;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class UserViewModel extends ViewModel {

    // --- UseCase'ler ---
    private final RegisterUserUseCase registerUserUseCase;
    private final LoginUserUseCase loginUserUseCase;
    private final GetCurrentUserProfileUseCase getCurrentUserProfileUseCase;
    private final SignOutUserUseCase signOutUserUseCase;
    private final ResendVerificationEmailUseCase resendVerificationEmailUseCase;

    // --- YENİ: Firebase nesneleri ---
    private final FirebaseAuth firebaseAuth;
    private final FirebaseFirestore db;
    // ---------------------------------

    // --- LiveData'lar ---
    private final MutableLiveData<AuthResultState> _authResult = new MutableLiveData<>();
    public LiveData<AuthResultState> getAuthResult() {
        return _authResult;
    }

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
            ResendVerificationEmailUseCase resendVerificationEmailUseCase,
            // --- YENİ: Firebase nesnelerini Hilt ile alıyoruz ---
            FirebaseAuth firebaseAuth,
            FirebaseFirestore db
            // ----------------------------------------------------
    ) {
        this.registerUserUseCase = registerUserUseCase;
        this.loginUserUseCase = loginUserUseCase;
        this.getCurrentUserProfileUseCase = getCurrentUserProfileUseCase;
        this.signOutUserUseCase = signOutUserUseCase;
        this.resendVerificationEmailUseCase = resendVerificationEmailUseCase;
        // --- YENİ: Gelen nesneleri sınıf değişkenlerine atıyoruz ---
        this.firebaseAuth = firebaseAuth;
        this.db = db;
        // -------------------------------------------------------
    }

    // Kayıt işlemini başlatır. (Bu metot aynı kalıyor)
    public void registerUser(String email, String password, String userName) {
        _authResult.setValue(new AuthResultState.Loading());
        registerUserUseCase.execute(email, password, userName)
                .thenAccept(user -> signOutUserUseCase.execute()
                        .thenRun(() -> _authResult.postValue(new AuthResultState.EmailNotVerified()))
                        .exceptionally(e -> {
                            System.err.println("Register signOut error: " + getRootCause(e).getLocalizedMessage());
                            _authResult.postValue(new AuthResultState.EmailNotVerified());
                            return null;
                        }))
                .exceptionally(throwable -> {
                    _authResult.postValue(new AuthResultState.Error(getRootCause(throwable).getLocalizedMessage()));
                    return null;
                });
    }

    // Giriş işlemini başlatır. (Bu metot aynı kalıyor)
    public void loginUser(String email, String password) {
        _authResult.setValue(new AuthResultState.Loading());
        loginUserUseCase.execute(email, password)
                .thenAccept(basicUser -> getCurrentUserProfileUseCase.execute()
                        .thenAccept(fullUser -> {
                            if (fullUser != null) {
                                _userProfile.postValue(new AuthResultState.Success(fullUser));
                                _authResult.postValue(new AuthResultState.Success(fullUser));
                            } else {
                                _authResult.postValue(new AuthResultState.Error("Giriş başarılı ancak profil verisi bulunamadı."));
                            }
                        })
                        .exceptionally(profileFetchError -> {
                            _authResult.postValue(new AuthResultState.Error("Giriş başarılı ama profil yükleme hatası: " + getRootCause(profileFetchError).getLocalizedMessage()));
                            return null;
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

    // Oturum açmış kullanıcının profilini çeker. (Bu metot aynı kalıyor)
    public void fetchUserProfile() {
        _userProfile.setValue(new AuthResultState.Loading());
        getCurrentUserProfileUseCase.execute()
                .thenAccept(user -> {
                    if (user != null) {
                        if (user.isEmailVerified()) {
                            _userProfile.postValue(new AuthResultState.Success(user));
                        } else {
                            signOutUserUseCase.execute()
                                    .thenRun(() -> _userProfile.postValue(new AuthResultState.EmailNotVerified()))
                                    .exceptionally(e -> {
                                        System.err.println("Unverified user sign out error: " + getRootCause(e).getLocalizedMessage());
                                        _userProfile.postValue(new AuthResultState.EmailNotVerified());
                                        return null;
                                    });
                        }
                    } else {
                        _userProfile.postValue(new AuthResultState.SignedOut());
                    }
                })
                .exceptionally(throwable -> {
                    _userProfile.postValue(new AuthResultState.SignedOut());
                    return null;
                });
    }

    // ******************** İSTEDİĞİNİZ YENİ METOT (DÜZELTİLMİŞ HALİ) ********************
    /**
     * **BU METOT GÜNCELLENDİ:** Artık toplam XP yerine, mevcut level'daki XP'yi kaydeder.
     * @param newLevel Kullanıcının yeni level'ı
     * @param xpInNewLevel Kullanıcının o anki level'da sahip olduğu XP
     */
    public void updateUserProgress(int newLevel, int xpInNewLevel) {
        String userId = firebaseAuth.getUid();
        if (userId == null) {
            Log.w("UserViewModel", "Kullanıcı ID'si null, ilerleme kaydedilemedi.");
            return;
        }

        // Güncellenecek verileri bir Map içinde hazırla
        Map<String, Object> updates = new HashMap<>();
        updates.put("level", newLevel);
        updates.put("xp", xpInNewLevel); // DİKKAT: Artık toplam XP değil, o level'daki XP'yi kaydediyoruz.

        // Firestore'da ilgili kullanıcının dokümanını bu yeni verilerle güncelle
        db.collection("users").document(userId)
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    // Başarıyla güncellendi
                    Log.d("UserViewModel", "Kullanıcı ilerlemesi güncellendi. Level: " + newLevel + ", XP: " + xpInNewLevel);
                })
                .addOnFailureListener(e -> {
                    // Güncelleme sırasında hata oluştu
                    Log.e("UserViewModel", "Kullanıcı ilerlemesi güncellenirken hata oluştu.", e);
                });
    }

    // ************************************************************************************

    // Kullanıcının oturumunu kapatır. (Bu metot aynı kalıyor)
    public void signOut() {
        signOutUserUseCase.execute()
                .exceptionally(throwable -> {
                    System.err.println("Sign out error: " + getRootCause(throwable).getLocalizedMessage());
                    return null;
                });
    }

    // Doğrulama email'ini tekrar gönderir. (Bu metot aynı kalıyor)
    public void resendVerificationEmail() {
        _authResult.setValue(new AuthResultState.Loading());
        resendVerificationEmailUseCase.execute()
                .thenAccept(aVoid -> _authResult.postValue(new AuthResultState.ResendEmailSuccess()))
                .exceptionally(throwable -> {
                    _authResult.postValue(new AuthResultState.Error(getRootCause(throwable).getLocalizedMessage()));
                    return null;
                });
    }

    // LiveData durumunu temizler. (Bu metot aynı kalıyor)
    public void clearAuthResultState() {
        _authResult.setValue(null);
    }

    // Hatanın kök sebebini bulur. (Bu metot aynı kalıyor)
    private Throwable getRootCause(Throwable throwable) {
        if (throwable instanceof CancellationException || throwable.getCause() == null) {
            return throwable;
        }
        return throwable.getCause();
    }
}
// KODUN SONU
