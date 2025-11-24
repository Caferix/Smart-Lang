package com.scu.smartlang.domain.usecase.user;

import com.scu.smartlang.domain.repository.UserProfileRepository;

import java.util.concurrent.CompletableFuture;
import javax.inject.Inject;


public class GetUnreadNotificationsCountUseCase {
    private final UserProfileRepository repository;

    @Inject
    public GetUnreadNotificationsCountUseCase(UserProfileRepository repository) {
        this.repository = repository;
    }

    public CompletableFuture<Integer> execute(String uid) {
        return repository.getUnreadNotificationsCount(uid);
    }
}
