package com.scu.smartlang.presentation.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.scu.smartlang.R;
import com.scu.smartlang.domain.model.User;
import com.scu.smartlang.presentation.ui.auth.AuthResultState;
import com.scu.smartlang.presentation.viewmodel.UserViewModel;
import java.util.ArrayList;
import java.util.List;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ProfileFragment extends Fragment {

    private UserViewModel userViewModel;
    private TextView tvName, tvLevel, tvXp;
    private ProgressBar pbXp;
    private RecyclerView rvFriends;
    private FriendsAdapter friendsAdapter; // Adaptörü sınıf seviyesinde tutmak iyi olabilir

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        // View tanımlamaları
        tvName = view.findViewById(R.id.tv_profile_name);
        tvLevel = view.findViewById(R.id.tv_level_label);
        tvXp = view.findViewById(R.id.tv_xp_label);
        pbXp = view.findViewById(R.id.pb_profile_xp);
        rvFriends = view.findViewById(R.id.rv_friends);

        setupFriendsList();
        observeUserData();

        // Kullanıcı verisini çek
        userViewModel.fetchUserProfile();
    }

    private void observeUserData() {
        userViewModel.getUserProfile().observe(getViewLifecycleOwner(), state -> {
            if (state instanceof AuthResultState.Success) {
                User user = ((AuthResultState.Success) state).getUser();
                if (user != null) {
                    updateUI(user);
                }
            }
        });
    }

    private void updateUI(User user) {
        tvName.setText(user.getUserName() != null ? user.getUserName() : "Kullanıcı");

        int currentLevel = user.getLevel();
        int totalXp = user.getXp();
        int requiredXp = 100 + (currentLevel - 1) * 25;

        int previousLevelsTotalXp = 0;
        for (int i = 1; i < currentLevel; i++) {
            previousLevelsTotalXp += (100 + (i - 1) * 25);
        }

        int xpInCurrentLevel = totalXp - previousLevelsTotalXp;

        if (xpInCurrentLevel < 0) xpInCurrentLevel = 0;

        tvLevel.setText("Level " + currentLevel);
        tvXp.setText(xpInCurrentLevel + " / " + requiredXp + " XP");

        pbXp.setMax(requiredXp);
        pbXp.setProgress(xpInCurrentLevel);
    }

    private void setupFriendsList() {
        // Sabit isimler kaldırıldı.
        // Arkadaşın buraya veritabanından veri çeken kodu entegre edecek.
        List<FriendsAdapter.FriendModel> emptyList = new ArrayList<>();

        friendsAdapter = new FriendsAdapter(emptyList);
        rvFriends.setLayoutManager(new LinearLayoutManager(getContext()));
        rvFriends.setAdapter(friendsAdapter);
    }
}