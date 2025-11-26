package com.scu.smartlang.domain.model;

import com.scu.smartlang.domain.model.Difficulty;
import com.scu.smartlang.domain.model.SentenceQuestion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Sentence Game için MEGA SORU HAVUZU (100+ SORU)
 *
 * İçerik:
 * - Gramer (A1 - C1)
 * - Kelime Bilgisi (Vocabulary)
 * - Mantık ve Genel Kültür
 */
public class SentenceGameSampleData {

    private static final List<SentenceQuestion> allQuestions = new ArrayList<>();

    static {
        // =================================================================
        // EASY SORULAR (5 XP) - (A1-A2 Gramer & Temel Kelimeler)
        // =================================================================

        // --- Fiiller & Zamanlar ---
        allQuestions.add(new SentenceQuestion("I ___ to school every day.", "go", Arrays.asList("goes", "going", "gone"), Difficulty.EASY, 5));
        allQuestions.add(new SentenceQuestion("She ___ coffee in the morning.", "drinks", Arrays.asList("drink", "drinking", "drank"), Difficulty.EASY, 5));
        allQuestions.add(new SentenceQuestion("They ___ football right now.", "are playing", Arrays.asList("plays", "play", "played"), Difficulty.EASY, 5));
        allQuestions.add(new SentenceQuestion("We ___ a big house.", "have", Arrays.asList("has", "having", "had"), Difficulty.EASY, 5));
        allQuestions.add(new SentenceQuestion("He ___ fast.", "runs", Arrays.asList("run", "running", "ran"), Difficulty.EASY, 5));
        allQuestions.add(new SentenceQuestion("Yesterday, I ___ a movie.", "watched", Arrays.asList("watch", "watching", "watches"), Difficulty.EASY, 5));
        allQuestions.add(new SentenceQuestion("___ you speak English?", "Do", Arrays.asList("Are", "Does", "Is"), Difficulty.EASY, 5));
        allQuestions.add(new SentenceQuestion("Where ___ you from?", "are", Arrays.asList("is", "do", "does"), Difficulty.EASY, 5));
        allQuestions.add(new SentenceQuestion("She ___ not like pizza.", "does", Arrays.asList("do", "is", "has"), Difficulty.EASY, 5));
        allQuestions.add(new SentenceQuestion("Listen! The baby ___ crying.", "is", Arrays.asList("are", "do", "does"), Difficulty.EASY, 5));
        allQuestions.add(new SentenceQuestion("I ___ born in 1995.", "was", Arrays.asList("am", "were", "be"), Difficulty.EASY, 5));
        allQuestions.add(new SentenceQuestion("They ___ at home yesterday.", "were", Arrays.asList("was", "are", "is"), Difficulty.EASY, 5));
        allQuestions.add(new SentenceQuestion("I will ___ you tomorrow.", "call", Arrays.asList("calling", "called", "calls"), Difficulty.EASY, 5));
        allQuestions.add(new SentenceQuestion("Can you ___ me?", "help", Arrays.asList("helping", "helps", "to help"), Difficulty.EASY, 5));
        allQuestions.add(new SentenceQuestion("Let's ___ to the park.", "go", Arrays.asList("going", "goes", "went"), Difficulty.EASY, 5));

        // --- Kelime & Mantık (Nesneler, Hayvanlar, Renkler) ---
        allQuestions.add(new SentenceQuestion("The sky is ___.", "blue", Arrays.asList("green", "yellow", "purple"), Difficulty.EASY, 5));
        allQuestions.add(new SentenceQuestion("A banana is ___.", "yellow", Arrays.asList("red", "blue", "pink"), Difficulty.EASY, 5));
        allQuestions.add(new SentenceQuestion("Grass is ___.", "green", Arrays.asList("red", "purple", "orange"), Difficulty.EASY, 5));
        allQuestions.add(new SentenceQuestion("Snow is ___.", "white", Arrays.asList("black", "green", "red"), Difficulty.EASY, 5));
        allQuestions.add(new SentenceQuestion("I take my ___ for a walk.", "dog", Arrays.asList("fish", "bear", "cow"), Difficulty.EASY, 5));
        allQuestions.add(new SentenceQuestion("A cat says ___.", "meow", Arrays.asList("woof", "moo", "oink"), Difficulty.EASY, 5));
        allQuestions.add(new SentenceQuestion("We use a ___ to cut paper.", "scissors", Arrays.asList("stone", "spoon", "fork"), Difficulty.EASY, 5));
        allQuestions.add(new SentenceQuestion("You write with a ___.", "pen", Arrays.asList("spoon", "car", "shoe"), Difficulty.EASY, 5));
        allQuestions.add(new SentenceQuestion("I wear ___ on my feet.", "shoes", Arrays.asList("hat", "gloves", "glasses"), Difficulty.EASY, 5));
        allQuestions.add(new SentenceQuestion("I wear a ___ on my head.", "hat", Arrays.asList("shoe", "sock", "belt"), Difficulty.EASY, 5));
        allQuestions.add(new SentenceQuestion("Fish live in the ___.", "water", Arrays.asList("air", "fire", "tree"), Difficulty.EASY, 5));
        allQuestions.add(new SentenceQuestion("Birds can ___.", "fly", Arrays.asList("swim", "drive", "cook"), Difficulty.EASY, 5));
        allQuestions.add(new SentenceQuestion("A car has four ___.", "wheels", Arrays.asList("legs", "eyes", "ears"), Difficulty.EASY, 5));
        allQuestions.add(new SentenceQuestion("We sleep in a ___.", "bed", Arrays.asList("kitchen", "bathroom", "table"), Difficulty.EASY, 5));
        allQuestions.add(new SentenceQuestion("I brush my ___ every morning.", "teeth", Arrays.asList("ears", "knees", "elbows"), Difficulty.EASY, 5));
        allQuestions.add(new SentenceQuestion("A lemon is ___.", "sour", Arrays.asList("sweet", "salty", "spicy"), Difficulty.EASY, 5));
        allQuestions.add(new SentenceQuestion("Sugar is ___.", "sweet", Arrays.asList("sour", "bitter", "hot"), Difficulty.EASY, 5));
        allQuestions.add(new SentenceQuestion("Fire is ___.", "hot", Arrays.asList("cold", "wet", "soft"), Difficulty.EASY, 5));
        allQuestions.add(new SentenceQuestion("Ice is ___.", "cold", Arrays.asList("hot", "warm", "burning"), Difficulty.EASY, 5));
        allQuestions.add(new SentenceQuestion("There are seven days in a ___.", "week", Arrays.asList("year", "month", "hour"), Difficulty.EASY, 5));
        allQuestions.add(new SentenceQuestion("There are twelve months in a ___.", "year", Arrays.asList("week", "day", "minute"), Difficulty.EASY, 5));
        allQuestions.add(new SentenceQuestion("My mother's sister is my ___.", "aunt", Arrays.asList("uncle", "brother", "father"), Difficulty.EASY, 5));
        allQuestions.add(new SentenceQuestion("My father's father is my ___.", "grandfather", Arrays.asList("grandmother", "sister", "son"), Difficulty.EASY, 5));


        // =================================================================
        // MEDIUM SORULAR (10 XP) - (B1-B2 Gramer & Meslekler & İlişkiler)
        // =================================================================

        // --- Gramer ---
        allQuestions.add(new SentenceQuestion("I have been ___ English for two years.", "studying", Arrays.asList("study", "studied", "studies"), Difficulty.MEDIUM, 10));
        allQuestions.add(new SentenceQuestion("If it ___ tomorrow, we will stay at home.", "rains", Arrays.asList("rain", "rained", "is raining"), Difficulty.MEDIUM, 10));
        allQuestions.add(new SentenceQuestion("He suggested ___ to the cinema.", "going", Arrays.asList("to go", "go", "gone"), Difficulty.MEDIUM, 10));
        allQuestions.add(new SentenceQuestion("She is interested ___ learning Spanish.", "in", Arrays.asList("on", "at", "with"), Difficulty.MEDIUM, 10));
        allQuestions.add(new SentenceQuestion("I have ___ finished my homework.", "just", Arrays.asList("yet", "ever", "since"), Difficulty.MEDIUM, 10));
        allQuestions.add(new SentenceQuestion("You ___ smoke here. It is forbidden.", "mustn't", Arrays.asList("don't have to", "needn't", "couldn't"), Difficulty.MEDIUM, 10));
        allQuestions.add(new SentenceQuestion("This car was ___ in Germany.", "made", Arrays.asList("make", "making", "makes"), Difficulty.MEDIUM, 10));
        allQuestions.add(new SentenceQuestion("While I ___ TV, the electricity went out.", "was watching", Arrays.asList("watched", "am watching", "watch"), Difficulty.MEDIUM, 10));
        allQuestions.add(new SentenceQuestion("I am looking ___ to the weekend.", "forward", Arrays.asList("for", "after", "up"), Difficulty.MEDIUM, 10));
        allQuestions.add(new SentenceQuestion("He is good ___ playing chess.", "at", Arrays.asList("in", "on", "with"), Difficulty.MEDIUM, 10));
        allQuestions.add(new SentenceQuestion("We enjoyed ___ the movie.", "watching", Arrays.asList("to watch", "watch", "watched"), Difficulty.MEDIUM, 10));
        allQuestions.add(new SentenceQuestion("I decided ___ a new car.", "to buy", Arrays.asList("buying", "buy", "bought"), Difficulty.MEDIUM, 10));
        allQuestions.add(new SentenceQuestion("This is the boy ___ broke the window.", "who", Arrays.asList("which", "where", "whose"), Difficulty.MEDIUM, 10));
        allQuestions.add(new SentenceQuestion("This is the house ___ I was born.", "where", Arrays.asList("which", "who", "when"), Difficulty.MEDIUM, 10));
        allQuestions.add(new SentenceQuestion("I haven't seen him ___ 2010.", "since", Arrays.asList("for", "ago", "before"), Difficulty.MEDIUM, 10));
        allQuestions.add(new SentenceQuestion("I haven't seen him ___ ten years.", "for", Arrays.asList("since", "ago", "past"), Difficulty.MEDIUM, 10));

        // --- Mantık & Kelime (Meslekler, Yerler, Eşyalar) ---
        allQuestions.add(new SentenceQuestion("A doctor works in a ___.", "hospital", Arrays.asList("school", "park", "restaurant"), Difficulty.MEDIUM, 10));
        allQuestions.add(new SentenceQuestion("A teacher works in a ___.", "school", Arrays.asList("hospital", "bank", "factory"), Difficulty.MEDIUM, 10));
        allQuestions.add(new SentenceQuestion("A baker makes ___.", "bread", Arrays.asList("shoes", "cars", "furniture"), Difficulty.MEDIUM, 10));
        allQuestions.add(new SentenceQuestion("A mechanic repairs ___.", "cars", Arrays.asList("people", "teeth", "food"), Difficulty.MEDIUM, 10));
        allQuestions.add(new SentenceQuestion("Before you eat, you should wash your ___.", "hands", Arrays.asList("car", "hair", "shoes"), Difficulty.MEDIUM, 10));
        allQuestions.add(new SentenceQuestion("The opposite of 'Expensive' is ___.", "Cheap", Arrays.asList("Rich", "Big", "Small"), Difficulty.MEDIUM, 10));
        allQuestions.add(new SentenceQuestion("The opposite of 'Heavy' is ___.", "Light", Arrays.asList("Dark", "Hard", "Soft"), Difficulty.MEDIUM, 10));
        allQuestions.add(new SentenceQuestion("You need a ___ to drive a car.", "license", Arrays.asList("ticket", "passport", "diploma"), Difficulty.MEDIUM, 10));
        allQuestions.add(new SentenceQuestion("If you are thirsty, you should drink ___.", "water", Arrays.asList("bread", "salt", "cake"), Difficulty.MEDIUM, 10));
        allQuestions.add(new SentenceQuestion("You go to the library to ___ books.", "read", Arrays.asList("buy", "eat", "cook"), Difficulty.MEDIUM, 10));
        allQuestions.add(new SentenceQuestion("I keep my money in my ___.", "wallet", Arrays.asList("shoe", "hat", "book"), Difficulty.MEDIUM, 10));
        allQuestions.add(new SentenceQuestion("An umbrella protects you from ___.", "rain", Arrays.asList("wind", "snow", "sun"), Difficulty.MEDIUM, 10));
        allQuestions.add(new SentenceQuestion("The month after January is ___.", "February", Arrays.asList("March", "December", "April"), Difficulty.MEDIUM, 10));
        allQuestions.add(new SentenceQuestion("A kangaroo is famous for ___.", "jumping", Arrays.asList("flying", "swimming", "singing"), Difficulty.MEDIUM, 10));
        allQuestions.add(new SentenceQuestion("Sushi is a traditional food from ___.", "Japan", Arrays.asList("Italy", "Mexico", "France"), Difficulty.MEDIUM, 10));
        allQuestions.add(new SentenceQuestion("Pizza is originally from ___.", "Italy", Arrays.asList("China", "USA", "Turkey"), Difficulty.MEDIUM, 10));


        // =================================================================
        // HARD SORULAR (15 XP) - (C1 Gramer, Deyimler & Karmaşık Mantık)
        // =================================================================

        // --- İleri Gramer ---
        allQuestions.add(new SentenceQuestion("If I had known, I ___ you.", "would have called", Arrays.asList("would call", "will call", "called"), Difficulty.HARD, 15));
        allQuestions.add(new SentenceQuestion("The man ___ car was stolen is here.", "whose", Arrays.asList("who", "whom", "which"), Difficulty.HARD, 15));
        allQuestions.add(new SentenceQuestion("No sooner had I arrived ___ the phone rang.", "than", Arrays.asList("when", "then", "that"), Difficulty.HARD, 15));
        allQuestions.add(new SentenceQuestion("Little ___ he know the truth.", "did", Arrays.asList("does", "was", "had"), Difficulty.HARD, 15));
        allQuestions.add(new SentenceQuestion("She is accused ___ stealing the money.", "of", Arrays.asList("for", "with", "on"), Difficulty.HARD, 15));
        allQuestions.add(new SentenceQuestion("By next year, I ___ graduated from university.", "will have", Arrays.asList("will be", "have", "had"), Difficulty.HARD, 15));
        allQuestions.add(new SentenceQuestion("___ it rains, we will cancel the picnic.", "Unless", Arrays.asList("If", "When", "As long as"), Difficulty.HARD, 15));
        allQuestions.add(new SentenceQuestion("I'd rather you ___ now.", "went", Arrays.asList("go", "going", "will go"), Difficulty.HARD, 15));
        allQuestions.add(new SentenceQuestion("It's high time we ___ home.", "went", Arrays.asList("go", "going", "will go"), Difficulty.HARD, 15));
        allQuestions.add(new SentenceQuestion("Never ___ such a beautiful sunset.", "have I seen", Arrays.asList("I have seen", "I saw", "seen I have"), Difficulty.HARD, 15));
        allQuestions.add(new SentenceQuestion("Despite ___ tired, he kept working.", "being", Arrays.asList("be", "is", "was"), Difficulty.HARD, 15));
        allQuestions.add(new SentenceQuestion("He acts as if he ___ the boss.", "were", Arrays.asList("is", "was", "be"), Difficulty.HARD, 15));

        // --- Deyimler (Idioms) & Phrasal Verbs ---
        allQuestions.add(new SentenceQuestion("To 'let the cat out of the bag' means to reveal a ___.", "secret", Arrays.asList("animal", "gift", "problem"), Difficulty.HARD, 15));
        allQuestions.add(new SentenceQuestion("It's raining cats and ___.", "dogs", Arrays.asList("cows", "birds", "frogs"), Difficulty.HARD, 15));
        allQuestions.add(new SentenceQuestion("Please ___ your shoes before entering.", "take off", Arrays.asList("put on", "get up", "look for"), Difficulty.HARD, 15));
        allQuestions.add(new SentenceQuestion("I ran ___ of sugar, so I can't make a cake.", "out", Arrays.asList("away", "off", "in"), Difficulty.HARD, 15));
        allQuestions.add(new SentenceQuestion("Can you ___ the music? It's too loud.", "turn down", Arrays.asList("turn up", "turn on", "turn in"), Difficulty.HARD, 15));
        allQuestions.add(new SentenceQuestion("To 'break a leg' means ___.", "good luck", Arrays.asList("get hurt", "give up", "run fast"), Difficulty.HARD, 15));
        allQuestions.add(new SentenceQuestion("A 'piece of cake' means something very ___.", "easy", Arrays.asList("tasty", "hard", "expensive"), Difficulty.HARD, 15));

        // --- Zor Mantık / Genel Kültür ---
        allQuestions.add(new SentenceQuestion("The sun rises in the ___.", "East", Arrays.asList("West", "North", "South"), Difficulty.HARD, 15));
        allQuestions.add(new SentenceQuestion("An architect is someone who ___ buildings.", "designs", Arrays.asList("destroys", "paints", "cleans"), Difficulty.HARD, 15));
        allQuestions.add(new SentenceQuestion("The capital city of France is ___.", "Paris", Arrays.asList("London", "Berlin", "Madrid"), Difficulty.HARD, 15));
        allQuestions.add(new SentenceQuestion("The chemical symbol for water is ___.", "H2O", Arrays.asList("CO2", "O2", "NaCl"), Difficulty.HARD, 15));
        allQuestions.add(new SentenceQuestion("A thermometer measures ___.", "temperature", Arrays.asList("distance", "weight", "speed"), Difficulty.HARD, 15));
        allQuestions.add(new SentenceQuestion("The largest ocean in the world is the ___ Ocean.", "Pacific", Arrays.asList("Atlantic", "Indian", "Arctic"), Difficulty.HARD, 15));
        allQuestions.add(new SentenceQuestion("If you break a mirror, it brings bad ___ (Superstition).", "luck", Arrays.asList("mood", "weather", "health"), Difficulty.HARD, 15));
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