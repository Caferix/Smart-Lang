// Word.java dosyasının olması gereken hali

package com.scu.smartlang.domain.model; // Paket adınız bu olabilir

public class Word {
    private String id;
    private String englishWord;
    private String turkishMeaning;
    private String difficulty;

    // Constructor (Yapıcı Metot) - Bu sizde muhtemelen zaten var
    public Word(String id, String englishWord, String turkishMeaning, String difficulty) {
        this.id = id;
        this.englishWord = englishWord;
        this.turkishMeaning = turkishMeaning;
        this.difficulty = difficulty;
    }

    // --- HATA ALMANIZIN SEBEBİ BU METOTLARIN EKSİKLİĞİ ---
    // Aşağıdaki "getter" metotlarını sınıfınızın içine ekleyin

    public String getId() {
        return id;
    }

    public String getEnglishWord() {
        return englishWord;
    }

    public String getTurkishMeaning() {
        return turkishMeaning;
    }

    public String getDifficulty() {
        return difficulty;
    }

    // "Setter" metotları (İsteğe bağlı, şimdilik gerekmeyebilir)
    public void setId(String id) {
        this.id = id;
    }

    public void setEnglishWord(String englishWord) {
        this.englishWord = englishWord;
    }

    public void setTurkishMeaning(String turkishMeaning) {
        this.turkishMeaning = turkishMeaning;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }
}
