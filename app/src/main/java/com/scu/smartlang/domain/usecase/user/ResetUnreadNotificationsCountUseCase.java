package com.scu.smartlang.domain.usecase.user;

import com.scu.smartlang.domain.repository.UserProfileRepository;
import java.util.concurrent.CompletableFuture;
import javax.inject.Inject;

public class ResetUnreadNotificationsCountUseCase {
    private final UserProfileRepository repository;

    @Inject
    public ResetUnreadNotificationsCountUseCase(UserProfileRepository repository) {
        this.repository = repository;
    }

    public CompletableFuture<Void> execute(String uid) {
        return repository.resetUnreadNotificationsCount(uid);
    }
}
