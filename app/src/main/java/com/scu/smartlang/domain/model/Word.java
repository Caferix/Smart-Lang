package com.scu.smartlang.domain.model;

public class Word {
    private String id;
    private String word; // kelime
    private String matchWord; // karşılığı
    private String difficulty; // zorluk derecesi

    public Word() {} // Boş constructor (JSON dönüşümü için)

    public Word(String id, String word, String matchWord, String difficulty) {
        this.id = id;
        this.word = word;
        this.matchWord = matchWord;
        this.difficulty = difficulty;
    }

    // Getter Setter
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getMatchWord() {
        return matchWord;
    }

    public void setMatchWord(String matchWord) {
        this.matchWord = matchWord;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }
}
