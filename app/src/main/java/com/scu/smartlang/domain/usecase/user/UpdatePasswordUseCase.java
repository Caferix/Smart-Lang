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

    public CompletableFuture<Void> execute(String newPassword) {
        if (newPassword == null || newPassword.length() < 6) {
            CompletableFuture<Void> failed = new CompletableFuture<>();
            return failed;
        }
        return repository.updatePassword(newPassword);
    }

    public CompletableFuture<Void> executeWithReauth(String currentPassword, String newPassword) {
        if (newPassword == null || newPassword.length() < 6) {
            CompletableFuture<Void> failed = new CompletableFuture<>();
            return failed;
        }

    }
}