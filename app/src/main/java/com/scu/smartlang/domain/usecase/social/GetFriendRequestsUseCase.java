package com.scu.smartlang.domain.usecase.social;

import com.scu.smartlang.domain.model.FriendRequest;
import com.scu.smartlang.domain.repository.SocialRepository;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

public class GetFriendRequestsUseCase {
    private final SocialRepository repository;

    @Inject
    public GetFriendRequestsUseCase(SocialRepository repository) {
        this.repository = repository;
    }

    public CompletableFuture<List<FriendRequest>> execute(String uid) {
        return repository.getIncomingFriendRequests(uid);
    }
}
