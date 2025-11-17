package com.scu.smartlang.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Geçici test verisi sağlayan sınıf.
 * Sadece aktif olan Eşleştirme Oyunu için veri sağlıyor.
 * Diğer oyun tipleri ileride kullanılmak üzere pasif (yorum satırı) hale getirildi.
 */
public class GameSampleData {

    // === WORD QUIZ (A1) ÖRNEKLERİ ===
    // İleride kullanılmak üzere pasifleştirildi.
    /*
    public static List<WordQuestion> getWordQuestionsA1() {
        List<WordQuestion> list = new ArrayList<>();

        list.add(new WordQuestion(
                "w1",
                "book",
                "kitap",
                Arrays.asList("masa", "kapı", "yazmak"),
                "A1",
                "daily"
        ));

        list.add(new WordQuestion(
                "w2",
                "table",
                "masa",
                Arrays.asList("kitap", "pencere", "kalem"),
                "A1",
                "daily"
        ));

        // ... (diğer word question'lar) ...

        return list;
    }
    */

    // === SENTENCE QUIZ (A1) ÖRNEKLERİ ===
    // İleride kullanılmak üzere pasifleştirildi.
    /*
    public static List<SentenceQuestion> getSentenceQuestionsA1() {
        List<SentenceQuestion> list = new ArrayList<>();

        list.add(new SentenceQuestion(
                "s1",
                "I usually ___ coffee in the morning.",
                "drink",
                Arrays.asList("play", "read", "watch"),
                "A1",
                "daily",
                ""  // hint şimdilik boş
        ));

        list.add(new SentenceQuestion(
                "s2",
                "She ___ to music every day.",
                "listens",
                Arrays.asList("drinks", "reads", "cooks"),
                "A1",
                "daily",
                ""
        ));

        // ... (diğer sentence question'lar) ...

        return list;
    }
    */

    // === MATCHING GAME (A1) ÖRNEĞİ ===
    // BU METOT AKTİF VE KULLANILIYOR
    public static List<MatchingQuestion> getMatchingQuestionsA1() {
        List<MatchingQuestion> list = new ArrayList<>();

        // Doğru eşleşmeleri Map ile tanımlıyoruz:
        Map<String, String> pairs1 = new HashMap<>();
        pairs1.put("book", "kitap");
        pairs1.put("table", "masa");
        pairs1.put("door", "kapı");
        pairs1.put("student", "öğrenci");

        MatchingQuestion q1 = new MatchingQuestion(
                "m1",
                "A1",
                pairs1
        );

        list.add(q1);

        return list;
    }
}