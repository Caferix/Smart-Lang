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

    public void sendPasswordResetEmail(String email) {
        _isLoading.setValue(true);
        sendPasswordResetEmailUseCase.execute(email)
                .thenAccept(aVoid -> {
                    _isLoading.postValue(false);
                    _successMessage.postValue("Sıfırlama e-postası gönderildi. Lütfen kutunuzu kontrol edin.");
                })
                .exceptionally(throwable -> {
                    _isLoading.postValue(false);
                    _errorMessage.postValue(parseFirebaseError(throwable));
                    return null;
                });
    }

    public void updatePasswordWithReauthentication(String currentPassword, String newPassword) {
        _isLoading.setValue(true);

        // Bu Use Case'in içinde re-authentication (yeniden doğrulama) yapılıyor
        updatePasswordUseCase.executeWithReauth(currentPassword, newPassword)
                .thenAccept(aVoid -> {
                    _isLoading.postValue(false);
                    _successMessage.postValue("Şifreniz başarıyla değiştirildi");
                })
                .exceptionally(throwable -> {
                    _isLoading.postValue(false);

                    // Hata mesajını çevir
                    String userFriendlyMessage = parseFirebaseError(throwable);
                    _errorMessage.postValue(userFriendlyMessage);

                    return null;
                });
    }

    // Firebase hata mesajlarını kullanıcı dostu Türkçe mesajlara çevirir
    private String parseFirebaseError(Throwable throwable) {
        if (throwable == null || throwable.getMessage() == null) {
            return "Bilinmeyen bir hata oluştu. Lütfen tekrar deneyin.";
        }

        String errorMessage = throwable.getMessage().toLowerCase();

        if (errorMessage.contains("invalid_login_credentials") ||
                errorMessage.contains("wrong-password") ||
                errorMessage.contains("incorrect-malformed") ||
                errorMessage.contains("invalid-credential")) {
            return "Mevcut şifreniz yanlış. Lütfen tekrar deneyin.";
        }

        if (errorMessage.contains("user-not-found")) {
            return "Kullanıcı bulunamadı.";
        }

        if (errorMessage.contains("requires-recent-login")) {
            return "Güvenlik nedeniyle tekrar giriş yapmanız gerekiyor.";
        }

        if (errorMessage.contains("weak-password")) {
            return "Şifreniz çok zayıf. En az 6 karakter olmalı.";
        }

        if (errorMessage.contains("email adresi gerekli")) {
            return "Email adresi boş bırakılamaz.";
        }

        // Bilinmeyen hata
        return "İşlem tamamlanamadı: " + throwable.getMessage();
    }

    public void clearMessages() {
        _successMessage.setValue(null);
        _errorMessage.setValue(null);
    }
}