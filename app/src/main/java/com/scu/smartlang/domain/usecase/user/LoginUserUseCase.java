package com.scu.smartlang.domain.usecase.user;

import com.scu.smartlang.domain.model.User;
import com.scu.smartlang.domain.repository.AuthRepository;

import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

public class LoginUserUseCase {
    private final AuthRepository repository;

    @Inject
    public LoginUserUseCase(AuthRepository repository) {
        this.repository = repository;
    }

    public CompletableFuture<User> execute(String email, String password) {
        return repository.signInWithEmail(email, password);
    }
}
