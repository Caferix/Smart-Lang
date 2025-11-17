package com.scu.smartlang.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WordQuestion {

    private String id;
    private String word;                // İngilizce kelime
    private String correctAnswer;       // Doğru Türkçe anlam
    private List<String> wrongOptions;  // Yanlış ama mantıklı 3 seçenek
    private List<String> allOptions; // Yeni! Karışık tüm şıklar
    private String level;               // A1, A2, B1...
    private String category;            // daily, school, travel...
    private int points = 10;            // Default puan

    public WordQuestion(String id, String word, String correctAnswer, List<String> wrongOptions,
                        String level, String category) {
        this.id = id;
        this.word = word;
        this.correctAnswer = correctAnswer;
        this.wrongOptions = wrongOptions;
        this.level = level;
        this.category = category;


        // Bütün seçenekleri tek bir listede toplama ve karıştırma
        List<String> options = new ArrayList<>();
        options.add(correctAnswer);
        options.addAll(wrongOptions);
        Collections.shuffle(options);
        this.allOptions = options;
    }



    // Getter'lar
    public String getId() { return id; }
    public String getWord() { return word; }
    public String getCorrectAnswer() { return correctAnswer; }
    public List<String> getWrongOptions() { return wrongOptions; }
    public List<String> getAllOptions() { return allOptions; } // Yeni Getter
    public String getLevel() { return level; }
    public String getCategory() { return category; }
    public int getPoints() { return points; }

    // Setter
    public void setPoints(int points) {
        this.points = points;
    }
}
