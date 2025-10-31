package com.scu.smartlang.domain.usecase.user;

import com.scu.smartlang.domain.model.User;
import com.scu.smartlang.domain.repository.FirebaseRepository;

import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

public class LoginUserUseCase {
    private final FirebaseRepository repository;

    @Inject
    public LoginUserUseCase(FirebaseRepository repository) {
        this.repository = repository;
    }

    public CompletableFuture<User> execute(String email, String password) {
        return repository.signInWithEmail(email, password);
    }
}
