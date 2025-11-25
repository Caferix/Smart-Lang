package com.scu.smartlang.domain.usecase.social;

import com.scu.smartlang.domain.model.Friend;
import com.scu.smartlang.domain.repository.SocialRepository;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

public class GetFriendsUseCase {
    private final SocialRepository repository;

    @Inject
    public GetFriendsUseCase(SocialRepository repository) {
        this.repository = repository;
    }

    public CompletableFuture<List<Friend>> execute(String uid) {
        return repository.getFriends(uid);
    }
}
