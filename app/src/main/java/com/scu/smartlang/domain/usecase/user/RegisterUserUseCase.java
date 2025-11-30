package com.scu.smartlang.domain.usecase.user;

import com.scu.smartlang.domain.model.User;
import com.scu.smartlang.domain.repository.AuthRepository;

import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

public class RegisterUserUseCase {
    private final AuthRepository repository;

    @Inject
    public RegisterUserUseCase(AuthRepository repository) {
        this.repository = repository;
    }

    public CompletableFuture<User> execute(String email, String password, String userName) {
        return repository.createUserWithEmail(email, password, userName);
    }
}
