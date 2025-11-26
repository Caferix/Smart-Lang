package com.scu.smartlang;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.hilt.work.HiltWorkerFactory;
import androidx.work.Configuration;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import com.scu.smartlang.notifications.NotificationHelper;
import com.scu.smartlang.workers.ReminderWorker;
import dagger.hilt.android.HiltAndroidApp;

import javax.inject.Inject;
import java.util.concurrent.TimeUnit;

@HiltAndroidApp
public class SmartLangApp extends Application implements Configuration.Provider {

    @Inject
    HiltWorkerFactory workerFactory;

    @Override
    public void onCreate() {
        super.onCreate();

        // Bildirim kanalı oluştur
        NotificationHelper.createChannel(this);

        // WorkManager işlemleri
        // NOT: WorkManager.getInstance(this) çağrısı, getWorkManagerConfiguration() metodunu tetikler.
        // Bu nedenle önce WorkManager'ın doğru yapılandırıldığından emin olmalıyız.

        PeriodicWorkRequest reminderWork =
                new PeriodicWorkRequest.Builder(ReminderWorker.class, 15, TimeUnit.MINUTES)
                        .build();

        WorkManager.getInstance(this).enqueue(reminderWork);
    }

    @NonNull
    @Override
    public Configuration getWorkManagerConfiguration() {
        // .setWorkerFactory() metodu Hilt ile birlikte gelir.
        // Eğer bu satır hala kırmızı yanıyorsa build.gradle dosyanızda hilt-work kütüphanesi eksiktir.
        return new Configuration.Builder()
                .setWorkerFactory(workerFactory)
                .setMinimumLoggingLevel(android.util.Log.INFO)
                .build();
    }
}
