package com.scu.smartlang;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatDelegate; // EKLENDİ
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

        // 1️⃣ TEMA AYARINI UYGULA (YENİ EKLENEN KISIM)
        applyThemePreference();

        // 2️⃣ Bildirim kanalını oluştur (Android 8.0+)
        NotificationHelper.createChannel(this);

        // 3️⃣ WorkManager'ı başlat (her 15 dakikada bir alarm kontrolü)
        PeriodicWorkRequest reminderWork = new PeriodicWorkRequest.Builder(
                ReminderWorker.class,
                15, // Her 15 dakikada bir
                TimeUnit.MINUTES
        ).build();

        WorkManager.getInstance(this).enqueue(reminderWork);
    }

    // Kaydedilen tercihe göre temayı açılışta zorla
    private void applyThemePreference() {
        SharedPreferences prefs = getSharedPreferences("SmartLangPrefs", Context.MODE_PRIVATE);
        boolean isDarkMode = prefs.getBoolean("dark_mode_enabled", false);

        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            // Eğer kapalıysa sistem temasını değil, zorla aydınlık temayı kullan
            // (Çünkü SettingsFragment'taki switch yapısı On/Off şeklinde)
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }
}