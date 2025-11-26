package com.scu.smartlang.presentation.ui.leaderboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.scu.smartlang.R;
import com.scu.smartlang.presentation.viewmodel.LeaderboardViewModel;

import com.scu.smartlang.presentation.ui.auth.AuthResultState;
import com.scu.smartlang.presentation.viewmodel.AuthViewModel;
import com.scu.smartlang.presentation.viewmodel.ProfileViewModel;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class LeaderboardFragment extends Fragment {

    private LeaderboardViewModel leaderboardViewModel;
    private LeaderboardAdapter leaderboardAdapter;
    private RecyclerView recyclerView;
    private ProfileViewModel profileViewModel;
    private TextView tvLeaderboardContent;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_leaderboard, container, false);
        recyclerView = view.findViewById(R.id.leaderboard_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        leaderboardAdapter = new LeaderboardAdapter();
        recyclerView.setAdapter(leaderboardAdapter);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        leaderboardViewModel = new ViewModelProvider(this).get(LeaderboardViewModel.class);

        leaderboardViewModel.getLeaderboard().observe(getViewLifecycleOwner(), users -> {
            if (users != null) {
                leaderboardAdapter.setUsers(users);
            }
        });

        leaderboardViewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}