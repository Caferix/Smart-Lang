package com.scu.smartlang.domain.usecase.user;

import com.scu.smartlang.domain.repository.FirebaseRepository;
import java.util.concurrent.CompletableFuture;
import javax.inject.Inject;

public class GetUnreadNotificationsCountUseCase {
    private final FirebaseRepository repository;

    @Inject
    public GetUnreadNotificationsCountUseCase(FirebaseRepository repository) {
        this.repository = repository;
    }

    public CompletableFuture<Integer> execute(String uid) {
        return repository.getUnreadNotificationsCount(uid);
    }
}
