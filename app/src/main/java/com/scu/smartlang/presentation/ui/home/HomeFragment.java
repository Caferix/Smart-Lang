package com.scu.smartlang.presentation.ui.home;

import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
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
import com.scu.smartlang.domain.model.Word; // Word importunu ekleyin
import com.scu.smartlang.domain.model.WordSampleData; // Yeni oluşturduğumuz sınıfı import edin
import com.scu.smartlang.presentation.ui.auth.AuthResultState;
import com.scu.smartlang.presentation.viewmodel.ProfileViewModel;
import com.google.android.material.button.MaterialButton;

import java.util.List;
import java.util.Random;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class HomeFragment extends Fragment {

    private TextView tvWelcomeTitle;
    private TextView tvUserLevelXp;
    private ProgressBar progressXp;
    private MaterialButton btnStartGameMatch;
    private MaterialButton btnStartGamePuzzle;
    private MaterialButton btnStartAi;
    private ImageView ivNotificationIcon;
    private ProfileViewModel profileViewModel;

    private TextView tvEnglishWord;
    private TextView tvTurkishMeaning;

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
        ivNotificationIcon = view.findViewById(R.id.iv_notification_icon);
        tvEnglishWord = view.findViewById(R.id.tv_english_word);
        tvTurkishMeaning = view.findViewById(R.id.tv_turkish_meaning);

        // Butonlar
        btnStartGameMatch = view.findViewById(R.id.btn_start_game_match);
        btnStartGamePuzzle = view.findViewById(R.id.btn_start_game_puzzle);
        btnStartAi = view.findViewById(R.id.btn_start_ai);

        observeViewModel();
        setupListenersAndText();
        loadDailyWord();
    }

    private void loadDailyWord() {
        // Kelime listesini al
        List<Word> words = WordSampleData.getAllWords();

        if (words == null || words.isEmpty()) {
            tvEnglishWord.setText("HELLO");
            tvTurkishMeaning.setText("Merhaba");
            return;
        }

        // SharedPreferences: Basit verileri telefonda saklamak için kullanılır.
        // "DailyWordPrefs" adında bir dosya oluşturur.
        SharedPreferences prefs = requireActivity().getSharedPreferences("DailyWordPrefs", Context.MODE_PRIVATE);

        // Bugünün tarihini al (Format: 20231027 gibi)
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        String todayDate = sdf.format(new Date());

        // Telefonda kayıtlı olan tarihi ve kelime sırasını (index) al
        String savedDate = prefs.getString("saved_date", "");
        int savedIndex = prefs.getInt("saved_word_index", -1);

        int indexToUse;

        // Mantık: Eğer bugün, kaydedilen tarihten farklıysa VEYA hiç kayıt yoksa -> YENİ KELİME SEÇ
        if (!todayDate.equals(savedDate) || savedIndex == -1) {

            // Rastgele yeni bir sayı seç
            indexToUse = new Random().nextInt(words.size());

            // Yeni tarihi ve bu sayıyı hafızaya kaydet
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("saved_date", todayDate);
            editor.putInt("saved_word_index", indexToUse);
            editor.apply(); // Değişiklikleri uygula

        } else {
            // Tarihler aynı (yani gün değişmemiş) -> KAYITLI KELİMEYİ KULLAN
            indexToUse = savedIndex;

            // Güvenlik önlemi: Eğer kelime listesinin boyutu değiştiyse ve kayıtlı index sınır dışındaysa
            if (indexToUse >= words.size()) {
                indexToUse = 0;
            }
        }

        // Seçilen (veya kayıtlı) kelimeyi ekrana bas
        Word dailyWord = words.get(indexToUse);
        tvEnglishWord.setText(dailyWord.getEnglishWord().toUpperCase());
        tvTurkishMeaning.setText(dailyWord.getTurkishMeaning());
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
        // Oyun 1: Kelime Eşleştirme
        btnStartGameMatch.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), GameActivity.class);
            startActivity(intent);
        });

        // Boşluk Doldurma
        btnStartGamePuzzle.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.sentenceGameFragment);
        });

        // AI Sohbet
        btnStartAi.setOnClickListener(v ->
                NavHostFragment.findNavController(HomeFragment.this).navigate(R.id.action_navigation_home_to_aiChatFragment));

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
    }

    private void navigateToSignIn() {
        if (isAdded()) {
            NavController navController = NavHostFragment.findNavController(this);
            NavOptions navOptions = new NavOptions.Builder().setPopUpTo(R.id.main_nav_graph, true).build();
            navController.navigate(R.id.signInFragment, null, navOptions);
        }
    }
}