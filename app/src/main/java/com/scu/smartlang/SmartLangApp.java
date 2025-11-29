package com.scu.smartlang;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatDelegate; // Eklendi
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.google.firebase.firestore.FirebaseFirestoreException;
import com.scu.smartlang.notifications.NotificationHelper;
import com.scu.smartlang.workers.ReminderWorker;
import dagger.hilt.android.HiltAndroidApp;
import io.reactivex.rxjava3.plugins.RxJavaPlugins;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@HiltAndroidApp
public class SmartLangApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Prevent crashes for errors sent to disposed subscribers
        RxJavaPlugins.setErrorHandler(e -> {
            if (e instanceof io.reactivex.rxjava3.exceptions.UndeliverableException) {
                e = e.getCause();
            }
            if ((e instanceof IOException) || (e instanceof java.net.SocketException)) {
                // fine, irrelevant network problem or API that throws on cancellation
                return;
            }
            if (e instanceof InterruptedException) {
                // fine, some blocking code was interrupted by a dispose call
                return;
            }
            if ((e instanceof NullPointerException) || (e instanceof IllegalArgumentException)) {
                // that's likely a bug in the application
                Thread.currentThread().getUncaughtExceptionHandler()
                        .uncaughtException(Thread.currentThread(), e);
                return;
            }
            if (e instanceof IllegalStateException) {
                // that's a bug in RxJava or in a custom operator
                Thread.currentThread().getUncaughtExceptionHandler()
                        .uncaughtException(Thread.currentThread(), e);
                return;
            }

            // Log the permission error specifically so you see it in Logcat without crashing
            if (e instanceof FirebaseFirestoreException) {
                android.util.Log.w("RxErrorHandler", "Undeliverable Firebase Exception: " + e.getMessage());
                return;
            }

            android.util.Log.w("RxErrorHandler", "Undeliverable exception received, not sure what to do", e);
        });

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