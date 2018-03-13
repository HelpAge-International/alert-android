package org.alertpreparedness.platform.alert.offline;

import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.alertpreparedness.platform.alert.dagger.DependencyInjector;
import org.alertpreparedness.platform.alert.dagger.annotation.ActionGroupObservable;
import org.alertpreparedness.platform.alert.dagger.annotation.AgencyRef;
import org.alertpreparedness.platform.alert.dagger.annotation.AlertGroupObservable;
import org.alertpreparedness.platform.alert.dagger.annotation.AlertObservable;
import org.alertpreparedness.platform.alert.dagger.annotation.AlertRef;
import org.alertpreparedness.platform.alert.dagger.annotation.BaseActionRef;
import org.alertpreparedness.platform.alert.dagger.annotation.BaseAlertRef;
import org.alertpreparedness.platform.alert.dagger.annotation.BaseIndicatorRef;
import org.alertpreparedness.platform.alert.dagger.annotation.BaseLogRef;
import org.alertpreparedness.platform.alert.dagger.annotation.HazardGroupObservable;
import org.alertpreparedness.platform.alert.dagger.annotation.IndicatorGroupObservable;
import org.alertpreparedness.platform.alert.dagger.annotation.IndicatorRef;
import org.alertpreparedness.platform.alert.firebase.IndicatorModel;
import org.alertpreparedness.platform.alert.firebase.data_fetchers.IndicatorsFetcher;
import org.alertpreparedness.platform.alert.firebase.wrappers.ActionItemWrapper;
import org.alertpreparedness.platform.alert.helper.UserInfo;
import org.alertpreparedness.platform.alert.model.User;
import org.alertpreparedness.platform.alert.utils.AppUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import durdinapps.rxfirebase2.RxFirebaseDatabase;
import io.reactivex.Flowable;
import io.reactivex.disposables.Disposable;
import timber.log.Timber;


public class OfflineSyncHandler {

    @Inject
    @AlertGroupObservable
    Flowable<Collection<DataSnapshot>> alertGroupObservable;

    @Inject
    @ActionGroupObservable
    Flowable<Collection<ActionItemWrapper>> actionGroupObservable;


    @Inject
    @IndicatorGroupObservable
    public Flowable<Collection<DataSnapshot>> indicatorGroupObservable;


    @Inject
    @HazardGroupObservable
    public Flowable<Collection<DataSnapshot>> hazardGroupObservable;


    @Inject
    @BaseLogRef
    public DatabaseReference baseLogRef;


    private static OfflineSyncHandler instance;

    private OfflineSyncHandler() {
    }

    public static OfflineSyncHandler getInstance() {
        if (instance == null) {
            instance = new OfflineSyncHandler();
        }

        return instance;
    }

    public void sync() {

        if (alertGroupObservable == null) {
            if (new UserInfo().getUser() != null) {
                DependencyInjector.applicationComponent().inject(this);
            } else {
                return;
            }
        }
        Timber.d("SYNCING....");

        // TODO: Figure out if it's worthwhile going online and offline
         DatabaseReference.goOnline();

         Flowable<Collection<DataSnapshot>> indicatorLogsFlow = indicatorGroupObservable.flatMap(dataSnapshots -> {
             List<Flowable<List<DataSnapshot>>> flowables = new ArrayList<>();

             for(DataSnapshot dataSnapshot : dataSnapshots){
                 flowables.add(
                         RxFirebaseDatabase.observeValueEvent(baseLogRef.child(dataSnapshot.getKey())).map(parentLogSnapshot -> Lists.newArrayList(parentLogSnapshot.getChildren()))
                 );
             }

             return flowables.size() == 0 ? Flowable.just(new ArrayList<>()) : Flowable.combineLatest(flowables, AppUtils::combineDataSnapshotList);
         });


         Flowable.combineLatest(alertGroupObservable, actionGroupObservable, indicatorGroupObservable, hazardGroupObservable, indicatorLogsFlow, (alertGroup, actionGroup, indicatorGroup, hazardGroup, indicatorLogs) -> true)
                 .firstElement()
                 .subscribe(b -> {
                     Timber.d("Synchronised!");
                     DatabaseReference.goOffline();
                 });

    }

}
