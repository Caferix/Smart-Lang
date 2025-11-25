package com.scu.smartlang.domain.usecase.user;

import com.scu.smartlang.domain.repository.AuthRepository;

import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

public class SignOutUserUseCase {
    private final AuthRepository repository;

    @Inject
    public SignOutUserUseCase(AuthRepository repository) {
        this.repository = repository;
    }

    public CompletableFuture<Void> execute() {
        return repository.signOut();
    }
}
