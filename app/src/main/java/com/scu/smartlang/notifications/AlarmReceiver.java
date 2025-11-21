package com.scu.smartlang.notifications;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import java.util.List;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // Uygulama açıksa bildirim gösterme
        if (isAppInForeground(context)) {
            // Sonraki alarmı planla ama bildirim gösterme
            AlarmScheduler.scheduleNext(context);
            return;
        }

        // Kanal kontrolü
        NotificationHelper.createChannel(context);

        // Rastgele bildirim göster
        NotificationHelper.showReminder(context, null, null);

        // Sonraki alarmı planla
        AlarmScheduler.scheduleNext(context);
    }

    // Uygulama ön planda mı kontrol et
    private boolean isAppInForeground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager == null) return false;

        List<ActivityManager.RunningAppProcessInfo> processes = activityManager.getRunningAppProcesses();
        if (processes == null) return false;

        String packageName = context.getPackageName();
        for (ActivityManager.RunningAppProcessInfo processInfo : processes) {
            if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND &&
                    processInfo.processName.equals(packageName)) {
                return true;
            }
        }
        return false;
    }
}
