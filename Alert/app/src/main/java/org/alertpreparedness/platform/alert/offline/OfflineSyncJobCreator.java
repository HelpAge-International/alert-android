package org.alertpreparedness.platform.alert.offline;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobCreator;
import com.google.common.collect.Lists;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import org.alertpreparedness.platform.alert.dagger.DependencyInjector;
import org.alertpreparedness.platform.alert.dagger.annotation.ActionGroupObservable;
import org.alertpreparedness.platform.alert.dagger.annotation.AlertGroupObservable;
import org.alertpreparedness.platform.alert.dagger.annotation.BaseLogRef;
import org.alertpreparedness.platform.alert.dagger.annotation.HazardGroupObservable;
import org.alertpreparedness.platform.alert.dagger.annotation.IndicatorGroupObservable;
import org.alertpreparedness.platform.alert.firebase.wrappers.ActionItemWrapper;
import org.alertpreparedness.platform.alert.helper.UserInfo;
import org.alertpreparedness.platform.alert.utils.AppUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

import durdinapps.rxfirebase2.RxFirebaseDatabase;
import io.reactivex.Flowable;
import timber.log.Timber;


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
