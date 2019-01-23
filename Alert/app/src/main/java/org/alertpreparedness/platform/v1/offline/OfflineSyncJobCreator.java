package org.alertpreparedness.platform.v1.offline;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobCreator;


public class OfflineSyncJobCreator implements JobCreator{

    @Override
    @Nullable
    public Job create(@NonNull String tag) {
        switch (tag) {
            case OfflineSyncJob.TAG:
                return new OfflineSyncJob();
            default:
                return null;
        }
    }
}
