package com.scu.smartlang.data.mapper;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.scu.smartlang.domain.model.Word;

import java.lang.reflect.Type;
import java.util.List;

public class WordJsonMapper {

    private final Gson gson;

    public WordJsonMapper() {
        this.gson = new Gson();
    }

    // Convert a single Word object to JSON
    public String toJson(Word word) {
        return gson.toJson(word);
    }

    // Convert a list of Word objects to JSON
    public String toJson(List<Word> words) {
        return gson.toJson(words);
    }

    // Convert JSON to a single Word object
    public Word fromJson(String json) {
        return gson.fromJson(json, Word.class);
    }

    // Convert JSON to a list of Word objects
    public List<Word> fromJsonToList(String json) {
        Type listType = new TypeToken<List<Word>>() {}.getType();
        return gson.fromJson(json, listType);
    }
}
