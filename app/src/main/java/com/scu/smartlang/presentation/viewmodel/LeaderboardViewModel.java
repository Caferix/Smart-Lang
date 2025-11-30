package com.scu.smartlang.presentation.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.scu.smartlang.domain.model.User;
import com.scu.smartlang.domain.usecase.social.GetFriendLeaderboardUseCase;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

@HiltViewModel
public class LeaderboardViewModel extends ViewModel {
    private final GetFriendLeaderboardUseCase getFriendLeaderboardUseCase;
    private final CompositeDisposable disposables = new CompositeDisposable();
    private final MutableLiveData<List<User>> leaderboard = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();

    @Inject
    public LeaderboardViewModel(GetFriendLeaderboardUseCase getFriendLeaderboardUseCase) {
        this.getFriendLeaderboardUseCase = getFriendLeaderboardUseCase;
    }

    public LiveData<List<User>> getLeaderboard() {
        return leaderboard;
    }

    public LiveData<String> getError() {
        return error;
    }

    public void fetchLeaderboard() {
        disposables.clear();
        disposables.add(getFriendLeaderboardUseCase.execute()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        leaderboard::setValue,
                        throwable -> error.setValue(throwable.getMessage())
                ));
    }

    public void clearLeaderboard() {
        disposables.clear();
        leaderboard.setValue(null);
        error.setValue(null);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        disposables.clear();
    }
}

