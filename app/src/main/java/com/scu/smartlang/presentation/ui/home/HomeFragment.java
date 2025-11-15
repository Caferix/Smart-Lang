package com.scu.smartlang.presentation.ui.home;

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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

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
                AuthResultState.Success success = (AuthResultState.Success) authResult;
                User user = success.getUser();
                updateUiWithUser(user);
                progressXp.setIndeterminate(false);
            } else if (authResult instanceof AuthResultState.Error) {
                AuthResultState.Error error = (AuthResultState.Error) authResult;
                Toast.makeText(getContext(), "Profil yükleme hatası: " + error.getMessage(), Toast.LENGTH_LONG).show();
                tvWelcomeTitle.setText("Hoş Geldin!");
                progressXp.setIndeterminate(false);
            }
        });

        // Profil verisini çek
        userViewModel.fetchUserProfile();

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

        btnLanguageSelector.setOnClickListener(v -> Toast.makeText(getContext(), "Dil seçme menüsü açılacak (Çoklu dil desteği yakında!)", Toast.LENGTH_SHORT).show());
        btnStartGame.setOnClickListener(v -> Toast.makeText(getContext(), "Oyun modülü başlatılıyor!", Toast.LENGTH_SHORT).show());
    }


    /**
     * Kullanıcı verileri ile UI'ı günceller.
     */
    private void updateUiWithUser(User user) {
        String userName = user.getUserName();
        int currentXp = user.getXp();

        // Level, User.java'da int olarak tanımlı olduğu için doğrudan kullanıldı
        int currentLevel = user.getLevel();

        String welcomeName = (userName != null && !userName.isEmpty()) ? userName : user.getEmail().split("@")[0];

        tvWelcomeTitle.setText(String.format("Hoş Geldin, %s!", welcomeName));

        int progressPercent = (currentXp % 100);
        int xpTarget = 100;

        tvUserLevelXp.setText(String.format("Level %d | %d/%d XP", currentLevel, progressPercent, xpTarget));
        progressXp.setMax(xpTarget);
        progressXp.setProgress(progressPercent);

        tvStreakCount.setText("Seri: 0 Gün");
    }
}