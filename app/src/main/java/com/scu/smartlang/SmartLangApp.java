package com.scu.smartlang;

import android.app.Application;
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
}
