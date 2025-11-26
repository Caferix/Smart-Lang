package com.scu.smartlang.presentation.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;

import com.scu.smartlang.GameActivity;
import com.scu.smartlang.R;
import com.scu.smartlang.domain.model.User;
import com.scu.smartlang.presentation.ui.auth.AuthResultState;
import com.scu.smartlang.presentation.viewmodel.ProfileViewModel;
import com.google.android.material.button.MaterialButton;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class HomeFragment extends Fragment {

    private TextView tvWelcomeTitle;
    private TextView tvUserLevelXp;
    private ProgressBar progressXp;
    private TextView tvStreakCount;
    private MaterialButton btnStartDailyLesson;
    private MaterialButton btnStartGameMatch;
    private MaterialButton btnStartGamePuzzle;
    private MaterialButton btnStartAi;
    private ImageView ivNotificationIcon;
    private ProfileViewModel profileViewModel;

    private static final String DAILY_LESSON_TITLE = "GÜNLÜK DERSE BAŞLA";
    private static final String DEFAULT_MODULE_PLACEHOLDER = "(Temel Zamirler)";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (profileViewModel != null) {
            profileViewModel.fetchUserProfile();
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        profileViewModel = new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);

        // View'ları bağla
        tvWelcomeTitle = view.findViewById(R.id.tv_welcome_title);
        tvUserLevelXp = view.findViewById(R.id.tv_user_level_xp);
        progressXp = view.findViewById(R.id.progress_xp);
        tvStreakCount = view.findViewById(R.id.tv_streak_count);
        ivNotificationIcon = view.findViewById(R.id.iv_notification_icon);
        btnStartDailyLesson = view.findViewById(R.id.btn_start_daily_lesson);
        btnStartGameMatch = view.findViewById(R.id.btn_start_game_match);
        btnStartGamePuzzle = view.findViewById(R.id.btn_start_game_puzzle);
        btnStartAi = view.findViewById(R.id.btn_start_ai);

        observeViewModel();
        setupListenersAndText();
    }

    private void observeViewModel() {
        profileViewModel.getUserProfile().observe(getViewLifecycleOwner(), authResult -> {
            if (authResult instanceof AuthResultState.Loading) {
                tvWelcomeTitle.setText("Yükleniyor...");
            } else if (authResult instanceof AuthResultState.Success) {
                User user = ((AuthResultState.Success) authResult).getUser();
                if (user != null) {
                    updateUiWithUser(user);
                }
            } else if (authResult instanceof AuthResultState.Error) {
                Toast.makeText(getContext(), "Hata: " + ((AuthResultState.Error) authResult).getMessage(), Toast.LENGTH_LONG).show();
                navigateToSignIn();
            } else if (authResult instanceof AuthResultState.SignedOut || authResult instanceof AuthResultState.EmailNotVerified) {
                navigateToSignIn();
            }
        });
    }

    private void setupListenersAndText() {
        String buttonText = String.format("%s<br><small><small>%s</small></small>",
                DAILY_LESSON_TITLE, DEFAULT_MODULE_PLACEHOLDER);
        btnStartDailyLesson.setText(android.text.Html.fromHtml(buttonText, android.text.Html.FROM_HTML_MODE_LEGACY));

        btnStartDailyLesson.setOnClickListener(v -> Toast.makeText(getContext(), "Günlük ders yakında!", Toast.LENGTH_SHORT).show());

        btnStartGameMatch.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), GameActivity.class);
            startActivity(intent);
        });

        btnStartGamePuzzle.setOnClickListener(v ->
                Toast.makeText(getContext(), "Kelime Bulmaca çok yakında!", Toast.LENGTH_SHORT).show());

        btnStartAi.setOnClickListener(v ->
                Toast.makeText(getContext(), "AI Asistan ile sohbet yakında!", Toast.LENGTH_SHORT).show());

        // Arkadaş İsteklerine Git
        ivNotificationIcon.setOnClickListener(v -> {
            NavHostFragment.findNavController(this).navigate(R.id.action_to_friend_requests);
        });
    }

    private void updateUiWithUser(User user) {
        if (user == null) return;
        String userName = user.getUserName();
        String welcomeName = (userName != null && !userName.isEmpty()) ? userName : user.getEmail().split("@")[0];
        tvWelcomeTitle.setText(getString(R.string.welcome_message, welcomeName));

        int currentLevel = user.getLevel();
        int totalXp = user.getXp();
        int requiredXpForNextLevel = 100 + (currentLevel - 1) * 25;
        int previousLevelsXp = 0;
        for (int i = 1; i < currentLevel; i++) previousLevelsXp += (100 + (i - 1) * 25);
        int xpForCurrentLevel = totalXp - previousLevelsXp;

        tvUserLevelXp.setText(String.format("Level %d | %d/%d XP", currentLevel, xpForCurrentLevel, requiredXpForNextLevel));
        progressXp.setMax(requiredXpForNextLevel);
        progressXp.setProgress(xpForCurrentLevel);
        tvStreakCount.setText("Seri: 0 Gün");
    }

    private void navigateToSignIn() {
        if (isAdded()) {
            NavController navController = NavHostFragment.findNavController(this);
            NavOptions navOptions = new NavOptions.Builder().setPopUpTo(R.id.main_nav_graph, true).build();
            navController.navigate(R.id.signInFragment, null, navOptions);
        }
    }
}