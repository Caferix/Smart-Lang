package com.scu.smartlang.domain.usecase.user;

import com.scu.smartlang.domain.model.User;
import com.scu.smartlang.domain.repository.SocialRepository;

import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

public class GetUserByIdUseCase {
    private final SocialRepository repository;

    @Inject
    public GetUserByIdUseCase(SocialRepository repository) {
        this.repository = repository;
    }

    public CompletableFuture<User> execute(String uid) {
        return repository.getUserById(uid);
    }
}
