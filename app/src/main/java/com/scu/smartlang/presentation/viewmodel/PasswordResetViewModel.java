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
                    return null;
                });
    }

    private String parseFirebaseError(Throwable throwable) {
    }
}