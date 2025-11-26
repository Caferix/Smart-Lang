package com.scu.smartlang.domain.usecase.user;

import com.scu.smartlang.domain.repository.AuthRepository;
import java.util.concurrent.CompletableFuture;
import javax.inject.Inject;

public class SendPasswordResetEmailUseCase {
    private final AuthRepository repository;

    @Inject
    public SendPasswordResetEmailUseCase(AuthRepository repository) {
        this.repository = repository;
    }

    public CompletableFuture<Void> execute(String email) {
        return repository.sendPasswordResetEmail(email);
    }
}
