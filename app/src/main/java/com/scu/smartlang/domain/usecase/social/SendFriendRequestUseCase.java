package com.scu.smartlang.domain.usecase.social;

import com.scu.smartlang.domain.repository.SocialRepository;

import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

public class SendFriendRequestUseCase {
    private final SocialRepository repository;

    @Inject
    public SendFriendRequestUseCase(SocialRepository repository) {
        this.repository = repository;
    }

    public CompletableFuture<Void> execute(String fromUid, String toUid) {
        return repository.sendFriendRequest(fromUid, toUid);
    }
}
