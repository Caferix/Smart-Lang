package com.scu.smartlang.domain.usecase.user;

import com.scu.smartlang.domain.model.Friend;
import com.scu.smartlang.domain.repository.FirebaseRepository;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

public class GetFriendsUseCase {
    private final FirebaseRepository repository;

    @Inject
    public GetFriendsUseCase(FirebaseRepository repository) {
        this.repository = repository;
    }

    public CompletableFuture<List<Friend>> execute(String uid) {
        return repository.getFriends(uid);
    }
}
