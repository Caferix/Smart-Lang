package com.scu.smartlang.domain.usecase.user;

import com.scu.smartlang.domain.repository.FirebaseRepository;

import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

public class AcceptFriendRequestUseCase {
    private final FirebaseRepository repository;

    @Inject
    public AcceptFriendRequestUseCase(FirebaseRepository repository) {
        this.repository = repository;
    }

    public CompletableFuture<Void> execute(String requestId, String acceptorUid) {
        return repository.acceptFriendRequest(requestId, acceptorUid);
    }
}
