package com.scu.smartlang;

import android.app.Application;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatDelegate;
import dagger.hilt.android.HiltAndroidApp;

@HiltAndroidApp
public class SmartLangApp extends Application {

    private static final String PREFS_NAME = "SmartLangPrefs";
    private static final String KEY_DARK_MODE = "dark_mode_enabled";

    @Override
    public void onCreate() {
        super.onCreate();
        // Uygulama ilk açıldığında temayı yükle
        applySavedTheme();
    }

    private void applySavedTheme() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        // Kayıtlı tercih var mı?
        if (prefs.contains(KEY_DARK_MODE)) {
            boolean isDarkMode = prefs.getBoolean(KEY_DARK_MODE, false);

            // Kaydedilen ayarı uygula
            int themeMode = isDarkMode ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO;
            AppCompatDelegate.setDefaultNightMode(themeMode);
        } else {
            // Kayıt yoksa sistem ayarını takip et
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        }
    }
}