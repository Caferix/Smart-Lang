package com.scu.smartlang.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.scu.smartlang.notifications.AlarmScheduler;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // Sistem başladığında alarm yeniden kur
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            AlarmScheduler.scheduleNext(context);
        }
    }
}
