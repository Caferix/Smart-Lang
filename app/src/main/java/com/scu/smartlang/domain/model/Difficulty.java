package com.scu.smartlang.domain.model;

// En İyi Pratik: Zorluk seviyesini String yerine Enum yapıyoruz.
// Böylece yazım hatası (örn: "Hard" yerine "hard") riski kalkar.
public enum Difficulty {
    EASY, MEDIUM, HARD
}
