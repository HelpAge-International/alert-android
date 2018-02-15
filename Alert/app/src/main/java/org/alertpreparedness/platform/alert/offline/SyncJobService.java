package org.alertpreparedness.platform.alert.offline;


import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

import timber.log.Timber;

public class SyncJobService extends JobService {
    @Override
    public boolean onStartJob(JobParameters job) {
        // Do some work here

        OfflineSyncHandler.getInstance().sync();

        return false; // Answers the question: "Is there still work going on?"
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        return false; // Answers the question: "Should this job be retried?"
    }
}
