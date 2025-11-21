package com.scu.smartlang.workers;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import com.scu.smartlang.notifications.AlarmScheduler;

public class ReminderWorker extends Worker {

    public ReminderWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        // Alarmın doğru kurulduğundan emin ol
        AlarmScheduler.scheduleNext(getApplicationContext());
        return Result.success();
    }
}
