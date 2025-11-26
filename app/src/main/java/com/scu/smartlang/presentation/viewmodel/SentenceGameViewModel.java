package com.scu.smartlang.presentation.viewmodel;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.scu.smartlang.R;
import com.scu.smartlang.domain.model.Difficulty;
import com.scu.smartlang.domain.model.SentenceGameSampleData;
import com.scu.smartlang.domain.model.SentenceQuestion;
// ↓↓↓ FIREBASE İÇİN GEREKLİ İMPORTLAR ↓↓↓
import com.scu.smartlang.domain.repository.UserProfileRepository;
import com.scu.smartlang.domain.model.User;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;
import dagger.hilt.android.lifecycle.HiltViewModel;
import dagger.hilt.android.qualifiers.ApplicationContext;

@HiltViewModel
public class SentenceGameViewModel extends ViewModel {

    // --- SABİTLER ---
    private static final String PREF_NAME = "game_data";
    private static final String KEY_XP = "xp";
    private static final String KEY_LEVEL = "level";
    private static final String KEY_INDEX = "question_index";
    private static final String KEY_DATE = "last_played_date";

    private static final int LEVEL_XP_STEP = 15;
    private static final int SPECIAL_EVENT_XP = 75;
    private static final int EASY_COUNT = 6;
    private static final int MED_COUNT  = 3;
    private static final int HARD_COUNT = 1;

    private final SharedPreferences prefs;

    // 🔥 FIREBASE BAĞLANTISI İÇİN BU LAZIM 🔥
    private final UserProfileRepository userProfileRepository;

    // --- OYUN VERİLERİ ---
    private final List<SentenceQuestion> questions = new ArrayList<>();
    private int currentIndex = 0;
    private boolean isInteractionLocked = false;
    private int currentXp = 0;
    private int currentLevel = 1;

    // --- LIVEDATA ---
    private final MutableLiveData<SentenceQuestion> _currentQuestion = new MutableLiveData<>();
    private final MutableLiveData<List<String>> _options = new MutableLiveData<>();
    private final MutableLiveData<Integer> _totalXp = new MutableLiveData<>(0);
    private final MutableLiveData<Integer> _currentLevel = new MutableLiveData<>(1);
    private final MutableLiveData<Integer> _currentProgress = new MutableLiveData<>(0);
    private final MutableLiveData<Boolean> _answerStatus = new MutableLiveData<>();
    private final MutableLiveData<Boolean> _quizFinished = new MutableLiveData<>(false);
    private final MutableLiveData<Integer> _animationEvent = new MutableLiveData<>(0);
    private final MutableLiveData<Integer> _soundEvent = new MutableLiveData<>(0);

    public LiveData<SentenceQuestion> currentQuestion = _currentQuestion;
    public LiveData<List<String>> options = _options;
    public LiveData<Integer> totalXp = _totalXp;
    public LiveData<Integer> currentLevelLiveData = _currentLevel;
    public LiveData<Integer> currentProgress = _currentProgress;
    public LiveData<Boolean> answerStatus = _answerStatus;
    public LiveData<Boolean> quizFinished = _quizFinished;
    public LiveData<Integer> animationEvent = _animationEvent;
    public LiveData<Integer> soundEvent = _soundEvent;

    // =========================
    // 💉 CONSTRUCTOR (BAĞLANTI YERİ)
    // =========================
    @Inject
    public SentenceGameViewModel(
            @ApplicationContext Context context,
            UserProfileRepository userProfileRepository // <-- Firebase Deposunu Çağırdık
    ) {
        this.prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        this.userProfileRepository = userProfileRepository; // <-- Atadık

        prepareQuestionList();
        checkAndLoadProgress();
    }

    // =========================
    // 🔥 FIREBASE KAYIT METODU 🔥
    // =========================
    private void sendXpToFirebase(int earnedXp) {
        // 1. Kullanıcıyı buluttan getir
        userProfileRepository.getCurrentUserProfile()
                .thenAccept(user -> {
                    if (user != null) {
                        // 2. Mevcut puanına yeniyi ekle
                        int newTotalXp = user.getXp() + earnedXp;
                        user.setXp(newTotalXp);

                        // 3. Güncel halini geri gönder
                        userProfileRepository.updateUserProfile(user);
                    }
                })
                .exceptionally(e -> {
                    // İnternet yoksa veya hata varsa burası çalışır (Uygulama çökmez)
                    e.printStackTrace();
                    return null;
                });
    }

    // ... (STANDART METOTLAR - DEĞİŞMEDİ) ...

    private void checkAndLoadProgress() {
        String savedDate = prefs.getString(KEY_DATE, "");
        String todayDate = getTodayDate();
        if (savedDate.equals(todayDate)) {
            currentXp = prefs.getInt(KEY_XP, 0);
            currentLevel = prefs.getInt(KEY_LEVEL, 1);
            currentIndex = prefs.getInt(KEY_INDEX, 0);
        } else {
            resetGameData();
        }
        updateLiveData();
        if (currentIndex >= questions.size()) restartGame();
        else publishCurrentQuestion();
    }

    private void saveProgress() {
        prefs.edit()
                .putInt(KEY_XP, currentXp)
                .putInt(KEY_LEVEL, currentLevel)
                .putInt(KEY_INDEX, currentIndex)
                .putString(KEY_DATE, getTodayDate())
                .apply();
    }

    private void resetGameData() {
        currentXp = 0;
        currentLevel = 1;
        currentIndex = 0;
        prefs.edit().clear().putString(KEY_DATE, getTodayDate()).apply();
    }

    private String getTodayDate() {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
    }

    private void updateLiveData() {
        _totalXp.setValue(currentXp);
        _currentLevel.setValue(currentLevel);
        _currentProgress.setValue(currentXp % LEVEL_XP_STEP);
    }

    public void restartGame() {
        currentIndex = 0;
        _quizFinished.setValue(false);
        _answerStatus.setValue(null);
        prepareQuestionList();
        publishCurrentQuestion();
        saveProgress();
    }

    private void prepareQuestionList() {
        questions.clear();
        List<SentenceQuestion> poolEasy   = new ArrayList<>(SentenceGameSampleData.getQuestionsByDifficulty(Difficulty.EASY));
        List<SentenceQuestion> poolMedium = new ArrayList<>(SentenceGameSampleData.getQuestionsByDifficulty(Difficulty.MEDIUM));
        List<SentenceQuestion> poolHard   = new ArrayList<>(SentenceGameSampleData.getQuestionsByDifficulty(Difficulty.HARD));
        Collections.shuffle(poolEasy);
        Collections.shuffle(poolMedium);
        Collections.shuffle(poolHard);
        questions.addAll(poolEasy.subList(0, Math.min(EASY_COUNT, poolEasy.size())));
        questions.addAll(poolMedium.subList(0, Math.min(MED_COUNT, poolMedium.size())));
        questions.addAll(poolHard.subList(0, Math.min(HARD_COUNT, poolHard.size())));
        Collections.shuffle(questions);
    }

    private void publishCurrentQuestion() {
        isInteractionLocked = false;
        _answerStatus.setValue(null);
        if (currentIndex < questions.size()) {
            SentenceQuestion q = questions.get(currentIndex);
            _currentQuestion.setValue(q);
            _options.setValue(q.getShuffledOptions());
        } else {
            _quizFinished.setValue(true);
        }
    }

    public void onAnswerSelected(String selectedOption) {
        if (isInteractionLocked || Boolean.TRUE.equals(_quizFinished.getValue())) return;

        SentenceQuestion q = _currentQuestion.getValue();
        if (q == null) return;

        isInteractionLocked = true;
        boolean isCorrect = q.checkAnswer(selectedOption);
        _answerStatus.setValue(isCorrect);

        if (isCorrect) {
            int earnedXp = q.getXpReward();
            processXpGain(earnedXp);

            // 🔥 BURASI ÖNEMLİ: PUANI FIREBASE'E GÖNDERİYORUZ
            sendXpToFirebase(earnedXp);
        } else {
            _soundEvent.setValue(R.raw.sound_wrong);
        }

        new Handler(Looper.getMainLooper()).postDelayed(this::advanceToNextQuestion, 1500);
    }

    private void processXpGain(int gainedXp) {
        int oldXp = currentXp;
        currentXp += gainedXp;
        updateLiveData();
        int oldLevel = (oldXp / LEVEL_XP_STEP) + 1;
        int newLevel = (currentXp / LEVEL_XP_STEP) + 1;
        boolean didLevelUp = false;

        if (oldXp < SPECIAL_EVENT_XP && currentXp >= SPECIAL_EVENT_XP) {
            _animationEvent.setValue(2);
            _soundEvent.setValue(R.raw.sound_level_up);
            didLevelUp = true;
        }
        else if (newLevel > oldLevel) {
            currentLevel = newLevel;
            _currentLevel.setValue(currentLevel);
            _animationEvent.setValue(1);
            _soundEvent.setValue(R.raw.sound_level_up);
            didLevelUp = true;
        }

        if (!didLevelUp) {
            _soundEvent.setValue(R.raw.sound_correct);
        }
        saveProgress();
    }

    private void advanceToNextQuestion() {
        currentIndex++;
        saveProgress();
        if (currentIndex < questions.size()) publishCurrentQuestion();
        else _quizFinished.setValue(true);
    }

    public void resetAnimationEvent() { _animationEvent.setValue(0); }
    public void resetSoundEvent()     { _soundEvent.setValue(0); }
    public int getCurrentIndex()         { return currentIndex; }
    public int getTotalQuestionCount()   { return questions.size(); }
}