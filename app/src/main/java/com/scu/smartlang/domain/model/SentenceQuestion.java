package com.scu.smartlang.domain.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class SentenceQuestion {

    // 1. Benzersiz ID (Veritabanı takibi ve DiffUtil için)
    private final String id;

    // 2. Soru Verileri
    private final String sentence;        // Örn: "I ___ coffee."
    private final String correctAnswer;   // Örn: "drink"
    private final List<String> wrongOptions; // Örn: ["drinks", "drinking", "drank"]

    // 3. Oyunlaştırma Verileri
    private final Difficulty difficulty;
    private final int xpReward;

    // --- CONSTRUCTORS ---

    /**
     * Constructor 1: YENİ bir soru oluştururken kullanılır.
     * ID otomatik olarak rastgele oluşturulur.
     */
    public SentenceQuestion(String sentence,
                            String correctAnswer,
                            List<String> wrongOptions,
                            Difficulty difficulty,
                            int xpReward) {
        this(UUID.randomUUID().toString(), sentence, correctAnswer, wrongOptions, difficulty, xpReward);
    }

    /**
     * Constructor 2: VERİTABANINDAN (Firebase/Room) veri çekerken kullanılır.
     * Var olan ID korunur.
     */
    public SentenceQuestion(String id,
                            String sentence,
                            String correctAnswer,
                            List<String> wrongOptions,
                            Difficulty difficulty,
                            int xpReward) {
        this.id = id;
        this.sentence = sentence;
        this.correctAnswer = correctAnswer;
        // Defensive Copy: Dışarıdaki liste değişse bile burası bozulmasın diye kopyasını alıyoruz.
        this.wrongOptions = new ArrayList<>(wrongOptions);
        this.difficulty = difficulty;
        this.xpReward = xpReward;
    }

    // --- GETTER METOTLARI ---

    public String getId() {
        return id;
    }

    public String getSentence() {
        return sentence;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    // Listeyi korumalı (değiştirilemez) olarak dışarı veriyoruz.
    public List<String> getWrongOptions() {
        return Collections.unmodifiableList(wrongOptions);
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public int getXpReward() {
        return xpReward;
    }

    // --- OYUN MANTIĞI (Business Logic) ---

    /**
     * UI (Butonlar) için tüm şıkları (Doğru + Yanlışlar) tek listede karıştırıp döner.
     * Her çağrıldığında rastgele sıralama yapar.
     */
    public List<String> getShuffledOptions() {
        List<String> allOptions = new ArrayList<>(wrongOptions);
        allOptions.add(correctAnswer);
        Collections.shuffle(allOptions);
        return allOptions;
    }

    /**
     * Kullanıcının seçtiği cevabın doğruluğunu kontrol eder.
     * Büyük/küçük harf duyarlılığı yoktur (trim uygulanır).
     */
    public boolean checkAnswer(String selectedAnswer) {
        if (selectedAnswer == null) return false;
        return correctAnswer.trim().equalsIgnoreCase(selectedAnswer.trim());
    }

    // --- STANDART OVERRIDE'LAR (Performans ve Debug için) ---

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SentenceQuestion that = (SentenceQuestion) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "SentenceQuestion{" +
                "id='" + id + '\'' +
                ", sentence='" + sentence + '\'' +
                ", answer='" + correctAnswer + '\'' +
                ", difficulty=" + difficulty +
                '}';
    }
}
