package com.scu.smartlang.presentation.ui.home;// KODUN BAŞLANGICI - Bu satırdan itibaren kopyalayınpackage com.scu.smartlang.presentation.ui.home;

// GEREKLİ KÜTÜPHANELERİ EKLEDİM (Intent)
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
// GameActivity'yi burada import ettim
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
                AuthResultState.Success success = (AuthResultState.Success) authResult;
                User user = success.getUser();
                updateUiWithUser(user);
                progressXp.setIndeterminate(false);

            } else if (authResult instanceof AuthResultState.Error) {
                AuthResultState.Error error = (AuthResultState.Error) authResult;
                Toast.makeText(getContext(), "Profil yükleme hatası: " + error.getMessage(), Toast.LENGTH_LONG).show();
                // Hata durumunda da giriş ekranına yönlendirdik
                navigateToSignIn();

            } else if (authResult instanceof AuthResultState.SignedOut || authResult instanceof AuthResultState.EmailNotVerified) {
                // Oturum yoksa veya e-posta doğrulanmamışsa (fetchUserProfile bunu da kontrol ediyor)
                // HomeFragment'ta kalmanın anlamı yok. Giriş ekranına geri dön.
                Toast.makeText(getContext(), "Oturum bulunamadı, lütfen tekrar giriş yapın.", Toast.LENGTH_SHORT).show();
                navigateToSignIn();
            }
        });

        // Profil verisini çek
        //userViewModel.fetchUserProfile();

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

        // ******************** DEĞİŞİKLİK BURADA YAPILDI ********************
        // Eski Toast mesajı yerine yeni sayfaya geçiş kodunu ekledim.
        btnStartGame.setOnClickListener(v -> {
            // Yeni GameActivity'yi (boş ekranı) açmak için bir "niyet" (Intent) oluşturuyoruz
            Intent intent = new Intent(getActivity(), GameActivity.class);
            // Yeni ekranı başlatıyoruz
            startActivity(intent);
        });
        // ******************** DEĞİŞİKLİK SONA ERDİ ********************
    }


    /**
     * Kullanıcı verileri ile UI'ı günceller.
     * @param user Güncellenecek kullanıcı modeli
     */
    private void updateUiWithUser(User user) {
        String userName = user.getUserName();
        int currentXp = user.getXp();
        int currentLevel = user.getLevel();
        String welcomeName = (userName != null && !userName.isEmpty()) ? userName : user.getEmail().split("@")[0];
        int progressPercent = (currentXp % 100);
        int xpTarget = 100;

        tvWelcomeTitle.setText(String.format("Hoş Geldin, %s!", welcomeName));
        tvUserLevelXp.setText(String.format("Level %d | %d/%d XP", currentLevel, progressPercent, xpTarget));
        progressXp.setMax(xpTarget);
        progressXp.setProgress(progressPercent);
        tvStreakCount.setText("Seri: 0 Gün");
    }

    private void navigateToSignIn() {
        // NavController'ı tekrar al (eğer null olabilme ihtimali varsa)
        NavController navController = NavHostFragment.findNavController(this);

        // Geri yığınını (back stack) temizleyerek SignInFragment'a git
        NavOptions navOptions = new NavOptions.Builder()
                .setPopUpTo(R.id.main_nav_graph, true)
                .build();
        navController.navigate(R.id.signInFragment, null, navOptions);
    }
}
// KODUN SONU - Bu satıra kadar kopyalayın
