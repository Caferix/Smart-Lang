package com.scu.smartlang.domain.usecase.user;

import com.scu.smartlang.domain.repository.FirebaseRepository;
import java.util.concurrent.CompletableFuture;
import javax.inject.Inject;

public class SendPasswordResetEmailUseCase {
    private final FirebaseRepository repository;

    @Inject
    public SendPasswordResetEmailUseCase(FirebaseRepository repository) {
        this.repository = repository;
    }

    public CompletableFuture<Void> execute(String email) {
        if (email == null || email.trim().isEmpty()) {
            CompletableFuture<Void> failed = new CompletableFuture<>();
            failed.completeExceptionally(new IllegalArgumentException("Email adresi gerekli"));
            return failed;
        }
        return repository.sendPasswordResetEmail(email);
    }
}