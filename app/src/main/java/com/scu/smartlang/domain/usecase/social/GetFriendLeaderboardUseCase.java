package com.scu.smartlang.domain.usecase.social;

import com.scu.smartlang.domain.model.User;
import com.scu.smartlang.domain.repository.SocialRepository;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.rxjava3.core.Observable;

public class GetFriendLeaderboardUseCase {
    private final SocialRepository socialRepository;

    @Inject
    public GetFriendLeaderboardUseCase(SocialRepository socialRepository) {
        this.socialRepository = socialRepository;
    }

    public Observable<List<User>> execute() {
        return socialRepository.getFriendLeaderboard();
    }
}
