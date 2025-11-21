package com.scu.smartlang.domain.usecase.user;

import com.scu.smartlang.domain.model.User;
import com.scu.smartlang.domain.repository.FirebaseRepository;

import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

public class GetUserByIdUseCase {
    private final FirebaseRepository repository;

    @Inject
    public GetUserByIdUseCase(FirebaseRepository repository) {
        this.repository = repository;
    }

    public CompletableFuture<User> execute(String uid) {
        return repository.getUserById(uid);
    }
}
