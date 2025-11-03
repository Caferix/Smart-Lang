package com.scu.smartlang.domain.usecase.user;

import com.scu.smartlang.domain.model.User;
import com.scu.smartlang.domain.repository.FirebaseRepository;

import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

public class RegisterUserUseCase {
    private final FirebaseRepository repository;

    @Inject
    public RegisterUserUseCase(FirebaseRepository repository) {
        this.repository = repository;
    }

    public CompletableFuture<User> execute(String email, String password, String userName) {
        return repository.createUserWithEmail(email, password, userName);
    }
}
