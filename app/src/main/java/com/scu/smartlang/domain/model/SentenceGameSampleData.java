package com.scu.smartlang.domain.model;

import com.scu.smartlang.domain.model.Difficulty;
import com.scu.smartlang.domain.model.SentenceQuestion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Sentence Game için GENİŞLETİLMİŞ SORU HAVUZU (60+ SORU)
 *
 * İçerik:
 * - Gramer Soruları (Tenses, Prepositions vs.)
 * - Mantık/Kelime Soruları (Bağlam, Anlam bilgisi)
 */
public class SentenceGameSampleData {

    private static final List<SentenceQuestion> allQuestions = new ArrayList<>();

    static {
        // =================================================================
        // EASY SORULAR (5 XP) - Temel Gramer & Basit Mantık
        // =================================================================

        // --- GRAMER ---
        allQuestions.add(new SentenceQuestion("I ___ to school every day.", "go", Arrays.asList("goes", "going", "gone"), Difficulty.EASY, 5));
        allQuestions.add(new SentenceQuestion("She ___ coffee in the morning.", "drinks", Arrays.asList("drink", "drinking", "drank"), Difficulty.EASY, 5));
        allQuestions.add(new SentenceQuestion("They ___ football right now.", "are playing", Arrays.asList("plays", "play", "played"), Difficulty.EASY, 5));
        allQuestions.add(new SentenceQuestion("We ___ a big house.", "have", Arrays.asList("has", "having", "had"), Difficulty.EASY, 5));
        allQuestions.add(new SentenceQuestion("He ___ fast.", "runs", Arrays.asList("run", "running", "ran"), Difficulty.EASY, 5));
        allQuestions.add(new SentenceQuestion("___ you speak English?", "Do", Arrays.asList("Are", "Does", "Is"), Difficulty.EASY, 5));
        allQuestions.add(new SentenceQuestion("Where ___ you from?", "are", Arrays.asList("is", "do", "does"), Difficulty.EASY, 5));
        allQuestions.add(new SentenceQuestion("Yesterday, I ___ a movie.", "watched", Arrays.asList("watch", "watching", "watches"), Difficulty.EASY, 5));
        allQuestions.add(new SentenceQuestion("She ___ not like pizza.", "does", Arrays.asList("do", "is", "has"), Difficulty.EASY, 5));
        allQuestions.add(new SentenceQuestion("Listen! The baby ___ crying.", "is", Arrays.asList("are", "do", "does"), Difficulty.EASY, 5));

        // --- MANTIK / KELİME (YENİ TİPLER) ---
        allQuestions.add(new SentenceQuestion("I take my ___ for a walk in the park.", "dog", Arrays.asList("fish", "bear", "cow"), Difficulty.EASY, 5));
        allQuestions.add(new SentenceQuestion("We use a ___ to cut paper.", "scissors", Arrays.asList("stone", "spoon", "fork"), Difficulty.EASY, 5));
        allQuestions.add(new SentenceQuestion("The sky is ___.", "blue", Arrays.asList("green", "yellow", "purple"), Difficulty.EASY, 5));
        allQuestions.add(new SentenceQuestion("Fish live in the ___.", "water", Arrays.asList("air", "fire", "tree"), Difficulty.EASY, 5));
        allQuestions.add(new SentenceQuestion("I brush my ___ every morning.", "teeth", Arrays.asList("ears", "knees", "shoes"), Difficulty.EASY, 5));
        allQuestions.add(new SentenceQuestion("A lemon is ___.", "sour", Arrays.asList("sweet", "salty", "spicy"), Difficulty.EASY, 5));
        allQuestions.add(new SentenceQuestion("In winter, the weather is ___.", "cold", Arrays.asList("hot", "warm", "sunny"), Difficulty.EASY, 5));
        allQuestions.add(new SentenceQuestion("A pilot flies a ___.", "plane", Arrays.asList("car", "bus", "ship"), Difficulty.EASY, 5));
        allQuestions.add(new SentenceQuestion("You need a ___ to open the door.", "key", Arrays.asList("pen", "apple", "book"), Difficulty.EASY, 5));
        allQuestions.add(new SentenceQuestion("Rabbits love to eat ___.", "carrots", Arrays.asList("meat", "fish", "pizza"), Difficulty.EASY, 5));
        allQuestions.add(new SentenceQuestion("I wear ___ on my head.", "hat", Arrays.asList("shoe", "glove", "sock"), Difficulty.EASY, 5));
        allQuestions.add(new SentenceQuestion("We sleep in a ___.", "bed", Arrays.asList("kitchen", "bathroom", "table"), Difficulty.EASY, 5));


        // =================================================================
        // MEDIUM SORULAR (10 XP) - Orta Seviye Gramer & Kelime
        // =================================================================

        // --- GRAMER ---
        allQuestions.add(new SentenceQuestion("I have been ___ English for two years.", "studying", Arrays.asList("study", "studied", "studies"), Difficulty.MEDIUM, 10));
        allQuestions.add(new SentenceQuestion("If it ___ tomorrow, we will stay at home.", "rains", Arrays.asList("rain", "rained", "is raining"), Difficulty.MEDIUM, 10));
        allQuestions.add(new SentenceQuestion("He suggested ___ to the cinema.", "going", Arrays.asList("to go", "go", "gone"), Difficulty.MEDIUM, 10));
        allQuestions.add(new SentenceQuestion("She is interested ___ learning Spanish.", "in", Arrays.asList("on", "at", "with"), Difficulty.MEDIUM, 10));
        allQuestions.add(new SentenceQuestion("I have ___ finished my homework.", "just", Arrays.asList("yet", "ever", "since"), Difficulty.MEDIUM, 10));
        allQuestions.add(new SentenceQuestion("You ___ smoke here. It is forbidden.", "mustn't", Arrays.asList("don't have to", "needn't", "couldn't"), Difficulty.MEDIUM, 10));
        allQuestions.add(new SentenceQuestion("This car was ___ in Germany.", "made", Arrays.asList("make", "making", "makes"), Difficulty.MEDIUM, 10));
        allQuestions.add(new SentenceQuestion("While I ___ TV, the electricity went out.", "was watching", Arrays.asList("watched", "am watching", "watch"), Difficulty.MEDIUM, 10));
        allQuestions.add(new SentenceQuestion("I am looking ___ to the weekend.", "forward", Arrays.asList("for", "after", "up"), Difficulty.MEDIUM, 10));

        // --- MANTIK / KELİME ---
        allQuestions.add(new SentenceQuestion("A doctor works in a ___.", "hospital", Arrays.asList("school", "park", "restaurant"), Difficulty.MEDIUM, 10));
        allQuestions.add(new SentenceQuestion("Before you eat, you should wash your ___.", "hands", Arrays.asList("car", "hair", "shoes"), Difficulty.MEDIUM, 10));
        allQuestions.add(new SentenceQuestion("The opposite of 'Expensive' is ___.", "Cheap", Arrays.asList("Rich", "Big", "Small"), Difficulty.MEDIUM, 10));
        allQuestions.add(new SentenceQuestion("You need a ___ to drive a car.", "license", Arrays.asList("ticket", "passport", "diploma"), Difficulty.MEDIUM, 10));
        allQuestions.add(new SentenceQuestion("A baker makes ___.", "bread", Arrays.asList("shoes", "cars", "furniture"), Difficulty.MEDIUM, 10));
        allQuestions.add(new SentenceQuestion("If you are thirsty, you should drink ___.", "water", Arrays.asList("bread", "salt", "cake"), Difficulty.MEDIUM, 10));
        allQuestions.add(new SentenceQuestion("You go to the library to ___ books.", "read", Arrays.asList("buy", "eat", "cook"), Difficulty.MEDIUM, 10));
        allQuestions.add(new SentenceQuestion("I keep my money in my ___.", "wallet", Arrays.asList("shoe", "hat", "book"), Difficulty.MEDIUM, 10));
        allQuestions.add(new SentenceQuestion("An umbrella protects you from ___.", "rain", Arrays.asList("wind", "snow", "sun"), Difficulty.MEDIUM, 10));
        allQuestions.add(new SentenceQuestion("The month after January is ___.", "February", Arrays.asList("March", "December", "April"), Difficulty.MEDIUM, 10));


        // =================================================================
        // HARD SORULAR (15 XP) - İleri Gramer & Zor Mantık
        // =================================================================

        // --- GRAMER ---
        allQuestions.add(new SentenceQuestion("If I had known, I ___ you.", "would have called", Arrays.asList("would call", "will call", "called"), Difficulty.HARD, 15));
        allQuestions.add(new SentenceQuestion("The man ___ car was stolen is here.", "whose", Arrays.asList("who", "whom", "which"), Difficulty.HARD, 15));
        allQuestions.add(new SentenceQuestion("No sooner had I arrived ___ the phone rang.", "than", Arrays.asList("when", "then", "that"), Difficulty.HARD, 15));
        allQuestions.add(new SentenceQuestion("Little ___ he know the truth.", "did", Arrays.asList("does", "was", "had"), Difficulty.HARD, 15));
        allQuestions.add(new SentenceQuestion("She is accused ___ stealing the money.", "of", Arrays.asList("for", "with", "on"), Difficulty.HARD, 15));
        allQuestions.add(new SentenceQuestion("By next year, I ___ graduated from university.", "will have", Arrays.asList("will be", "have", "had"), Difficulty.HARD, 15));
        allQuestions.add(new SentenceQuestion("___ it rains, we will cancel the picnic.", "Unless", Arrays.asList("If", "When", "As long as"), Difficulty.HARD, 15)); // Hileli soru (Unless/If mantığı)

        // --- MANTIK / KELİME ---
        allQuestions.add(new SentenceQuestion("The sun rises in the ___.", "East", Arrays.asList("West", "North", "South"), Difficulty.HARD, 15));
        allQuestions.add(new SentenceQuestion("An architect is someone who ___ buildings.", "designs", Arrays.asList("destroys", "paints", "cleans"), Difficulty.HARD, 15));
        allQuestions.add(new SentenceQuestion("The capital city of France is ___.", "Paris", Arrays.asList("London", "Berlin", "Madrid"), Difficulty.HARD, 15));
        allQuestions.add(new SentenceQuestion("If you break a mirror, it brings bad ___ (Superstition).", "luck", Arrays.asList("mood", "weather", "health"), Difficulty.HARD, 15));
        allQuestions.add(new SentenceQuestion("To 'let the cat out of the bag' means to reveal a ___.", "secret", Arrays.asList("animal", "gift", "problem"), Difficulty.HARD, 15));
        allQuestions.add(new SentenceQuestion("A thermometer measures ___.", "temperature", Arrays.asList("distance", "weight", "speed"), Difficulty.HARD, 15));
    }

    /**
     * ViewModel bu metodu çağırarak o zorluktaki TÜM soruları alır.
     * Sonra kendi içinde karıştırıp (shuffle) içinden gereken kadarını (6-3-1) seçer.
     */
    public static List<SentenceQuestion> getQuestionsByDifficulty(Difficulty difficulty) {
        List<SentenceQuestion> filteredList = new ArrayList<>();
        for (SentenceQuestion q : allQuestions) {
            if (q.getDifficulty() == difficulty) {
                filteredList.add(q);
            }
        }
        return filteredList;
    }
}