package com.scu.smartlang.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Boşluk doldurma (Sentence Completion) tipi soruları temsil eden sınıf.
 * Dil bilgisi veya anlam odaklı testler için kullanılır.
 */
public class SentenceQuestion {

    // === Alanlar (Fields) ===

    private String id;              // Soru setinden benzersiz tanımlayıcı ID.
    private String sentence;        // Boşluk içeren cümle: "I usually ___ coffee..."
    private String correctAnswer;   // Boşluğu doldurması gereken doğru kelime / ifade.
    private List<String> wrongOptions; // Doğru cevaba alternatif, yanlış ama mantıklı 3 seçenek.
    private List<String> allOptions; // UI'da gösterilecek KARMA (shuffled) tüm 4 seçenek.
    private String level;           // Sorunun zorluk seviyesi (A1, B2 vb.).
    private String category;        // Sorunun konusu/kategorisi (daily, travel, school...).
    private String hint;            // Opsiyonel ipucu (yanlış cevapta gösterilebilir).
    private int points = 15;        // Bu sorunun doğru cevaplanmasıyla kazanılacak puan.

    // === Constructor ===

    /**
     * SentenceQuestion nesnesi oluşturur ve şıkları rastgele karıştırır.
     */
    public SentenceQuestion(String id,
                            String sentence,
                            String correctAnswer,
                            List<String> wrongOptions,
                            String level,
                            String category,
                            String hint) {
        // Gelen değerleri sınıf değişkenlerine atama
        this.id = id;
        this.sentence = sentence;
        this.correctAnswer = correctAnswer;
        this.wrongOptions = wrongOptions;
        this.level = level;
        this.category = category;
        this.hint = hint;

        // Bütün seçenekleri tek bir listede toplama ve karıştırma (UI için hazırlık)
        List<String> options = new ArrayList<>();
        options.add(correctAnswer);       // Doğru cevabı ekle
        options.addAll(wrongOptions);     // Yanlış cevapları ekle
        Collections.shuffle(options);     // Listeyi rastgele karıştır
        this.allOptions = options;        // Karışık listeyi sakla
    }

    // === Getter'lar ===

    public String getId() { return id; }
    public String getSentence() { return sentence; }
    public String getCorrectAnswer() { return correctAnswer; }
    public List<String> getWrongOptions() { return wrongOptions; }
    public List<String> getAllOptions() { return allOptions; } // Karışık şıklar listesini döndürür.
    public String getLevel() { return level; }
    public String getCategory() { return category; }
    public String getHint() { return hint; }
    public int getPoints() { return points; }

    // Setter (Puanı değiştirmek için)
    /**
     * Sorunun puan değerini dışarıdan ayarlama imkanı sağlar (Örn: Zorluğa göre puanlama).
     */
    public void setPoints(int points) {
        this.points = points;
    }
}