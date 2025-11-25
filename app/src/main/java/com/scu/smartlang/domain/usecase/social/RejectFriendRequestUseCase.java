package com.scu.smartlang.domain.usecase.social;

import com.scu.smartlang.domain.repository.SocialRepository;

import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

public class RejectFriendRequestUseCase {
    private final SocialRepository repository;

    @Inject
    public RejectFriendRequestUseCase(SocialRepository repository) {
        this.repository = repository;
    }

    public CompletableFuture<Void> execute(String requestId, String recipientUid) {
        if (requestId == null || recipientUid == null) {
            CompletableFuture<Void> failed = new CompletableFuture<>();
            failed.completeExceptionally(new IllegalArgumentException("Request ID and Recipient UID cannot be null."));
            return failed;
        }
        return repository.rejectFriendRequest(requestId, recipientUid);
    }
}
