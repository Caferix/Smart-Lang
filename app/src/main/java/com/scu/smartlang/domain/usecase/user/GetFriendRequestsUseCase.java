package com.scu.smartlang.domain.usecase.user;

import com.scu.smartlang.domain.model.FriendRequest;
import com.scu.smartlang.domain.repository.FirebaseRepository;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

public class GetFriendRequestsUseCase {
    private final FirebaseRepository repository;

    @Inject
    public GetFriendRequestsUseCase(FirebaseRepository repository) {
        this.repository = repository;
    }

    public CompletableFuture<List<FriendRequest>> execute(String uid) {
        return repository.getIncomingFriendRequests(uid);
    }
}
