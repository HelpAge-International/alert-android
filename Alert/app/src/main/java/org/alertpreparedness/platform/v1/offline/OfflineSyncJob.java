package org.alertpreparedness.platform.v1.offline;

import androidx.annotation.NonNull;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobRequest;

import org.alertpreparedness.platform.v1.AlertApplication;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;


public class OfflineSyncJob extends Job {

    public static final String TAG = "OfflineSyncJob";

    @NonNull
    @Override
    protected Result onRunJob(@NonNull Params params) {
        final CountDownLatch countDownLatch = new CountDownLatch(1);

        OfflineSyncHandler.getInstance().sync((AlertApplication) getContext().getApplicationContext(), countDownLatch::countDown);

        try {
            countDownLatch.await();
        } catch (InterruptedException ignored) {
        }

        return Result.SUCCESS;
    }

    public static void scheduleJob() {
        new JobRequest.Builder(OfflineSyncJob.TAG)
                .setPeriodic(TimeUnit.MINUTES.toMillis(15))
                .setRequiredNetworkType(JobRequest.NetworkType.CONNECTED)
                .setUpdateCurrent(true)
                .build()
                .schedule();
    }
}
