package com.scu.smartlang.domain.usecase.social;

import com.scu.smartlang.domain.model.User;
import com.scu.smartlang.domain.repository.UserProfileRepository;

import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

public class GetCurrentUserProfileUseCase {
    private final UserProfileRepository repository;

    @Inject
    public GetCurrentUserProfileUseCase(UserProfileRepository repository) {
        this.repository = repository;
    }

    public CompletableFuture<User> execute() {
        return repository.getCurrentUserProfile();
    }
}
