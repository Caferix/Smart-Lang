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
                    _successMessage.postValue("Password reset email sent. Please check your inbox.");
                })
                .exceptionally(throwable -> {
                    _isLoading.postValue(false);
                    _errorMessage.postValue(throwable.getMessage());
                    return null;
                });
    }

    public void updatePassword(String newPassword) {
        _isLoading.setValue(true);
        updatePasswordUseCase.execute(newPassword)
                .thenAccept(aVoid -> {
                    _isLoading.postValue(false);
                    _successMessage.postValue("Password updated successfully");
                })
                .exceptionally(throwable -> {
                    _isLoading.postValue(false);
                    _errorMessage.postValue(throwable.getMessage());
                    return null;
                });
    }

    public void updatePasswordWithReauthentication(String currentPassword, String newPassword) {
        _isLoading.setValue(true);

        updatePasswordUseCase.executeWithReauth(currentPassword, newPassword)
                .thenAccept(aVoid -> {
                    _isLoading.postValue(false);
                    _successMessage.postValue("Şifreniz başarıyla değiştirildi");
                })
                .exceptionally(throwable -> {
                    _isLoading.postValue(false);

                    // Firebase hata mesajını kullanıcı dostu Türkçe'ye çevir
                    String userFriendlyMessage = parseFirebaseError(throwable);
                    _errorMessage.postValue(userFriendlyMessage);

                    return null;
                });
    }


      // Firebase hata mesajlarını kullanıcı dostu Türkçe mesajlara çevirir
    private String parseFirebaseError(Throwable throwable) {
        if (throwable == null || throwable.getMessage() == null) {
            return "Bir hata oluştu. Lütfen tekrar deneyin.";
        }

        String errorMessage = throwable.getMessage().toLowerCase();

        // Firebase Authentication hata kodları
        if (errorMessage.contains("invalid_login_credentials") ||
                errorMessage.contains("wrong-password") ||
                errorMessage.contains("incorrect-malformed")||
                errorMessage.contains("invalid-credential")) {
            return "Mevcut şifreniz yanlış. Lütfen tekrar deneyin.";
        }

        if (errorMessage.contains("user-not-found")) {
            return "Kullanıcı bulunamadı. Lütfen giriş yapın.";
        }

        if (errorMessage.contains("requires-recent-login")) {
            return "Güvenlik nedeniyle tekrar giriş yapmanız gerekiyor.";
        }

        if (errorMessage.contains("weak-password")) {
            return "Şifreniz çok zayıf. Daha güçlü bir şifre seçin.";
        }

        if (errorMessage.contains("network")) {
            return "İnternet bağlantınızı kontrol edin.";
        }

        if (errorMessage.contains("too-many-requests")) {
            return "Çok fazla deneme yaptınız. Lütfen daha sonra tekrar deneyin.";
        }

        // Use case'den gelen validasyon hataları
        if (errorMessage.contains("şifre en az 6 karakter") ||
                errorMessage.contains("mevcut şifre gerekli")) {
            return throwable.getMessage();
        }

        // Bilinmeyen hata - detaylı mesaj yerine genel mesaj
        return "Şifre değiştirilemedi. Lütfen tekrar deneyin.";
    }

    public void clearMessages() {
        _successMessage.setValue(null);
        _errorMessage.setValue(null);
    }
}
