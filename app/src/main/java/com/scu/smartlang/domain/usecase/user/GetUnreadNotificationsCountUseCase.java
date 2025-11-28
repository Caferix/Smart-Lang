package com.scu.smartlang.domain.usecase.user;

import androidx.lifecycle.LiveData;

import com.scu.smartlang.domain.repository.UserProfileRepository;

import javax.inject.Inject;


public class GetUnreadNotificationsCountUseCase {
    private final UserProfileRepository repository;

    @Inject
    public GetUnreadNotificationsCountUseCase(UserProfileRepository repository) {
        this.repository = repository;
    }

    public LiveData<Integer> execute(String uid) {
        return repository.getUnreadNotificationsCount(uid);
    }
}
