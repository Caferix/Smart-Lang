package com.scu.smartlang.domain.usecase.user;

import com.scu.smartlang.domain.model.User;
import com.scu.smartlang.domain.repository.FirebaseRepository;

import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

public class GetCurrentUserProfileUseCase {
    private final FirebaseRepository repository;

    @Inject
    public GetCurrentUserProfileUseCase(FirebaseRepository repository) {
        this.repository = repository;
    }

    /**
     * Firebase'den o anki oturum açmış kullanıcının tam profilini çeker.
     */
    public CompletableFuture<User> execute() {
        return repository.getCurrentUserProfile();
    }
}