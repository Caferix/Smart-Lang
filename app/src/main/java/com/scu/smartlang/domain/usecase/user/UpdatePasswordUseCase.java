package com.scu.smartlang.domain.usecase.user;

import com.scu.smartlang.domain.repository.FirebaseRepository;
import java.util.concurrent.CompletableFuture;
import javax.inject.Inject;

public class UpdatePasswordUseCase {
    private final FirebaseRepository repository;

    @Inject
    public UpdatePasswordUseCase(FirebaseRepository repository) {
        this.repository = repository;
    }

    // Standart güncelleme
    public CompletableFuture<Void> execute(String newPassword) {
        if (newPassword == null || newPassword.length() < 6) {
            CompletableFuture<Void> failed = new CompletableFuture<>();
            failed.completeExceptionally(new IllegalArgumentException("Şifre en az 6 karakter olmalı"));
            return failed;
        }
        return repository.updatePassword(newPassword);
    }

    // Yeniden doğrulama ile güncelleme (Güvenlik için)
    public CompletableFuture<Void> executeWithReauth(String currentPassword, String newPassword) {
        if (newPassword == null || newPassword.length() < 6) {
            CompletableFuture<Void> failed = new CompletableFuture<>();
            failed.completeExceptionally(new IllegalArgumentException("Yeni şifre en az 6 karakter olmalı"));
            return failed;
        }

        // Not: Gerçek bir re-auth işlemi için Repository katmanında destek gerekir.
        // Şimdilik mevcut oturum üzerinden güncelleme deniyoruz.
        return repository.updatePassword(newPassword);
    }
}