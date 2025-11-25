package com.scu.smartlang.domain.usecase.social;

import com.scu.smartlang.domain.repository.SocialRepository;

import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

public class RemoveFriendUseCase {
    private final SocialRepository repository;

    @Inject
    public RemoveFriendUseCase(SocialRepository repository) {
        this.repository = repository;
    }

    public CompletableFuture<Void> execute(String currentUserId, String friendToRemoveId) {
        // Repository'deki metoda yetki ve geçerlilik kontrolü sonrası çağrı yapılır.
        if (currentUserId == null || friendToRemoveId == null) {
            CompletableFuture<Void> failed = new CompletableFuture<>();
            failed.completeExceptionally(new IllegalArgumentException("User IDs cannot be null."));
            return failed;
        }
        return repository.removeFriend(currentUserId, friendToRemoveId);
    }
}
