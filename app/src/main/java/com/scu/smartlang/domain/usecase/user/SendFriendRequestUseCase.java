package com.scu.smartlang.domain.usecase.user;

import com.scu.smartlang.domain.repository.FirebaseRepository;

import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

public class SendFriendRequestUseCase {
    private final FirebaseRepository repository;

    @Inject
    public SendFriendRequestUseCase(FirebaseRepository repository) {
        this.repository = repository;
    }

    public CompletableFuture<Void> execute(String fromUid, String toUid) {
        return repository.sendFriendRequest(fromUid, toUid);
    }
}
