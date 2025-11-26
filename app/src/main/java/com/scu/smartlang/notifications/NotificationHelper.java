package com.scu.smartlang.notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import com.scu.smartlang.R;
import com.scu.smartlang.presentation.ui.home.MainActivity;

import java.util.Random;

public class NotificationHelper {
    public static final String CHANNEL_ID = "reminder_channel";
    private static final int NOTIFICATION_ID = 1001;

    //  Rastgele başlıklar
    private static final String[] TITLES = {
            "📚 Kelime Zamanı!",
            "✨ Öğrenme Vakti!",
            "🎯 Hedefine Bir Adım Daha Yakın!",
            "🌟 Bugünün Kelimeleri Seni Bekliyor!",
            "🔥 Streak'ini Devam Ettir!",
            "Hadi seni bekliyoruz!",
            "Pişşşşşşşttt!",
            "⭐ Yeni Kelimeler Öğrenmeye Hazır mısın?"
    };

    //  Rastgele motivasyon mesajları
    private static final String[] MESSAGES = {
            "5 dakikan var mı? Bugünkü kelimeleri öğren!",
            "Biraz kelime çalışmaya ne dersin knk?",
            "Her gün biraz ilerlersen, büyük fark yaratırsın!",
            "Kelime hazinenizi genişletme zamanı 📖",
            "Dil öğrenme yolculuğunda bir adım daha!",
            "Başarı, küçük adımların toplamıdır 💪",
            "Bugün de bir şeyler öğrenmeye ne dersin?",
            "Hedeflerine ulaşmak için sabırlı ol ve devam et!",
            "Her kelime, yeni bir kapı açar 🚪",
            "Öğrenmeye devam et, ilerlemene şaşıracaksın!"
    };

    //  Bildirim kanalı oluştur
    public static void createChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Ses ayarları
            Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();

            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Hatırlatıcılar",
                    NotificationManager.IMPORTANCE_HIGH // Ses + ekran başlığı
            );
            channel.setDescription("Kelime öğrenme hatırlatmaları");
            channel.enableLights(true);
            channel.setLightColor(Color.parseColor("#4CAF50")); // Yeşi LED ışık
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{0, 300, 200, 300}); // Özel titreşim
            channel.setSound(soundUri, audioAttributes);
            channel.setShowBadge(true); // Uygulama ikonunda badge göster

            NotificationManager manager = context.getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    //  Renkli ve çeşitlendirilmiş bildirim göster
    public static void showReminder(Context context, String customTitle, String customMessage) {
        // Android 13+ için bildirim izni kontrolü
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (context.checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS)
                    != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }

        // Rastgele başlık ve mesaj seç
        Random random = new Random();
        String title = (customTitle != null && !customTitle.isEmpty())
                ? customTitle
                : TITLES[random.nextInt(TITLES.length)];
        String message = (customMessage != null && !customMessage.isEmpty())
                ? customMessage
                : MESSAGES[random.nextInt(MESSAGES.length)];

        // Ana uygulamaya giden intent
        Intent mainIntent = new Intent(context, MainActivity.class);
        mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent mainPendingIntent = PendingIntent.getActivity(
                context,
                0,
                mainIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Renkli ve genişletilmiş bildirim
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification_24dp) // Küçük bildirim ikonu
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(message + "\n\n💡 Günde 3 kelime öğrensen bir yılda..."))
                .setPriority(NotificationCompat.PRIORITY_HIGH) // Yüksek öncelik
                .setCategory(NotificationCompat.CATEGORY_REMINDER) // Hatırlatıcı kategorisi
                .setAutoCancel(true) // Tıklanınca otomatik kapansın
                .setContentIntent(mainPendingIntent) // Tıklanınca uygulamayı aç
                .setColor(Color.parseColor("#4CAF50")) // yeşil vurgu rengi
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC) // Kilit ekranında görünsün
                .setDefaults(NotificationCompat.DEFAULT_ALL); // Varsayılan ses/titreşim

        // Ses ve titreşim (Android 7.1 ve altı için)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
            builder.setVibrate(new long[]{0, 300, 200, 300});
            builder.setLights(Color.parseColor("#4CAF50"), 1000, 2000);
        }

        // Bildirimi göster
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }
}
