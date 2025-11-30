package com.scu.smartlang.presentation.ui.leaderboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.scu.smartlang.R;
import com.scu.smartlang.domain.model.User;
import com.scu.smartlang.presentation.viewmodel.LeaderboardViewModel;

import java.util.List;
import java.util.Locale;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class LeaderboardFragment extends Fragment implements LeaderboardAdapter.OnUserClickListener {

    private LeaderboardViewModel leaderboardViewModel;
    private LeaderboardAdapter leaderboardAdapter;
    private RecyclerView recyclerView;
    private LinearLayout firstPlaceLayout, secondPlaceLayout, thirdPlaceLayout;
    private ImageView ivFirstPlace, ivSecondPlace, ivThirdPlace;
    private TextView tvFirstName, tvFirstXp, tvSecondName, tvSecondXp, tvThirdName, tvThirdXp;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_leaderboard, container, false);
        recyclerView = view.findViewById(R.id.leaderboard_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Adapter'ı listener ile başlat
        leaderboardAdapter = new LeaderboardAdapter(this);
        recyclerView.setAdapter(leaderboardAdapter);

        bindPodiumViews(view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        leaderboardViewModel = new ViewModelProvider(this).get(LeaderboardViewModel.class);

        leaderboardViewModel.fetchLeaderboard();

        // Kullanıcı listesi güncellendiğinde burası çalışır
        leaderboardViewModel.getLeaderboard().observe(getViewLifecycleOwner(), users -> {
            if (users != null && !users.isEmpty()) {
                // Listeyi updatePodium'a gönder
                updatePodium(users);

                if (users.size() > 3) {
                    leaderboardAdapter.setUsers(users.subList(3, users.size()));
                } else {
                    leaderboardAdapter.setUsers(java.util.Collections.emptyList());
                }
            }
        });

        leaderboardViewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void bindPodiumViews(View view) {
        firstPlaceLayout = view.findViewById(R.id.first_place_layout);
        secondPlaceLayout = view.findViewById(R.id.second_place_layout);
        thirdPlaceLayout = view.findViewById(R.id.third_place_layout);

        ivFirstPlace = view.findViewById(R.id.iv_first_place);
        tvFirstName = view.findViewById(R.id.tv_first_place_name);
        tvFirstXp = view.findViewById(R.id.tv_first_place_xp);

        ivSecondPlace = view.findViewById(R.id.iv_second_place);
        tvSecondName = view.findViewById(R.id.tv_second_place_name);
        tvSecondXp = view.findViewById(R.id.tv_second_place_xp);

        ivThirdPlace = view.findViewById(R.id.iv_third_place);
        tvThirdName = view.findViewById(R.id.tv_third_place_name);
        tvThirdXp = view.findViewById(R.id.tv_third_place_xp);
    }

    private void updatePodium(List<User> users) {
        // 1. Kullanıcı
        if (users.size() > 0) {
            final User firstUser = users.get(0);
            bindUserToPodiumView(firstUser, ivFirstPlace, tvFirstName, tvFirstXp);
            firstPlaceLayout.setVisibility(View.VISIBLE);
            // Tıklama dinleyicisini burada kuruyoruz
            firstPlaceLayout.setOnClickListener(v -> onUserClick(firstUser.getUid()));
        } else {
            firstPlaceLayout.setVisibility(View.INVISIBLE);
            firstPlaceLayout.setOnClickListener(null);
        }

        // 2. Kullanıcı
        if (users.size() > 1) {
            final User secondUser = users.get(1);
            bindUserToPodiumView(secondUser, ivSecondPlace, tvSecondName, tvSecondXp);
            secondPlaceLayout.setVisibility(View.VISIBLE);
            // Tıklama dinleyicisini burada kuruyoruz
            secondPlaceLayout.setOnClickListener(v -> onUserClick(secondUser.getUid()));
        } else {
            secondPlaceLayout.setVisibility(View.INVISIBLE);
            secondPlaceLayout.setOnClickListener(null);
        }

        // 3. Kullanıcı
        if (users.size() > 2) {
            final User thirdUser = users.get(2);
            bindUserToPodiumView(thirdUser, ivThirdPlace, tvThirdName, tvThirdXp);
            thirdPlaceLayout.setVisibility(View.VISIBLE);
            // Tıklama dinleyicisini burada kuruyoruz
            thirdPlaceLayout.setOnClickListener(v -> onUserClick(thirdUser.getUid()));
        } else {
            thirdPlaceLayout.setVisibility(View.INVISIBLE);
            thirdPlaceLayout.setOnClickListener(null);
        }
    }

    private void bindUserToPodiumView(User user, ImageView imageView, TextView nameView, TextView xpView) {
        nameView.setText(user.getUserName());
        xpView.setText(String.format(Locale.getDefault(), "%d XP", user.getXp()));
        Glide.with(this)
                .load(user.getProfileImageUrl())
                .placeholder(R.drawable.ic_person_24dp)
                .circleCrop()
                .into(imageView);
    }

    @Override
    public void onUserClick(String userId) {
        // Navigasyon için Bundle hazırla
        Bundle args = new Bundle();
        args.putString("userId", userId);

        // Diğer kullanıcının profil sayfasına git
        NavHostFragment.findNavController(this).navigate(R.id.action_navigation_leaderboard_to_navigation_profile, args);
    }
}