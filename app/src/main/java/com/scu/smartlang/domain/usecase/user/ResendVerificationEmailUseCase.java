package com.scu.smartlang.domain.usecase.user;

import com.scu.smartlang.domain.repository.AuthRepository;

import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

public class ResendVerificationEmailUseCase {

    private final AuthRepository repository;

    @Inject
    public ResendVerificationEmailUseCase(AuthRepository repository) {
        this.repository = repository;
    }

    public CompletableFuture<Void> execute() {
        return repository.resendVerificationEmail();
    }
}
