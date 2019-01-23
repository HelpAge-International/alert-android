package org.alertpreparedness.platform.v1.offline;


import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

import org.alertpreparedness.platform.v1.AlertApplication;

public class SyncJobService extends JobService {
    @Override
    public boolean onStartJob(JobParameters job) {
        // Do some work here
        System.out.println("Offline SyncJobService Called");

        OfflineSyncHandler.getInstance().sync((AlertApplication) getApplication(), () -> {});

        return false; // Answers the question: "Is there still work going on?"
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        return false; // Answers the question: "Should this job be retried?"
    }
}
