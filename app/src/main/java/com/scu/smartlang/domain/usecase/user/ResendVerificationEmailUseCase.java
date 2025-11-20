package com.scu.smartlang.domain.usecase.user;

import com.scu.smartlang.domain.repository.FirebaseRepository;
import java.util.concurrent.CompletableFuture;
import javax.inject.Inject;

public class ResendVerificationEmailUseCase {
    private final FirebaseRepository repository;

    @Inject
    public ResendVerificationEmailUseCase(FirebaseRepository repository) {
        this.repository = repository;
    }

    public CompletableFuture<Void> execute() {
    }
}