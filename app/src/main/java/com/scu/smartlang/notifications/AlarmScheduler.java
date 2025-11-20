package com.scu.smartlang.notifications;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import java.util.Calendar;

public class AlarmScheduler {
    private static final String PREFS_NAME = "SmartLangPrefs";
    private static final int REQUEST_CODE = 2001;
    private static final int INTERVAL_MINUTES = 60; // Her saat tekrarla

    public static void scheduleNext(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        boolean enabled = prefs.getBoolean("notifications_enabled", true);
        if (!enabled) {
            cancel(context);
            return;
        }

        // Bugün çalışma yapıldı mı kontrol et
//        if (hasStudiedToday(context)) {
//            // Yarına kadar alarmı ertele
//            scheduleForTomorrow(context, prefs);
//            return;
//        }

        // Kullanıcı ayarlarını al
        int startHour = prefs.getInt("start_time_hour", 9);
        int startMinute = prefs.getInt("start_time_minute", 0);
        int endHour = prefs.getInt("end_time_hour", 20);
        int endMinute = prefs.getInt("end_time_minute", 0);

        // Bir sonraki alarm zamanını hesapla
        long triggerTime = calculateNextTrigger(startHour, startMinute, endHour, endMinute);

        // AlarmManager ile alarmı planla
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                REQUEST_CODE,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        if (alarmManager != null) {
            // Android 12+ için izin kontrolü
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
                } else {
                    // İzin yoksa kullanıcıyı yönlendir
                    android.util.Log.w("AlarmScheduler", "Exact alarm izni yok - ayarlara yönlendirin");
                }
            } else {
                // Android 11 ve altı için doğrudan kur
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
            }
        }
    }

    private static long calculateNextTrigger(int startH, int startM, int endH, int endM) {
        Calendar now = Calendar.getInstance();
        Calendar start = (Calendar) now.clone();
        start.set(Calendar.HOUR_OF_DAY, startH);
        start.set(Calendar.MINUTE, startM);
        start.set(Calendar.SECOND, 0);

        Calendar end = (Calendar) start.clone();
        end.set(Calendar.HOUR_OF_DAY, endH);
        end.set(Calendar.MINUTE, endM);

        // Eğer bitiş başlangıçtan önce ise (örn: 22:00 - 08:00), ertesi güne kaydır
        if (end.before(start)) {
            end.add(Calendar.DAY_OF_MONTH, 1);
        }

        // Şu anki saat başlangıçtan önce mi?
        if (now.before(start)) {
            return start.getTimeInMillis();
        }
        // Şu anki saat bitişten sonra mı?
        else if (now.after(end)) {
            start.add(Calendar.DAY_OF_MONTH, 1);
            return start.getTimeInMillis();
        }
        // Aralık içindeyiz, bir sonraki INTERVAL kadar ilerle
        else {
            now.add(Calendar.MINUTE, INTERVAL_MINUTES);
            if (now.after(end)) {
                start.add(Calendar.DAY_OF_MONTH, 1);
                return start.getTimeInMillis();
            }
            return now.getTimeInMillis();
        }
    }

    public static void cancel(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }
    }

    // 🆕 Bugün çalışma yapıldı mı kontrol et
    private static boolean hasStudiedToday(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        long lastStudyTimestamp = prefs.getLong("last_study_timestamp", 0);

        if (lastStudyTimestamp == 0) return false; // Hiç çalışma yok

        Calendar lastStudy = Calendar.getInstance();
        lastStudy.setTimeInMillis(lastStudyTimestamp);

        Calendar today = Calendar.getInstance();

        // Aynı gün mü kontrol et
        return lastStudy.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                lastStudy.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR);
    }

    // 🆕 Yarının başlangıç saatine alarm kur
    private static void scheduleForTomorrow(Context context, SharedPreferences prefs) {
        int startHour = prefs.getInt("start_time_hour", 9);
        int startMinute = prefs.getInt("start_time_minute", 0);

        Calendar tomorrow = Calendar.getInstance();
        tomorrow.add(Calendar.DAY_OF_MONTH, 1);
        tomorrow.set(Calendar.HOUR_OF_DAY, startHour);
        tomorrow.set(Calendar.MINUTE, startMinute);
        tomorrow.set(Calendar.SECOND, 0);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                REQUEST_CODE,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        if (alarmManager != null) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(
                            AlarmManager.RTC_WAKEUP,
                            tomorrow.getTimeInMillis(),
                            pendingIntent
                    );
                }
            } else {
                alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        tomorrow.getTimeInMillis(),
                        pendingIntent
                );
            }
        }
    }
}
