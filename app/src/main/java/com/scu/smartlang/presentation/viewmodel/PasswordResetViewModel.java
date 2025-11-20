package com.scu.smartlang.presentation.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.scu.smartlang.domain.usecase.user.SendPasswordResetEmailUseCase;
import com.scu.smartlang.domain.usecase.user.UpdatePasswordUseCase;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class PasswordResetViewModel extends ViewModel {
    private final SendPasswordResetEmailUseCase sendPasswordResetEmailUseCase;
    private final UpdatePasswordUseCase updatePasswordUseCase;

    private final MutableLiveData<Boolean> _isLoading = new MutableLiveData<>(false);
    public final LiveData<Boolean> isLoading = _isLoading;

    private final MutableLiveData<String> _successMessage = new MutableLiveData<>();
    public final LiveData<String> successMessage = _successMessage;

    private final MutableLiveData<String> _errorMessage = new MutableLiveData<>();
    public final LiveData<String> errorMessage = _errorMessage;

    @Inject
    public PasswordResetViewModel(
            SendPasswordResetEmailUseCase sendPasswordResetEmailUseCase,
            UpdatePasswordUseCase updatePasswordUseCase
    ) {
        this.sendPasswordResetEmailUseCase = sendPasswordResetEmailUseCase;
        this.updatePasswordUseCase = updatePasswordUseCase;
    }

    // Şifre sıfırlama e-postası gönder
    public void sendPasswordResetEmail(String email) {
        _isLoading.setValue(true);
        sendPasswordResetEmailUseCase.execute(email)
                .thenAccept(aVoid -> {
                    _isLoading.postValue(false);
                    _successMessage.postValue("Sıfırlama e-postası gönderildi. Lütfen kutunuzu kontrol edin.");
                })
                .exceptionally(throwable -> {
                    _isLoading.postValue(false);
                    _errorMessage.postValue(throwable.getMessage());
                    return null;
                });
    }

    // Yeni şifre ile güncelle (Re-auth gerektiren durumlar için repository'de işlem yapılır)
    public void updatePasswordWithReauthentication(String currentPassword, String newPassword) {
        _isLoading.setValue(true);

        updatePasswordUseCase.executeWithReauth(currentPassword, newPassword)
                .thenAccept(aVoid -> {
                    _isLoading.postValue(false);
                    _successMessage.postValue("Şifreniz başarıyla değiştirildi");
                })
                .exceptionally(throwable -> {
                    _isLoading.postValue(false);
                    _errorMessage.postValue(parseFirebaseError(throwable));
                    return null;
                });
    }

    // Hata mesajlarını Türkçeleştir
    private String parseFirebaseError(Throwable throwable) {
        if (throwable == null || throwable.getMessage() == null) return "Bir hata oluştu.";
        String msg = throwable.getMessage().toLowerCase();
        if (msg.contains("invalid_login_credentials") || msg.contains("wrong-password")) return "Mevcut şifre yanlış.";
        if (msg.contains("weak-password")) return "Şifre çok zayıf.";
        if (msg.contains("requires-recent-login")) return "Güvenlik için tekrar giriş yapmalısınız.";
        return throwable.getMessage(); // Bilinmeyen hatalar için orijinal mesaj
    }
}