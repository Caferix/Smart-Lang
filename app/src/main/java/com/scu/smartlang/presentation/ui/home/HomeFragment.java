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
import com.scu.smartlang.presentation.viewmodel.UserViewModel;
import com.google.android.material.button.MaterialButton;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class HomeFragment extends Fragment {

    private UserViewModel userViewModel;
    private TextView tvWelcomeTitle;
    private TextView tvUserLevelXp;
    private ProgressBar progressXp;
    private TextView tvStreakCount;
    private MaterialButton btnStartDailyLesson;
    private MaterialButton btnLanguageSelector;
    private MaterialButton btnStartGame;
    private ImageView ivNotificationIcon;

    private static final String DAILY_LESSON_TITLE = "GÜNLÜK DERSE BAŞLA";
    private static final String GAME_BUTTON_TITLE = "OYUN OYNA";
    private static final String DEFAULT_MODULE_PLACEHOLDER = "(Henüz ders atanmadı)";


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    /**
     * 1. DÜZELTME: Bu metot, fragment ekrana her geldiğinde (oyundan geri dönüldüğünde de) çalışır.
     */
    @Override
    public void onResume() {
        super.onResume();
        // Verilerin en güncel halini çekmek için ViewModel'a komut ver.
        if (userViewModel != null) {
            userViewModel.fetchUserProfile();
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);

        // View'ları bağla
        tvWelcomeTitle = view.findViewById(R.id.tv_welcome_title);
        tvUserLevelXp = view.findViewById(R.id.tv_user_level_xp);
        progressXp = view.findViewById(R.id.progress_xp);
        tvStreakCount = view.findViewById(R.id.tv_streak_count);
        btnStartDailyLesson = view.findViewById(R.id.btn_start_daily_lesson);
        ivNotificationIcon = view.findViewById(R.id.iv_notification_icon);
        btnLanguageSelector = view.findViewById(R.id.btn_language_selector);
        btnStartGame = view.findViewById(R.id.btn_start_game);


        // Kullanıcı Profilini Gözlemle
        userViewModel.getUserProfile().observe(getViewLifecycleOwner(), authResult -> {
            if (authResult instanceof AuthResultState.Loading) {
                tvWelcomeTitle.setText("Yükleniyor...");
                progressXp.setIndeterminate(true);

            } else if (authResult instanceof AuthResultState.Success) {
                User user = ((AuthResultState.Success) authResult).getUser();
                updateUiWithUser(user);
                progressXp.setIndeterminate(false);

            } else if (authResult instanceof AuthResultState.Error) {
                Toast.makeText(getContext(), "Profil yükleme hatası: " + ((AuthResultState.Error) authResult).getMessage(), Toast.LENGTH_LONG).show();
                navigateToSignIn();

            } else if (authResult instanceof AuthResultState.SignedOut || authResult instanceof AuthResultState.EmailNotVerified) {
                Toast.makeText(getContext(), "Oturum bulunamadı, lütfen tekrar giriş yapın.", Toast.LENGTH_SHORT).show();
                navigateToSignIn();
            }
        });

        // Buton ve Listener kurulumu
        setupListenersAndText();
    }

    private void setupListenersAndText() {
        String buttonText = String.format("%s<br><small><small>%s</small></small>",
                DAILY_LESSON_TITLE,
                DEFAULT_MODULE_PLACEHOLDER);

        btnStartDailyLesson.setText(android.text.Html.fromHtml(buttonText, android.text.Html.FROM_HTML_MODE_LEGACY));
        btnStartGame.setText(GAME_BUTTON_TITLE);

        btnStartDailyLesson.setOnClickListener(v -> Toast.makeText(getContext(), "Günlük derse başlama akışı!", Toast.LENGTH_SHORT).show());
        ivNotificationIcon.setOnClickListener(v -> Toast.makeText(getContext(), "Bildirimler açılıyor.", Toast.LENGTH_SHORT).show());
        btnLanguageSelector.setOnClickListener(v -> Toast.makeText(getContext(), "Dil seçme menüsü açılacak.", Toast.LENGTH_SHORT).show());

        btnStartGame.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), GameActivity.class);
            startActivity(intent);
        });
    }

    /**
     * 2. DÜZELTME: Artık `GameActivity`'deki gibi dinamik XP hesaplaması yapıyor.
     */
    private void updateUiWithUser(User user) {
        if (user == null) return;

        String userName = user.getUserName();
        String welcomeName = (userName != null && !userName.isEmpty()) ? userName : user.getEmail().split("@")[0];
        tvWelcomeTitle.setText(String.format("Hoş Geldin, %s!", welcomeName));

        // --- DİNAMİK XP HESAPLAMA MANTIĞI ---
        int currentLevel = user.getLevel();
        int totalXp = user.getXp(); // Toplam XP veritabanından geliyor

        int requiredXpForNextLevel = calculateRequiredXp(currentLevel);
        int xpForCurrentLevel = totalXp - calculateTotalXpForLevel(currentLevel);

        tvUserLevelXp.setText(String.format("Level %d | %d/%d XP", currentLevel, xpForCurrentLevel, requiredXpForNextLevel));
        progressXp.setMax(requiredXpForNextLevel);
        progressXp.setProgress(xpForCurrentLevel);
        // ---------------------------------

        tvStreakCount.setText("Seri: 0 Gün");
    }

    /**
     * 3. DÜZELTME: GameActivity'den kopyalanan yardımcı metotlar.
     */
    private int calculateTotalXpForLevel(int level) {
        int totalXp = 0;
        for (int i = 1; i < level; i++) {
            totalXp += calculateRequiredXp(i);
        }
        return totalXp;
    }

    private int calculateRequiredXp(int level) {
        return 100 + (level - 1) * 25;
    }

    private void navigateToSignIn() {
        if (isAdded()) {
            NavController navController = NavHostFragment.findNavController(this);
            NavOptions navOptions = new NavOptions.Builder()
                    .setPopUpTo(R.id.main_nav_graph, true)
                    .build();
            navController.navigate(R.id.signInFragment, null, navOptions);
        }
    }
}
