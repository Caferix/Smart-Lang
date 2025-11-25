package com.scu.smartlang.domain.usecase.social;

import com.scu.smartlang.domain.repository.SocialRepository;

import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

public class AcceptFriendRequestUseCase {
    private final SocialRepository repository;

    @Inject
    public AcceptFriendRequestUseCase(SocialRepository repository) {
        this.repository = repository;
    }

    public CompletableFuture<Void> execute(String requestId, String acceptorUid, String requesterUid) {
        return repository.acceptFriendRequest(requestId, acceptorUid, requesterUid);
    }
}
