package com.scu.smartlang.data.repository;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.scu.smartlang.domain.model.Word;
import com.scu.smartlang.domain.repository.WordRepository;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class WordRepositoryImpl implements WordRepository {

    private static final String FILE_NAME = "words.json";
    private final Context context;
    private final Gson gson;

    public WordRepositoryImpl(Context context) {
        this.context = context;
        this.gson = new Gson();
    }

    private List<Word> loadWords() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(context.openFileInput(FILE_NAME)))) {
            Type listType = new TypeToken<List<Word>>() {}.getType();
            return gson.fromJson(reader, listType);
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    private void saveWords(List<Word> words) {
        try (FileOutputStream fos = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE)) {
            fos.write(gson.toJson(words).getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public CompletableFuture<List<Word>> getAllWords() {
        return CompletableFuture.supplyAsync(this::loadWords);
    }

    @Override
    public CompletableFuture<Word> getWordById(String id) {
        return CompletableFuture.supplyAsync(() -> {
            for (Word word : loadWords()) {
                if (word.getId().equals(id)) {
                    return word;
                }
            }
            return null;
        });
    }

    @Override
    public CompletableFuture<Void> addWord(Word word) {
        return CompletableFuture.runAsync(() -> {
            List<Word> words = loadWords();
            words.add(word);
            saveWords(words);
        });
    }

    @Override
    public CompletableFuture<Void> updateWord(Word word) {
        return CompletableFuture.runAsync(() -> {
            List<Word> words = loadWords();
            for (int i = 0; i < words.size(); i++) {
                if (words.get(i).getId().equals(word.getId())) {
                    words.set(i, word);
                    break;
                }
            }
            saveWords(words);
        });
    }

    @Override
    public CompletableFuture<Void> deleteWord(String id) {
        return CompletableFuture.runAsync(() -> {
            List<Word> words = loadWords();
            words.removeIf(word -> word.getId().equals(id));
            saveWords(words);
        });
    }
}
