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
import androidx.navigation.Navigation;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;

import com.scu.smartlang.GameActivity;
import com.scu.smartlang.R;
import com.scu.smartlang.domain.model.User;
import com.scu.smartlang.presentation.ui.auth.AuthResultState;
import com.scu.smartlang.presentation.viewmodel.AuthViewModel;
import com.scu.smartlang.presentation.viewmodel.ProfileViewModel;
import com.google.android.material.button.MaterialButton;
import dagger.hilt.android.AndroidEntryPoint;
import com.scu.smartlang.presentation.viewmodel.AuthViewModel;
import com.scu.smartlang.presentation.viewmodel.ProfileViewModel;

@AndroidEntryPoint
public class HomeFragment extends Fragment {

    private TextView tvWelcomeTitle;
    private TextView tvUserLevelXp;
    private ProgressBar progressXp;
    private TextView tvStreakCount;
    private MaterialButton btnStartDailyLesson;
    private MaterialButton btnLanguageSelector;
    private MaterialButton btnStartGameMatch; // Kelime Eşleştirme
    private MaterialButton btnStartGamePuzzle; // Boşluk Doldurma (Senin Oyunun)
    private MaterialButton btnStartAi;         // AI Butonu
    private ImageView ivNotificationIcon;
    private TextView tvNotificationBadge;
    private ProfileViewModel profileViewModel;
    private AuthViewModel authViewModel;

    private static final String DAILY_LESSON_TITLE = "GÜNLÜK DERSE BAŞLA";
    private static final String DEFAULT_MODULE_PLACEHOLDER = "(Henüz ders atanmadı)";

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
            profileViewModel.fetchUnreadNotificationsCount(); // Badge'i güncelle
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        profileViewModel = new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);
        authViewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);

        // View'ları bağla
        tvWelcomeTitle = view.findViewById(R.id.tv_welcome_title);
        tvUserLevelXp = view.findViewById(R.id.tv_user_level_xp);
        progressXp = view.findViewById(R.id.progress_xp);
        tvStreakCount = view.findViewById(R.id.tv_streak_count);
        btnStartDailyLesson = view.findViewById(R.id.btn_start_daily_lesson);
        ivNotificationIcon = view.findViewById(R.id.iv_notification_icon);
        btnLanguageSelector = view.findViewById(R.id.btn_language_selector);
        tvNotificationBadge = view.findViewById(R.id.tv_notification_badge);

        // Butonlar
        btnStartGameMatch = view.findViewById(R.id.btn_start_game_match);
        btnStartGamePuzzle = view.findViewById(R.id.btn_start_game_puzzle); // Bu artık senin oyunun
        btnStartAi = view.findViewById(R.id.btn_start_ai);

        // Kullanıcı Profilini Gözlemle
        profileViewModel.getUserProfile().observe(getViewLifecycleOwner(), authResult -> {
            if (authResult instanceof AuthResultState.Loading) {
                tvWelcomeTitle.setText("Yükleniyor...");
                progressXp.setIndeterminate(true);
            } else if (authResult instanceof AuthResultState.Success) {
                User user = ((AuthResultState.Success) authResult).getUser();

                // GÜVENLİK KONTROLÜ: User nesnesi null ise UI güncellemesini atla.
                // Bu, veri tam yüklenmeden çökmesini engeller.
                if (user == null) {
                    return;
                }

                updateUiWithUser(user);
                updateNotificationBadge(user.getUnreadNotifications()); // Bildirim rozetini güncelle
                progressXp.setIndeterminate(false);

            } else if (authResult instanceof AuthResultState.Error) {
                Toast.makeText(getContext(), "Hata: " + ((AuthResultState.Error) authResult).getMessage(), Toast.LENGTH_LONG).show();
                navigateToSignIn();
            } else if (authResult instanceof AuthResultState.SignedOut || authResult instanceof AuthResultState.EmailNotVerified) {
                // Oturum kapalıysa veya e-posta doğrulanmamışsa giriş ekranına yönlendir.
                navigateToSignIn();
            }
        });


        setupListenersAndText();
    }

    private void setupListenersAndText() {
        String buttonText = String.format("%s<br><small><small>%s</small></small>",
                DAILY_LESSON_TITLE, DEFAULT_MODULE_PLACEHOLDER);
        btnStartDailyLesson.setText(android.text.Html.fromHtml(buttonText, android.text.Html.FROM_HTML_MODE_LEGACY));

        btnStartDailyLesson.setOnClickListener(v -> Toast.makeText(getContext(), "Günlük ders yakında!", Toast.LENGTH_SHORT).show());

        // Oyun 1: Kelime Eşleştirme
        btnStartGameMatch.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), GameActivity.class);
            startActivity(intent);
        });

        // --- SENİN OYUNUN ---
        // "Kelime Bulmaca" butonuna (btnStartGamePuzzle) basınca "Boşluk Doldurma" açılacak.
        btnStartGamePuzzle.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.sentenceGameFragment);
        });

        // AI Butonu
        btnStartAi.setOnClickListener(v ->
                Toast.makeText(getContext(), "AI Asistan ile sohbet yakında!", Toast.LENGTH_SHORT).show());

        // Badge güncellemesi için listener
        ivNotificationIcon.setOnClickListener(v -> {
            // Arkadaşlık istekleri sayfasına git
            NavHostFragment.findNavController(this)
                    .navigate(R.id.action_to_friend_requests);
        });

        ivNotificationIcon.setOnClickListener(v -> {
            NavHostFragment.findNavController(this).navigate(R.id.action_to_friend_requests);
        });

        // ilk bildirim sayısını çek
        profileViewModel.fetchUnreadNotificationsCount();


        btnLanguageSelector.setOnClickListener(v -> Toast.makeText(getContext(), "Dil seçimi", Toast.LENGTH_SHORT).show());
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

    private void updateNotificationBadge(int count) {
        if (count > 0) {
            tvNotificationBadge.setText(count > 99 ? "99+" : String.valueOf(count));
            tvNotificationBadge.setVisibility(View.VISIBLE);
        } else {
            tvNotificationBadge.setVisibility(View.GONE);
        }
    }
}