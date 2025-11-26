package com.scu.smartlang;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatDelegate; // Eklendi
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import com.scu.smartlang.notifications.NotificationHelper;
import com.scu.smartlang.workers.ReminderWorker;
import dagger.hilt.android.HiltAndroidApp;

import java.util.concurrent.TimeUnit;

@HiltAndroidApp
public class SmartLangApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // TEMA AYARINI UYGULA
        applyThemePreference();

        // Bildirim kanalını oluştur (Android 8.0+)
        NotificationHelper.createChannel(this);

        // WorkManager'ı başlat (her 15 dakikada bir alarm kontrolü)
        PeriodicWorkRequest reminderWork = new PeriodicWorkRequest.Builder(
                ReminderWorker.class,
                15, // Her 15 dakikada bir
                TimeUnit.MINUTES
        ).build();

        WorkManager.getInstance(this).enqueue(reminderWork);
    }

    private void applyThemePreference() {
        SharedPreferences prefs = getSharedPreferences("SmartLangPrefs", Context.MODE_PRIVATE);
        boolean isDarkMode = prefs.getBoolean("dark_mode_enabled", false);

        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }
}