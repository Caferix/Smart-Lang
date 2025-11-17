package com.scu.smartlang.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map; // Eşleştirmeyi tutmak için Map kullanacağız

/**
 * Kelime-anlam eşleştirme oyunu sorularını temsil eden sınıf.
 * Doğru eşleşme bilgisini bir Map'te tutar ve UI için karışık listeler üretir.
 */
public class MatchingQuestion {

    private String id;
    private String level; // İstatistik için (A1, A2...)
    private final int points = 1; // Not: Puan sabit +1

    // Doğru cevap anahtarı (Bu bilgi UI'da doğrudan gösterilmez)
    private Map<String, String> correctPairs;

    // UI'da gösterilecek KARMA (shuffled) listeler
    private List<String> englishWords; // Karışık İngilizce kelimeler (Sol sütun)
    private List<String> turkishWords; // Karışık Türkçe anlamlar (Sağ sütun)

    // === Constructor ===

    /**
     * MatchingQuestion nesnesi oluşturur.
     * Doğru eşleşmelerden (Map) yola çıkarak UI'da gösterilecek karışık listeleri hazırlar.
     *
     * @param id Soru ID'si
     * @param level Sorunun seviyesi (istatistik için)
     * @param correctPairs Doğru İngilizce-Türkçe eşleşmeleri içeren Map.
     */

    public MatchingQuestion(String id,
                            String level,
                            Map<String, String> correctPairs) {

        // --- Girdi Doğrulaması (YENİ EKLENEN KISIM) --
        if (correctPairs == null || correctPairs.isEmpty()) {
            throw new IllegalArgumentException("correctPairs null veya boş olamaz. ID: " + id);
        }


        this.id = id;
        this.level = level;
        this.correctPairs = correctPairs; // Doğru cevapları sakla




        // --- UI için Karışık Listeleri Oluşturma ---

        // 1. İngilizce kelimeleri (Key'ler) al ve karıştır
        List<String> enList = new ArrayList<>(correctPairs.keySet());
        Collections.shuffle(enList);
        this.englishWords = enList;

        // 2. Türkçe anlamları (Value'lar) al ve karıştır
        List<String> trList = new ArrayList<>(correctPairs.values());
        Collections.shuffle(trList);
        this.turkishWords = trList;
    }

    // === Mantık Metodu ===

    /**
     * Kullanıcının seçtiği iki kelimenin doğru eşleşme olup olmadığını kontrol eder.
     * @param selectedEnglish Kullanıcının seçtiği İngilizce kelime
     * @param selectedTurkish Kullanıcının seçtiği Türkçe kelime
     * @return Eşleşme doğru ise true, değilse false.
     */
    public boolean checkMatch(String selectedEnglish, String selectedTurkish) {
        // Map'ten doğru Türkçe karşılığı al:
        String correctTurkishAnswer = correctPairs.get(selectedEnglish);

        // Kullanıcının seçimiyle karşılaştır:
        return selectedTurkish.equals(correctTurkishAnswer);
    }

    // === Getter'lar ===

    public String getId() { return id; }
    public String getLevel() { return level; }
    public int getPoints() { return points; }

    // UI'ın kullanacağı karışık listeler:
    public List<String> getEnglishWords() { return englishWords; }
    public List<String> getTurkishWords() { return turkishWords; }
}