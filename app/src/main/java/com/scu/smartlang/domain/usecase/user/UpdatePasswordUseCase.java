package com.scu.smartlang.domain.usecase.user;

import com.scu.smartlang.domain.repository.AuthRepository;
import java.util.concurrent.CompletableFuture;
import javax.inject.Inject;

public class UpdatePasswordUseCase {
    private final AuthRepository repository;

    @Inject
    public UpdatePasswordUseCase(AuthRepository repository) {
        this.repository = repository;
    }

    public CompletableFuture<Void> execute(String newPassword) {
        if (newPassword == null || newPassword.length() < 6) {
            CompletableFuture<Void> failed = new CompletableFuture<>();
            failed.completeExceptionally(
                    new IllegalArgumentException("Şifre en az 6 karakter olmalı")
            );
            return failed;
        }
        return repository.updatePassword(newPassword);
    }

    public CompletableFuture<Void> executeWithReauth(String currentPassword, String newPassword) {
        if (newPassword == null || newPassword.length() < 6) {
            CompletableFuture<Void> failed = new CompletableFuture<>();
            failed.completeExceptionally(
                    new IllegalArgumentException("Yeni şifre en az 6 karakter olmalı")
            );
            return failed;
        }

        if (currentPassword == null || currentPassword.isEmpty()) {
            CompletableFuture<Void> failed = new CompletableFuture<>();
            failed.completeExceptionally(
                    new IllegalArgumentException("Mevcut şifre gerekli")
            );
            return failed;
        }
        return repository.reauthenticateAndUpdatePassword(currentPassword, newPassword);
    }
}
