package com.scu.smartlang.game;

// JUnit testleri için gerekli import'lar
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.List;

/**
 * Bu bir JUnit test sınıfıdır.
 * Sadece aktif olan Eşleştirme (Matching) oyunu test ediliyor.
 * Diğer oyun tipleri ileride kullanılmak üzere pasif (yorum satırı) hale getirildi.
 */
public class GameDataTest {

    // === İleride kullanılmak üzere pasifleştirildi ===
    /*
    @Test
    public void wordQuestion_DataLoadsCorrectly() {
        System.out.println("--- WordQuestion Testi ---");
        // List<WordQuestion> wordList = GameSampleData.getWordQuestionsA1();
        // assertFalse("Kelime listesi boş gelmemeli", wordList.isEmpty());
        // ... (diğer testler) ...
    }
    */

    // === İleride kullanılmak üzere pasifleştirildi ===
    /*
    @Test
    public void sentenceQuestion_DataLoadsCorrectly() {
        System.out.println("\n--- SentenceQuestion Testi ---");
        // List<SentenceQuestion> sentenceList = GameSampleData.getSentenceQuestionsA1();
        // assertFalse("Cümle listesi boş gelmemeli", sentenceList.isEmpty());
        // ... (diğer testler) ...
    }
    */

    /**
     * BU TEST AKTİF VE ÇALIŞIYOR
     * GameSampleData'dan eşleştirme verilerini çekip,
     * MatchingQuestion modelinin mantığının (checkMatch) doğru çalışıp çalışmadığını kontrol eder.
     */
    @Test
    public void matchingQuestion_LogicWorksCorrectly() {
        System.out.println("\n--- MatchingQuestion Testi ---");

        // 1. Veriyi al
        List<MatchingQuestion> matchingList = GameSampleData.getMatchingQuestionsA1();

        // 2. Veri var mı diye kontrol et
        assertFalse("Eşleştirme listesi (GameSampleData'dan) boş gelmemeli", matchingList.isEmpty());

        // 3. İlk soruyu al
        MatchingQuestion firstMatch = matchingList.get(0);

        // 4. Görsel kontrol için konsola yazdır
        System.out.println("Sol Sütun (EN): " + firstMatch.getEnglishWords());
        System.out.println("Sağ Sütun (TR): " + firstMatch.getTurkishWords());

        // 5. Mantık Testi: 'checkMatch' metodu doğru çalışıyor mu?

        // 'book' ve 'kitap' eşleşmesi DOĞRU (true) olmalı
        assertTrue("('book', 'kitap') eşleşmesi DOĞRU olmalı",
                firstMatch.checkMatch("book", "kitap"));

        // 'book' ve 'masa' eşleşmesi YANLIŞ (false) olmalı
        assertFalse("('book', 'masa') eşleşmesi YANLIŞ olmalı",
                firstMatch.checkMatch("book", "masa"));
    }
}