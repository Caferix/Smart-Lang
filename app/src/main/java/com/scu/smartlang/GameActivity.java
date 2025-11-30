// KODUN BAŞLANGICI
package com.scu.smartlang;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.button.MaterialButton;
import com.scu.smartlang.domain.model.User;
import com.scu.smartlang.domain.model.Word;
import com.scu.smartlang.presentation.ui.auth.AuthResultState;
import com.scu.smartlang.presentation.viewmodel.ProfileViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class GameActivity extends AppCompatActivity {

    // --- UI ve ViewModel Değişkenleri ---
    private ProfileViewModel profileViewModel;
    private TextView tvGameUserLevel, tvGameUserXp;
    private ImageView correctFeedback, wrongFeedback;
    private LinearLayout englishWordsColumn, turkishWordsColumn;
    private ProgressBar xpProgressBar;
    private LottieAnimationView levelUpAnimationView;
    private LinearLayout gameOverMenu;
    private MaterialButton btnNextRound, btnReturnHome;

    // --- Oyun Veri Listeleri ---
    private List<Word> allWords;
    private List<Word> gameWords;
    private List<String> englishWords;
    private List<String> turkishWords;

    // --- Oyun Durum Değişkenleri ---
    private MaterialButton selectedEnglishButton, selectedTurkishButton;
    private String selectedEnglishWord, selectedTurkishWord;
    private boolean isChecking = false;
    private int matchedPairs = 0;
    private final int wordsPerRound = 5;

    // --- İlerleme (Progression) Değişkenleri ---
    private User currentUser;
    private int sessionXpGain = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        initializeViews();
        setupListeners();
        profileViewModel.fetchUserProfile();
        setupUserProfileObserver();

        startNewRound();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        saveProgress();
    }

    private void initializeViews() {
        profileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        englishWordsColumn = findViewById(R.id.english_words_column);
        turkishWordsColumn = findViewById(R.id.turkish_words_column);
        tvGameUserLevel = findViewById(R.id.tv_game_user_level);
        tvGameUserXp = findViewById(R.id.tv_game_user_xp);
        correctFeedback = findViewById(R.id.iv_correct_feedback);
        wrongFeedback = findViewById(R.id.iv_wrong_feedback);
        xpProgressBar = findViewById(R.id.progress_xp_game);
        levelUpAnimationView = findViewById(R.id.lottie_level_up_animation);
        gameOverMenu = findViewById(R.id.game_over_menu);
        btnNextRound = findViewById(R.id.btn_next_round);
        btnReturnHome = findViewById(R.id.btn_return_home);
    }

    private void setupListeners() {
        btnReturnHome.setOnClickListener(v -> finish());
        btnNextRound.setOnClickListener(v -> startNewRound());
    }

    private void startNewRound() {
        matchedPairs = 0;
        gameOverMenu.setVisibility(View.GONE);
        resetAndShowAllWordButtons();
        prepareNewGame();

        if (gameWords.isEmpty()) {
            Toast.makeText(this, "Tebrikler! Tüm kelimeleri tamamladın.", Toast.LENGTH_LONG).show();
            btnNextRound.setEnabled(false);
            btnNextRound.setText("Tüm Kelimeler Bitti");
            showGameOverMenu();
        } else {
            populateButtons();
        }
    }

    private void setupUserProfileObserver() {
        profileViewModel.getUserProfile().observe(this, authResult -> {
            if (authResult instanceof AuthResultState.Success) {
                currentUser = ((AuthResultState.Success) authResult).getUser();
                if (currentUser != null) {
                    updateUserUi(currentUser, false);
                }
            } else if (authResult instanceof AuthResultState.Error) {
                Toast.makeText(this, "Kullanıcı verisi alınamadı.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUserUi(User user, boolean animate) {
        if (user == null) return;
        int currentLevel = user.getLevel();
        int totalXp = user.getXp();
        int requiredXp = calculateRequiredXp(currentLevel);
        int xpInCurrentLevel = totalXp - calculateTotalXpForLevel(currentLevel);

        tvGameUserLevel.setText(String.format("Level: %d", currentLevel));
        tvGameUserXp.setText(String.format("XP: %d/%d", xpInCurrentLevel, requiredXp));

        if (animate) {
            int previousXpInLevel = xpInCurrentLevel - 10;
            if (previousXpInLevel < 0) {
                int prevLevelRequiredXp = calculateRequiredXp(currentLevel - 1);
                previousXpInLevel = prevLevelRequiredXp + previousXpInLevel;
            }
            animateXpBar(previousXpInLevel, xpInCurrentLevel, requiredXp);
        } else {
            xpProgressBar.setMax(requiredXp);
            xpProgressBar.setProgress(xpInCurrentLevel);
        }
    }

    private void animateXpBar(int from, int to, int max) {
        xpProgressBar.setMax(max);
        ObjectAnimator.ofInt(xpProgressBar, "progress", from, to)
                .setDuration(800)
                .start();
    }

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

    private void checkForMatch() {
        isChecking = true;
        String correctTurkishMeaning = "";
        Word matchedWord = null;
        for (Word word : gameWords) {
            if (word.getEnglishWord().equals(selectedEnglishWord)) {
                correctTurkishMeaning = word.getTurkishMeaning();
                matchedWord = word;
                break;
            }
        }
        if (selectedTurkishWord.equals(correctTurkishMeaning) && matchedWord != null) {
            matchedPairs++;
            int oldLevel = currentUser.getLevel();
            int xpGainedThisTurn;
            switch (matchedWord.getDifficulty().toLowerCase()) {
                case "medium":
                    xpGainedThisTurn = 20;
                    break;
                case "hard":
                    xpGainedThisTurn = 30;
                    break;
                default:
                    xpGainedThisTurn = 10;
                    break;
            }
            currentUser.setXp(currentUser.getXp() + xpGainedThisTurn);
            sessionXpGain += xpGainedThisTurn;
            Log.d("GameLogic", "Doğru! Zorluk: " + matchedWord.getDifficulty() + ", Kazanılan XP: " + xpGainedThisTurn);

            boolean hasLeveledUp = false;
            int requiredXp = calculateRequiredXp(oldLevel);
            int xpInLevel = currentUser.getXp() - calculateTotalXpForLevel(oldLevel);
            if (xpInLevel >= requiredXp) {
                currentUser.setLevel(oldLevel + 1);
                hasLeveledUp = true;
                Log.d("GameLogic", "LEVEL UP! Yeni Level: " + currentUser.getLevel());
            }

            updateUserUi(currentUser, true);
            showFeedbackAnimation(correctFeedback, true, hasLeveledUp);
        } else {
            showFeedbackAnimation(wrongFeedback, false, false);
        }
    }

    private void saveProgress() {
        if (currentUser == null || sessionXpGain == 0) return;
        profileViewModel.updateUserProfile(currentUser);
    }

    private void populateButtons() {
        englishWordsColumn.removeAllViews();
        turkishWordsColumn.removeAllViews();

        for (String word : englishWords) {
            MaterialButton button = createWordButton(word);
            button.setOnClickListener(v -> handleEnglishWordClick((MaterialButton) v, word));
            englishWordsColumn.addView(button);
        }

        for (String word : turkishWords) {
            MaterialButton button = createWordButton(word);
            button.setOnClickListener(v -> handleTurkishWordClick((MaterialButton) v, word));
            turkishWordsColumn.addView(button);
        }
    }

    private MaterialButton createWordButton(String text) {
        MaterialButton button = new MaterialButton(this);
        button.setText(text);
        button.setTextColor(ContextCompat.getColor(this, R.color.game_button_text));
        button.setBackgroundColor(ContextCompat.getColor(this, R.color.game_button_bg));
        button.setStrokeColor(ContextCompat.getColorStateList(this, R.color.game_button_stroke));
        button.setStrokeWidth(dpToPx(2));
        button.setCornerRadius(dpToPx(24));
        button.setAllCaps(false);
        button.setTextSize(16);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dpToPx(70)
        );
        params.setMargins(0, 0, 0, dpToPx(12));
        button.setLayoutParams(params);

        return button;
    }

    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }

    private void handleEnglishWordClick(MaterialButton button, String word) {
        if (isChecking) return;
        if (selectedEnglishButton == button) {
            resetEnglishSelection();
            return;
        }
        if (selectedEnglishButton == null) {
            selectEnglishButton(button, word);
        }
    }

    private void handleTurkishWordClick(MaterialButton button, String word) {
        if (isChecking) return;
        if (selectedEnglishButton != null && selectedTurkishButton == null) {
            selectTurkishButton(button, word);
            checkForMatch();
        }
    }

    private void selectEnglishButton(MaterialButton button, String word) {
        selectedEnglishButton = button;
        selectedEnglishWord = word;
        button.animate().scaleX(1.05f).scaleY(1.05f).setDuration(200).start();
        button.setBackgroundColor(ContextCompat.getColor(this, R.color.game_button_selected_bg));
        button.setTextColor(ContextCompat.getColor(this, R.color.game_button_selected_text));
        button.setStrokeWidth(0);

        for (int i = 0; i < englishWordsColumn.getChildCount(); i++) {
            MaterialButton otherButton = (MaterialButton) englishWordsColumn.getChildAt(i);
            if (otherButton != selectedEnglishButton) {
                otherButton.setEnabled(false);
                otherButton.setAlpha(0.5f);
            }
        }
    }

    private void resetEnglishSelection() {
        if (selectedEnglishButton == null) return;
        selectedEnglishButton.animate().scaleX(1.0f).scaleY(1.0f).setDuration(200).start();
        for (int i = 0; i < englishWordsColumn.getChildCount(); i++) {
            MaterialButton button = (MaterialButton) englishWordsColumn.getChildAt(i);
            button.setEnabled(true);
            button.setAlpha(1.0f);
            button.setBackgroundColor(ContextCompat.getColor(this, R.color.game_button_bg));
            button.setTextColor(ContextCompat.getColor(this, R.color.game_button_text));
            button.setStrokeColor(ContextCompat.getColorStateList(this, R.color.game_button_stroke));
            button.setStrokeWidth(dpToPx(2));
        }
        selectedEnglishButton = null;
        selectedEnglishWord = null;
    }

    private void selectTurkishButton(MaterialButton button, String word) {
        selectedTurkishButton = button;
        selectedTurkishWord = word;
        button.animate().scaleX(1.05f).scaleY(1.05f).setDuration(200).start();
        button.setBackgroundColor(ContextCompat.getColor(this, R.color.game_button_selected_bg));
        button.setTextColor(ContextCompat.getColor(this, R.color.game_button_selected_text));
        button.setStrokeWidth(0);
        for (int i = 0; i < turkishWordsColumn.getChildCount(); i++) {
            ((MaterialButton) turkishWordsColumn.getChildAt(i)).setEnabled(false);
        }
    }

    private void showFeedbackAnimation(View feedbackView, boolean isCorrect, boolean hasLeveledUp) {
        feedbackView.setVisibility(View.VISIBLE);
        feedbackView.setScaleX(0.1f);
        feedbackView.setScaleY(0.1f);
        feedbackView.setAlpha(0.0f);
        feedbackView.animate()
                .alpha(1.0f)
                .scaleX(1.0f)
                .scaleY(1.0f)
                .setDuration(400)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        new Handler(Looper.getMainLooper()).postDelayed(() -> hideFeedbackAnimation(feedbackView, isCorrect, hasLeveledUp), 1000);
                    }
                })
                .start();
    }

    private void hideFeedbackAnimation(View feedbackView, boolean wasCorrect, boolean hasLeveledUp) {
        feedbackView.animate()
                .alpha(0.0f)
                .setDuration(400)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        feedbackView.setVisibility(View.GONE);
                        if (wasCorrect) {
                            hideMatchedButtons(selectedEnglishButton, selectedTurkishButton);
                            if (hasLeveledUp) {
                                playLevelUpAnimation();
                            }
                        } else {
                            resetAllSelections();
                        }
                    }
                })
                .start();
    }

    private void playLevelUpAnimation() {
        levelUpAnimationView.setVisibility(View.VISIBLE);
        levelUpAnimationView.playAnimation();
        levelUpAnimationView.addAnimatorListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                levelUpAnimationView.setVisibility(View.GONE);
                levelUpAnimationView.removeAnimatorListener(this);
                updateUserUi(currentUser, false);
            }
        });
    }

    private void hideMatchedButtons(View englishButton, View turkishButton) {
        ObjectAnimator alphaEnglish = ObjectAnimator.ofFloat(englishButton, "alpha", 1.0f, 0.0f);
        ObjectAnimator alphaTurkish = ObjectAnimator.ofFloat(turkishButton, "alpha", 1.0f, 0.0f);
        alphaEnglish.setDuration(500);
        alphaTurkish.setDuration(500);

        alphaEnglish.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                englishButton.setVisibility(View.GONE);
                resetAllSelections();

                if (matchedPairs >= wordsPerRound) {
                    showGameOverMenu();
                }
            }
        });

        alphaTurkish.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                turkishButton.setVisibility(View.INVISIBLE);
            }
        });
        alphaEnglish.start();
        alphaTurkish.start();
    }

    private void showGameOverMenu() {
        englishWordsColumn.setVisibility(View.GONE);
        turkishWordsColumn.setVisibility(View.GONE);
        gameOverMenu.setAlpha(0f);
        gameOverMenu.setVisibility(View.VISIBLE);
        gameOverMenu.animate().alpha(1f).setDuration(500).start();
    }

    private void resetAndShowAllWordButtons() {
        englishWordsColumn.setVisibility(View.VISIBLE);
        turkishWordsColumn.setVisibility(View.VISIBLE);
        resetAllSelections();
    }

    private void resetAllSelections() {
        resetEnglishSelection();
        if (turkishWordsColumn != null) {
            for (int i = 0; i < turkishWordsColumn.getChildCount(); i++) {
                MaterialButton button = (MaterialButton) turkishWordsColumn.getChildAt(i);
                button.setEnabled(true);
                button.setAlpha(1.0f);
                button.animate().scaleX(1.0f).scaleY(1.0f).setDuration(200).start();
                button.setBackgroundColor(ContextCompat.getColor(this, R.color.game_button_bg));
                button.setTextColor(ContextCompat.getColor(this, R.color.game_button_text));
                button.setStrokeColor(ContextCompat.getColorStateList(this, R.color.game_button_stroke));
                button.setStrokeWidth(dpToPx(2));
            }
        }
        selectedTurkishButton = null;
        selectedTurkishWord = null;
        isChecking = false;
    }

    private void prepareNewGame() {
        if (allWords == null) {
            createAllWordsList();
            Collections.shuffle(allWords);
        }
        int wordsToTake = Math.min(wordsPerRound, allWords.size());
        if (wordsToTake == 0) {
            gameWords = new ArrayList<>();
            return;
        }
        gameWords = new ArrayList<>(allWords.subList(0, wordsToTake));
        allWords.subList(0, wordsToTake).clear();
        englishWords = new ArrayList<>();
        turkishWords = new ArrayList<>();
        for (Word word : gameWords) {
            englishWords.add(word.getEnglishWord());
            turkishWords.add(word.getTurkishMeaning());
        }
        Collections.shuffle(turkishWords);
    }


    private void createAllWordsList() {
        allWords = new ArrayList<>();
        allWords.add(new Word("1", "Apple", "Elma", "easy"));
        allWords.add(new Word("2", "Book", "Kitap", "easy"));
        allWords.add(new Word("3", "Car", "Araba", "easy"));
        allWords.add(new Word("4", "House", "Ev", "easy"));
        allWords.add(new Word("5", "Table", "Masa", "easy"));
        allWords.add(new Word("6", "Water", "Su", "easy"));
        allWords.add(new Word("7", "Sky", "Gökyüzü", "easy"));
        allWords.add(new Word("8", "Mouse", "Fare", "easy"));
        allWords.add(new Word("9", "Headphone", "Kulaklık", "easy"));
        allWords.add(new Word("10", "Pencil", "Kalem", "easy"));
        allWords.add(new Word("11", "Flower", "Çiçek", "easy"));
        allWords.add(new Word("12", "Dog", "Köpek", "easy"));
        allWords.add(new Word("13", "Cat", "Kedi", "easy"));
        allWords.add(new Word("14", "Sun", "Güneş", "easy"));
        allWords.add(new Word("15", "Moon", "Ay", "easy"));
        allWords.add(new Word("16", "Star", "Yıldız", "easy"));
        allWords.add(new Word("17", "Chair", "Sandalye", "easy"));
        allWords.add(new Word("18", "Door", "Kapı", "easy"));
        allWords.add(new Word("19", "Window", "Pencere", "easy"));
        allWords.add(new Word("20", "Pen", "Tükenmez Kalem", "easy"));
        allWords.add(new Word("21", "Key", "Anahtar", "easy"));
        allWords.add(new Word("22", "Light", "Işık", "easy"));
        allWords.add(new Word("23", "Food", "Yemek", "easy"));
        allWords.add(new Word("24", "Drink", "İçecek", "easy"));
        allWords.add(new Word("25", "Time", "Zaman", "easy"));
        allWords.add(new Word("26", "Day", "Gün", "easy"));
        allWords.add(new Word("27", "Night", "Gece", "easy"));
        allWords.add(new Word("28", "Friend", "Arkadaş", "easy"));
        allWords.add(new Word("29", "Family", "Aile", "easy"));
        allWords.add(new Word("30", "Money", "Para", "easy"));
        allWords.add(new Word("31", "Work", "İş", "easy"));
        allWords.add(new Word("32", "School", "Okul", "easy"));
        allWords.add(new Word("33", "City", "Şehir", "easy"));
        allWords.add(new Word("34", "Tree", "Ağaç", "easy"));
        allWords.add(new Word("35", "Hand", "El", "easy"));
        allWords.add(new Word("36", "Foot", "Ayak", "easy"));
        allWords.add(new Word("37", "Eye", "Göz", "easy"));
        allWords.add(new Word("38", "Mouth", "Ağız", "easy"));
        allWords.add(new Word("39", "Heart", "Kalp", "easy"));
        allWords.add(new Word("40", "Small", "Küçük", "easy"));
        allWords.add(new Word("41", "Big", "Büyük", "easy"));
        allWords.add(new Word("42", "Happy", "Mutlu", "easy"));
        allWords.add(new Word("43", "Sad", "Üzgün", "easy"));
        allWords.add(new Word("44", "New", "Yeni", "easy"));
        allWords.add(new Word("45", "Old", "Eski", "easy"));
        allWords.add(new Word("46", "Good", "İyi", "easy"));
        allWords.add(new Word("47", "Bad", "Kötü", "easy"));
        allWords.add(new Word("48", "Come", "Gelmek", "easy"));
        allWords.add(new Word("49", "Go", "Gitmek", "easy"));
        allWords.add(new Word("50", "See", "Görmek", "easy"));
        allWords.add(new Word("51", "Environment", "Çevre", "medium"));
        allWords.add(new Word("52", "Technology", "Teknoloji", "medium"));
        allWords.add(new Word("53", "Development", "Geliştirme", "medium"));
        allWords.add(new Word("54", "Necessary", "Gerekli", "medium"));
        allWords.add(new Word("55", "Opportunity", "Fırsat", "medium"));
        allWords.add(new Word("56", "Government", "Hükümet", "medium"));
        allWords.add(new Word("57", "Information", "Bilgi", "medium"));
        allWords.add(new Word("58", "Experience", "Deneyim", "medium"));
        allWords.add(new Word("59", "Determine", "Belirlemek", "medium"));
        allWords.add(new Word("60", "Understand", "Anlamak", "medium"));
        allWords.add(new Word("61", "Suggest", "Önermek", "medium"));
        allWords.add(new Word("62", "Imagine", "Hayal Etmek", "medium"));
        allWords.add(new Word("63", "Improve", "Geliştirmek", "medium"));
        allWords.add(new Word("64", "Responsibility", "Sorumluluk", "medium"));
        allWords.add(new Word("65", "Customer", "Müşteri", "medium"));
        allWords.add(new Word("66", "Service", "Hizmet", "medium"));
        allWords.add(new Word("67", "Problem", "Sorun", "medium"));
        allWords.add(new Word("68", "Solution", "Çözüm", "medium"));
        allWords.add(new Word("69", "Difference", "Fark", "medium"));
        allWords.add(new Word("70", "Knowledge", "Bilgi, İrfan", "medium"));
        allWords.add(new Word("71", "Research", "Araştırma", "medium"));
        allWords.add(new Word("72", "Benefit", "Fayda", "medium"));
        allWords.add(new Word("73", "Effect", "Etki", "medium"));
        allWords.add(new Word("74", "Method", "Yöntem", "medium"));
        allWords.add(new Word("75", "Condition", "Durum, Koşul", "medium"));
        allWords.add(new Word("76", "Protect", "Korumak", "medium"));
        allWords.add(new Word("77", "Discuss", "Tartışmak", "medium"));
        allWords.add(new Word("78", "Provide", "Sağlamak", "medium"));
        allWords.add(new Word("79", "Require", "Gerektirmek", "medium"));
        allWords.add(new Word("80", "Manage", "Yönetmek", "medium"));
        allWords.add(new Word("81", "Achieve", "Ulaşmak, Başarmak", "medium"));
        allWords.add(new Word("82", "Successful", "Başarılı", "medium"));
        allWords.add(new Word("83", "Common", "Yaygın, Ortak", "medium"));
        allWords.add(new Word("84", "Serious", "Ciddi", "medium"));
        allWords.add(new Word("85", "Specific", "Özel, Belirli", "medium"));
        allWords.add(new Word("86", "Cultural", "Kültürel", "medium"));
        allWords.add(new Word("87", "Material", "Malzeme", "medium"));
        allWords.add(new Word("88", "Involve", "İçermek, Kapsamak", "medium"));
        allWords.add(new Word("89", "Maintain", "Sürdürmek", "medium"));
        allWords.add(new Word("90", "Observe", "Gözlemlemek", "medium"));
        allWords.add(new Word("91", "Produce", "Üretmek", "medium"));
        allWords.add(new Word("92", "Respect", "Saygı Duymak", "medium"));
        allWords.add(new Word("93", "Source", "Kaynak", "medium"));
        allWords.add(new Word("94", "Structure", "Yapı", "medium"));
        allWords.add(new Word("95", "Target", "Hedef", "medium"));
        allWords.add(new Word("96", "Variety", "Çeşitlilik", "medium"));
        allWords.add(new Word("97", "Global", "Küresel", "medium"));
        allWords.add(new Word("98", "Impact", "Etki", "medium"));
        allWords.add(new Word("99", "Explore", "Keşfetmek", "medium"));
        allWords.add(new Word("100", "Attempt", "Girişim, Deneme", "medium"));
        allWords.add(new Word("101", "Acquisition", "Edinim, Satın Alma", "hard"));
        allWords.add(new Word("102", "Consequence", "Sonuç, Akıbet", "hard"));
        allWords.add(new Word("103", "Exaggerate", "Abartmak", "hard"));
        allWords.add(new Word("104", "Ubiquitous", "Yaygın, Her Yerde Bulunan", "hard"));
        allWords.add(new Word("105", "Vulnerable", "Savunmasız, Hassas", "hard"));
        allWords.add(new Word("106", "Prerequisite", "Ön Koşul", "hard"));
        allWords.add(new Word("107", "Ineffable", "Anlatılmaz, Sözle İfade Edilemez", "hard"));
        allWords.add(new Word("108", "Paradigm", "Örnek, Model, Paradigma", "hard"));
        allWords.add(new Word("109", "Resilience", "Esneklik, Direnç", "hard"));
        allWords.add(new Word("110", "Conundrum", "Bilmece, Çıkmaz", "hard"));
        allWords.add(new Word("111", "Meticulous", "Titiz, Dikkatli", "hard"));
        allWords.add(new Word("112", "Eradicate", "Kökünü Kazımak, Yok Etmek", "hard"));
        allWords.add(new Word("113", "Perfunctory", "Özensiz, Alelade", "hard"));
        allWords.add(new Word("114", "Dichotomy", "İkilik, Karşıtlık", "hard"));
        allWords.add(new Word("115", "Ambivalent", "Çelişkili, Kararsız", "hard"));
        allWords.add(new Word("116", "Extrapolate", "Tahmin Etmek, Kestirim Yapmak", "hard"));
        allWords.add(new Word("117", "Capricious", "Maymun İştahlı, Değişken", "hard"));
        allWords.add(new Word("118", "Synergy", "Sinerji, İşbirliği", "hard"));
        allWords.add(new Word("119", "Obfuscate", "Kafa Karıştırmak, Bulanıklaştırmak", "hard"));
        allWords.add(new Word("120", "Inherent", "Doğuştan Gelen, Özünde Olan", "hard"));
        allWords.add(new Word("121", "Mitigate", "Hafifletmek, Azaltmak", "hard"));
        allWords.add(new Word("122", "Paucity", "Azlık, Kıtlık", "hard"));
        allWords.add(new Word("123", "Disseminate", "Yaymak, Dağıtmak", "hard"));
        allWords.add(new Word("124", "Ephemeral", "Kısa Ömürlü, Geçici", "hard"));
        allWords.add(new Word("125", "Recalcitrant", "İnatçı, Boyun Eğmez", "hard"));
        allWords.add(new Word("126", "Television", "Televizyon", "easy"));
        allWords.add(new Word("127", "Express", "İfade Etmek", "medium"));
        allWords.add(new Word("128", "Authentic", "Otantik, Gerçek", "hard"));
        allWords.add(new Word("129", "Beautiful", "Güzel", "easy"));
        allWords.add(new Word("130", "Construct", "İnşa Etmek", "medium"));
        allWords.add(new Word("131", "Turbulence", "Türbülans, Çalkantı", "hard"));
        allWords.add(new Word("132", "Street", "Sokak", "easy"));
        allWords.add(new Word("133", "Acknowledge", "Kabul Etmek, Onaylamak", "medium"));
        allWords.add(new Word("134", "Ameliorate", "İyileştirmek, Düzeltmek", "hard"));
        allWords.add(new Word("135", "Read", "Okumak", "easy"));
        allWords.add(new Word("136", "Acquire", "Elde Etmek", "medium"));
        allWords.add(new Word("137", "Fiduciary", "Vesayet, Güvene Dayalı", "hard"));
        allWords.add(new Word("138", "Listen", "Dinlemek", "easy"));
        allWords.add(new Word("139", "Campaign", "Kampanya", "medium"));
        allWords.add(new Word("140", "Intransigent", "Uzlaşmaz, İnatçı", "hard"));
        allWords.add(new Word("141", "Play", "Oynamak", "easy"));
        allWords.add(new Word("142", "Distinguish", "Ayırt Etmek", "medium"));
        allWords.add(new Word("143", "Surreptitious", "Gizli, Sinsice", "hard"));
        allWords.add(new Word("144", "Run", "Koşmak", "easy"));
        allWords.add(new Word("145", "Volatile", "Uçucu, İstikrarsız", "medium"));
        allWords.add(new Word("146", "Misanthrope", "İnsan Sevmez", "hard"));
        allWords.add(new Word("147", "Walk", "Yürümek", "easy"));
        allWords.add(new Word("148", "Hypothesis", "Hipotez", "medium"));
        allWords.add(new Word("149", "Maelstrom", "Girdap, Kargaşa", "hard"));
        allWords.add(new Word("150", "Music", "Müzik", "easy"));
        allWords.add(new Word("151", "Crucial", "Çok Önemli", "medium"));
        allWords.add(new Word("152", "Inscrutable", "Anlaşılmaz, Gizemli", "hard"));
        allWords.add(new Word("153", "Morning", "Sabah", "easy"));
        allWords.add(new Word("154", "Perspective", "Bakış Açısı", "medium"));
        allWords.add(new Word("155", "Zenith", "Zirve, Doruk Noktası", "hard"));
        allWords.add(new Word("156", "Afternoon", "Öğleden Sonra", "easy"));
        allWords.add(new Word("157", "Cooperate", "İşbirliği Yapmak", "medium"));
        allWords.add(new Word("158", "Taciturn", "Az Konuşan, Sessiz", "hard"));
        allWords.add(new Word("159", "Evening", "Akşam", "easy"));
        allWords.add(new Word("160", "Integrate", "Bütünleştirmek", "medium"));
        allWords.add(new Word("161", "Quotidian", "Günlük, Sıradan", "hard"));
        allWords.add(new Word("162", "Brother", "Erkek Kardeş", "easy"));
        allWords.add(new Word("163", "Justify", "Haklı Çıkarmak", "medium"));
        allWords.add(new Word("164", "Mendacious", "Yalancı, Yanıltıcı", "hard"));
        allWords.add(new Word("165", "Sister", "Kız Kardeş", "easy"));
        allWords.add(new Word("166", "Negotiate", "Müzakere Etmek", "medium"));
        allWords.add(new Word("167", "Sepulchral", "Mezar gibi, Kasvetli", "hard"));
        allWords.add(new Word("168", "Cold", "Soğuk", "easy"));
        allWords.add(new Word("169", "Predict", "Tahmin Etmek", "medium"));
        allWords.add(new Word("170", "Clandestine", "Gizli, Saklı", "hard"));
        allWords.add(new Word("171", "Hot", "Sıcak", "easy"));
        allWords.add(new Word("172", "Substitute", "Yerine Koymak, Yedek", "medium"));
        allWords.add(new Word("173", "Parsimonious", "Cimri, Tutumlu", "hard"));
        allWords.add(new Word("174", "Near", "Yakın", "easy"));
        allWords.add(new Word("175", "Transform", "Dönüştürmek", "medium"));
        allWords.add(new Word("176", "Mellifluous", "Bal gibi tatlı, Akıcı (Ses)", "hard"));
        allWords.add(new Word("177", "Far", "Uzak", "easy"));
        allWords.add(new Word("178", "Validate", "Onaylamak, Geçerli Kılmak", "medium"));
        allWords.add(new Word("179", "Lugubrious", "Kederli, Hüzünlü", "hard"));
        allWords.add(new Word("180", "Quick", "Hızlı", "easy"));
        allWords.add(new Word("181", "Disrupt", "Aksatmak, Bozmak", "medium"));
        allWords.add(new Word("182", "Pulchritude", "Fiziksel Güzellik", "hard"));
        allWords.add(new Word("183", "Slow", "Yavaş", "easy"));
        allWords.add(new Word("184", "Allocate", "Tahsis Etmek", "medium"));
        allWords.add(new Word("185", "Sycophant", "Dalkavuk", "hard"));
        allWords.add(new Word("186", "Color", "Renk", "easy"));
        allWords.add(new Word("187", "Initiative", "Girişim", "medium"));
        allWords.add(new Word("188", "Misanthrope", "İnsan Sevmez", "hard"));
        allWords.add(new Word("189", "Paper", "Kağıt", "easy"));
        allWords.add(new Word("190", "Sustainable", "Sürdürülebilir", "medium"));
        allWords.add(new Word("191", "Pneumatic", "Hava Basınçlı", "hard"));
        allWords.add(new Word("192", "Film", "Film", "easy"));
        allWords.add(new Word("193", "Clarify", "Açıklığa Kavuşturmak", "medium"));
        allWords.add(new Word("194", "Malignant", "Kötü Huylu, Zararlı", "hard"));
        allWords.add(new Word("195", "Sport", "Spor", "easy"));
        allWords.add(new Word("196", "Formulate", "Formüle Etmek", "medium"));
        allWords.add(new Word("197", "Incipient", "Başlangıç Aşaması", "hard"));
        allWords.add(new Word("198", "Hobby", "Hobi", "easy"));
        allWords.add(new Word("199", "Demonstrate", "Gösteri Yapmak, Kanıtlamak", "medium"));
        allWords.add(new Word("200", "Lassitude", "Yorgunluk, Bitkinlik", "hard"));
    }

}
