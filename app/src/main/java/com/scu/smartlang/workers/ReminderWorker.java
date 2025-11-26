package com.scu.smartlang.workers;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.hilt.work.HiltWorker;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import dagger.assisted.Assisted;
import dagger.assisted.AssistedInject;

@HiltWorker // 👈 BU ÇOK ÖNEMLİ
public class ReminderWorker extends Worker {

    @AssistedInject // 👈 BU DA ÇOK ÖNEMLİ
    public ReminderWorker(@Assisted @NonNull Context context, @Assisted @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        // İşlemlerin...
        return Result.success();
    }
}
