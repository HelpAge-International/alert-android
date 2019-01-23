package org.alertpreparedness.platform.v1.offline;

import com.google.common.collect.Lists;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import org.alertpreparedness.platform.v1.AlertApplication;
import org.alertpreparedness.platform.v1.dagger.DependencyInjector;
import org.alertpreparedness.platform.v1.dagger.annotation.ActionGroupObservable;
import org.alertpreparedness.platform.v1.dagger.annotation.AlertGroupObservable;
import org.alertpreparedness.platform.v1.dagger.annotation.BaseLogRef;
import org.alertpreparedness.platform.v1.dagger.annotation.HazardGroupObservable;
import org.alertpreparedness.platform.v1.dagger.annotation.IndicatorGroupObservable;
import org.alertpreparedness.platform.v1.firebase.wrappers.ActionItemWrapper;
import org.alertpreparedness.platform.v1.utils.AppUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

import durdinapps.rxfirebase2.RxFirebaseDatabase;
import io.reactivex.Flowable;
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
    @HazardGroupObservable
    public Flowable<Collection<DataSnapshot>> responsePlanGroupObservable;

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

    public void sync(AlertApplication alertApplication, SyncCallback syncCallback) {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
//            if(DependencyInjector.userScopeComponent() == null){
//                DependencyInjector.initialize(alertApplication);
//            }
            DependencyInjector.userScopeComponent().inject(this);
        } else {
            syncCallback.syncSuccess();
            return;
        }
        Timber.d("SYNCING....");
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

         Flowable.combineLatest(alertGroupObservable, actionGroupObservable, indicatorGroupObservable, hazardGroupObservable, responsePlanGroupObservable, indicatorLogsFlow, (alertGroup, actionGroup, indicatorGroup, hazardGroup, responsePlanGroup, indicatorLogs) -> true)
                 .firstElement()
                 .subscribe(b -> {
                     Timber.d("Synchronised!");
                     DatabaseReference.goOffline();
                     syncCallback.syncSuccess();
                 }, e-> {});

    }

    public interface SyncCallback{
        void syncSuccess();
    }

}
