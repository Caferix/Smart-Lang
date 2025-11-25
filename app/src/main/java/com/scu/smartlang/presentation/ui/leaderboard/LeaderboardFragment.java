package com.scu.smartlang.presentation.ui.leaderboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.scu.smartlang.R;
import com.scu.smartlang.presentation.ui.auth.AuthResultState;
import com.scu.smartlang.presentation.viewmodel.AuthViewModel;
import com.scu.smartlang.presentation.viewmodel.ProfileViewModel;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class LeaderboardFragment extends Fragment {

    private ProfileViewModel profileViewModel;
    private TextView tvLeaderboardContent;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_leaderboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        profileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);

        // Layout'unda bir TextView ID'si tanımlı olmalı, örneğin: tv_leaderboard_title'ın altındaki bir textview
        // Şimdilik sadece başlığı güncelleyelim veya log basalım.

        // Kullanıcı kendi puanını görsün
        profileViewModel.getUserProfile().observe(getViewLifecycleOwner(), state -> {
            if (state instanceof AuthResultState.Success) {
                // Burada ileride tüm kullanıcıları listeleyen bir logic kurulacak.
                // Şimdilik kullanıcının kendi sırasını (temsili) gösterelim.
            }
        });
        profileViewModel.fetchUserProfile();
    }
}