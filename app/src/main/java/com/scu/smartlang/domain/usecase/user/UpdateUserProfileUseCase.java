package com.scu.smartlang.domain.usecase.user;

import com.scu.smartlang.domain.model.User;
import com.scu.smartlang.domain.repository.UserProfileRepository; // DEĞİŞTİ

import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

public class UpdateUserProfileUseCase {

    private final UserProfileRepository repository;

    @Inject
    public UpdateUserProfileUseCase(UserProfileRepository repository) { // DEĞİŞTİ
        this.repository = repository;
    }

    public CompletableFuture<Void> execute(User user) {
        return repository.updateUserProfile(user);
    }
}
