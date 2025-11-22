package com.scu.smartlang.presentation.viewmodel;

import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.scu.smartlang.R;
import com.scu.smartlang.domain.model.Difficulty;
import com.scu.smartlang.domain.model.SentenceGameSampleData;
import com.scu.smartlang.domain.model.SentenceQuestion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class SentenceGameViewModel extends ViewModel {

    // =========================
    // 🔧 SABİTLER
    // =========================
    private static final int LEVEL_XP_STEP = 15;
    private static final int SPECIAL_EVENT_XP = 75;
    private static final int EASY_COUNT = 6;
    private static final int MED_COUNT  = 3;
    private static final int HARD_COUNT = 1;

    // =========================
    // 🎮 OYUN VERİLERİ
    // =========================
    private final List<SentenceQuestion> questions = new ArrayList<>();
    private int currentIndex = 0;
    private boolean isInteractionLocked = false;

    private int currentXp = 0;
    private int currentLevel = 1;

    // =========================
    // 📡 LIVEDATA
    // =========================
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
    // 💉 CONSTRUCTOR
    // =========================
    @Inject
    public SentenceGameViewModel() {
        // Artık SharedPreferences yüklemiyoruz.
        // Direkt oyunu başlatıyoruz (Her zaman 0'dan başlar).
        startNewGame();
    }

    // =========================
    // 🚀 OYUN BAŞLATMA
    // =========================
    private void startNewGame() {
        // Değişkenleri sıfırla
        currentXp = 0;
        currentLevel = 1;
        currentIndex = 0;

        // UI güncelle
        _totalXp.setValue(0);
        _currentLevel.setValue(1);
        _currentProgress.setValue(0);

        // Soruları hazırla
        prepareQuestionList();
        publishCurrentQuestion();
    }

    // =========================
    // 🗑 OYUNU SIFIRLA (Tekrar Oyna için)
    // =========================
    public void restartGame() {
        // Zaten startNewGame her şeyi sıfırlıyor, onu çağırıyoruz.
        startNewGame();

        // Bitiş ekranını kapatmak için flag'leri resetle
        _quizFinished.setValue(false);
        _answerStatus.setValue(null);
    }

    // =========================
    // 🧩 SORU HAZIRLAMA
    // =========================
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

    // =========================
    // 👆 CEVAP KONTROLÜ
    // =========================
    public void onAnswerSelected(String selectedOption) {
        if (isInteractionLocked || Boolean.TRUE.equals(_quizFinished.getValue())) return;

        SentenceQuestion q = _currentQuestion.getValue();
        if (q == null) return;

        isInteractionLocked = true;
        boolean isCorrect = q.checkAnswer(selectedOption);
        _answerStatus.setValue(isCorrect);

        if (isCorrect) {
            processXpGain(q.getXpReward());
        } else {
            _soundEvent.setValue(R.raw.sound_wrong);
        }

        new Handler(Looper.getMainLooper()).postDelayed(this::advanceToNextQuestion, 1500);
    }

    private void processXpGain(int gainedXp) {
        int oldXp = currentXp;
        currentXp += gainedXp;
        _totalXp.setValue(currentXp);

        int oldLevel = (oldXp / LEVEL_XP_STEP) + 1;
        int newLevel = (currentXp / LEVEL_XP_STEP) + 1;

        _currentProgress.setValue(currentXp % LEVEL_XP_STEP);

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

        // saveProgress() metodunu sildik, artık kayıt yok.
    }

    private void advanceToNextQuestion() {
        currentIndex++;
        if (currentIndex < questions.size()) publishCurrentQuestion();
        else _quizFinished.setValue(true);
    }

    public void resetAnimationEvent() { _animationEvent.setValue(0); }
    public void resetSoundEvent()     { _soundEvent.setValue(0); }

    public int getCurrentIndex()         { return currentIndex; }
    public int getTotalQuestionCount()   { return questions.size(); }
}