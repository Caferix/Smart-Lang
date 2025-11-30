package com.scu.smartlang.domain.repository;

import com.scu.smartlang.domain.model.Word;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface WordRepository {
    CompletableFuture<List<Word>> getAllWords();
    CompletableFuture<Word> getWordById(String id);
    CompletableFuture<Void> addWord(Word word);
    CompletableFuture<Void> updateWord(Word word);
    CompletableFuture<Void> deleteWord(String id);
}
