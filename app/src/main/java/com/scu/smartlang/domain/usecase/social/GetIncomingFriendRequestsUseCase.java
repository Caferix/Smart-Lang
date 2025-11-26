package com.scu.smartlang.domain.usecase.user;

import androidx.lifecycle.LiveData;
import com.scu.smartlang.domain.model.FriendRequest;
import com.scu.smartlang.domain.repository.UserProfileRepository;
import java.util.List;
import javax.inject.Inject;

public class GetIncomingFriendRequestsUseCase {
    private final UserProfileRepository repository;

    @Inject
    public GetIncomingFriendRequestsUseCase(UserProfileRepository repository) {
        this.repository = repository;
    }

    public LiveData<List<FriendRequest>> execute(String uid) {
        return repository.getIncomingFriendRequests(uid);
    }
}
